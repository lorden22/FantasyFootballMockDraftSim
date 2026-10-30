import { useEffect, useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { Shuffle } from "lucide-react";
import { motion } from "motion/react";
import { Modal } from "../../components/ui/Modal";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { FormAlert } from "../auth/AuthLayout";
import { draftApi } from "../../api/endpoints";
import { ApiError, errorMessage } from "../../api/client";
import { useToast } from "../../components/ui/Toast";
import { ordinal } from "../../lib/format";
import { cn } from "../../lib/cn";

const PRESETS = [8, 10, 12, 14];
const TEAM_NAME_RE = /^[\p{L}\p{N} _\-'.&!]{1,40}$/u;

export function NewDraftModal({ open, onClose, defaultTeamName }: { open: boolean; onClose: () => void; defaultTeamName: string }) {
  const navigate = useNavigate();
  const toast = useToast();
  const [teamName, setTeamName] = useState(defaultTeamName);
  const [numTeams, setNumTeams] = useState(12);
  const [position, setPosition] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    if (open) {
      setError(null);
      setFieldErrors({});
    }
  }, [open]);

  useEffect(() => {
    if (position !== null && position > numTeams) setPosition(numTeams);
  }, [numTeams, position]);

  const trimmed = teamName.trim();
  const nameError = trimmed.length === 0 ? "Give your team a name" : !TEAM_NAME_RE.test(trimmed) ? "Use letters, numbers, spaces and . ' - & ! (max 40)" : null;

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    if (nameError) {
      setFieldErrors({ teamName: nameError });
      return;
    }
    setBusy(true);
    setError(null);
    try {
      const draft = await draftApi.create(trimmed, numTeams, position);
      toast.success("Draft created", `You're picking ${ordinal(draft.userSlot)} in a ${draft.numTeams}-team league.`);
      onClose();
      navigate(`/drafts/${draft.id}`);
    } catch (err) {
      if (err instanceof ApiError) setFieldErrors(err.fields);
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      locked={busy}
      title="New mock draft"
      description="Snake format · 15 rounds · PPR projections"
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={busy}>
            Cancel
          </Button>
          <Button type="submit" form="new-draft-form" loading={busy}>
            Enter draft room
          </Button>
        </>
      }
    >
      <form id="new-draft-form" onSubmit={submit} className="space-y-6" noValidate>
        {error && <FormAlert>{error}</FormAlert>}
        <Input
          label="Team name"
          value={teamName}
          maxLength={40}
          onChange={(e) => setTeamName(e.target.value)}
          error={fieldErrors.teamName}
          data-autofocus
          required
        />

        <fieldset>
          <div className="flex items-baseline justify-between">
            <legend className="text-sm font-medium text-fg-muted">League size</legend>
            <span className="text-sm font-bold tabular text-fg">{numTeams} teams</span>
          </div>
          <div className="mt-3 grid grid-cols-4 gap-2">
            {PRESETS.map((n) => (
              <button
                key={n}
                type="button"
                aria-pressed={numTeams === n}
                onClick={() => setNumTeams(n)}
                className={cn(
                  "relative h-10 rounded-xl border text-sm font-semibold transition",
                  numTeams === n ? "border-accent/60 text-accent" : "border-line text-fg-muted hover:border-line-strong hover:text-fg",
                )}
              >
                {numTeams === n && <motion.span layoutId="size-pill" className="absolute inset-0 rounded-xl bg-accent/10" />}
                <span className="relative">{n}</span>
              </button>
            ))}
          </div>
          <label className="mt-4 block">
            <span className="sr-only">League size (4 to 20 teams)</span>
            <input
              type="range"
              min={4}
              max={20}
              step={1}
              value={numTeams}
              onChange={(e) => setNumTeams(Number(e.target.value))}
              className="w-full accent-[var(--color-accent)]"
            />
          </label>
          <div className="flex justify-between text-[11px] text-fg-subtle tabular">
            <span>4</span>
            <span>20</span>
          </div>
          {fieldErrors.numTeams && <p className="mt-1 text-xs text-danger">{fieldErrors.numTeams}</p>}
        </fieldset>

        <fieldset>
          <legend className="text-sm font-medium text-fg-muted">Your draft slot</legend>
          <div className="mt-3 grid grid-cols-5 gap-2 sm:grid-cols-7">
            <button
              type="button"
              aria-pressed={position === null}
              onClick={() => setPosition(null)}
              className={cn(
                "col-span-2 flex h-10 items-center justify-center gap-1.5 rounded-xl border text-xs font-semibold transition",
                position === null ? "border-accent/60 bg-accent/10 text-accent" : "border-line text-fg-muted hover:border-line-strong",
              )}
            >
              <Shuffle className="size-3.5" aria-hidden /> Random
            </button>
            {Array.from({ length: numTeams }, (_, i) => i + 1).map((n) => (
              <button
                key={n}
                type="button"
                aria-pressed={position === n}
                aria-label={`Pick ${ordinal(n)}`}
                onClick={() => setPosition(n)}
                className={cn(
                  "h-10 rounded-xl border text-sm font-semibold tabular transition",
                  position === n ? "border-accent/60 bg-accent/10 text-accent" : "border-line text-fg-muted hover:border-line-strong hover:text-fg",
                )}
              >
                {n}
              </button>
            ))}
          </div>
          {fieldErrors.draftPosition && <p className="mt-1 text-xs text-danger">{fieldErrors.draftPosition}</p>}
        </fieldset>
      </form>
    </Modal>
  );
}
