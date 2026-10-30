import { AnimatePresence, motion } from "motion/react";
import { Bot, FastForward, Flag, Hand, MousePointerClick } from "lucide-react";
import type { DraftState, Player } from "../../api/types";
import type { DraftAction } from "./useDraftRoom";
import { Button } from "../../components/ui/Button";
import { PositionBadge } from "../../components/ui/Badge";
import { formatPoints, ordinal, pickLabel, plural } from "../../lib/format";
import { cn } from "../../lib/cn";

export function OnTheClock({
  draft,
  revealing,
  action,
  selected,
  autoSim,
  onAutoSimChange,
  onDraft,
  onAutoPick,
  onSimulate,
}: {
  draft: DraftState;
  revealing: boolean;
  action: DraftAction;
  selected: Player | null;
  autoSim: boolean;
  onAutoSimChange: (v: boolean) => void;
  onDraft: () => void;
  onAutoPick: () => void;
  onSimulate: () => void;
}) {
  const cp = draft.currentPick;
  const userTurn = !!cp?.isUser && !revealing;
  const busy = action !== null || revealing;
  const pct = draft.totalPicks ? (draft.picksMade / draft.totalPicks) * 100 : 0;

  return (
    <div className="space-y-5">
      <div
        className={cn(
          "relative overflow-hidden rounded-2xl border p-5 transition-colors",
          userTurn ? "border-accent/50 bg-accent/[0.07] shadow-[var(--shadow-glow)]" : "border-line bg-surface-2/50",
        )}
      >
        <AnimatePresence mode="wait">
          <motion.div
            key={revealing ? "rev" : cp ? `${cp.overall}-${cp.isUser}` : "none"}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            transition={{ duration: 0.2 }}
          >
            {revealing || (action === "simulate" && !userTurn) ? (
              <div>
                <p className="flex items-center gap-2 text-xs font-semibold tracking-widest text-info uppercase">
                  <Bot className="size-4" aria-hidden /> CPU teams drafting
                </p>
                <p className="mt-3 text-2xl font-bold">Picks are coming in…</p>
                <div className="mt-4 flex gap-1.5" aria-hidden>
                  {[0, 1, 2].map((i) => (
                    <motion.span key={i} className="size-2 rounded-full bg-info" animate={{ opacity: [0.3, 1, 0.3] }} transition={{ repeat: Infinity, duration: 1, delay: i * 0.2 }} />
                  ))}
                </div>
              </div>
            ) : !cp ? (
              <div>
                <p className="flex items-center gap-2 text-xs font-semibold tracking-widest text-accent uppercase">
                  <Flag className="size-4" aria-hidden /> Draft complete
                </p>
                <p className="mt-3 text-2xl font-bold">That's a wrap.</p>
              </div>
            ) : userTurn ? (
              <div>
                <div className="flex items-center justify-between">
                  <p className="flex items-center gap-2 text-xs font-semibold tracking-widest text-accent uppercase">
                    <span className="relative flex size-2.5">
                      <span className="absolute inline-flex size-full animate-ping rounded-full bg-accent opacity-70" />
                      <span className="relative inline-flex size-2.5 rounded-full bg-accent" />
                    </span>
                    You're on the clock
                  </p>
                  <span className="rounded-lg bg-accent/15 px-2 py-0.5 text-sm font-bold text-accent tabular">{pickLabel(cp.round, cp.pickInRound)}</span>
                </div>
                <p className="mt-1 text-xs text-fg-subtle">Overall pick #{cp.overall}</p>
                <div className="mt-4 min-h-[76px]">
                  {selected ? (
                    <motion.div
                      key={selected.rank}
                      initial={{ opacity: 0, scale: 0.97 }}
                      animate={{ opacity: 1, scale: 1 }}
                      className="flex items-center gap-3 rounded-xl border border-line-strong bg-surface p-3"
                    >
                      <PositionBadge position={selected.position} />
                      <div className="min-w-0 flex-1">
                        <p className="truncate font-semibold">{selected.name}</p>
                        <p className="text-xs text-fg-subtle tabular">
                          Rank {selected.rank} · ADP {selected.adp} · {formatPoints(selected.projectedPoints)} pts
                        </p>
                      </div>
                    </motion.div>
                  ) : (
                    <p className="flex items-center gap-2 rounded-xl border border-dashed border-line-strong p-4 text-sm text-fg-subtle">
                      <MousePointerClick className="size-4" aria-hidden /> Select a player from the board
                    </p>
                  )}
                </div>
              </div>
            ) : (
              <div>
                <p className="text-xs font-semibold tracking-widest text-fg-subtle uppercase">On the clock</p>
                <p className="mt-2 truncate text-2xl font-bold">{cp.teamName}</p>
                <p className="mt-1 text-sm text-fg-muted tabular">
                  Pick {pickLabel(cp.round, cp.pickInRound)} · #{cp.overall} overall
                </p>
              </div>
            )}
          </motion.div>
        </AnimatePresence>
      </div>

      {cp && (
        <div className="space-y-2">
          {userTurn ? (
            <div className="grid grid-cols-[1fr_auto] gap-2">
              <Button size="lg" icon={<Hand className="size-4" />} onClick={onDraft} disabled={!selected || busy} loading={action === "pick"}>
                {selected ? `Draft ${selected.name.split(" ").slice(-1)[0]}` : "Draft player"}
              </Button>
              <Button size="lg" variant="secondary" icon={<Bot className="size-4" />} onClick={onAutoPick} disabled={busy} loading={action === "auto"} title="Let the CPU pick the best fit for your roster">
                Auto
              </Button>
            </div>
          ) : (
            <Button size="lg" className="w-full" icon={<FastForward className="size-4" />} onClick={onSimulate} disabled={busy} loading={action === "simulate"}>
              Sim to my pick
            </Button>
          )}
          <label className="flex cursor-pointer items-center justify-between gap-3 rounded-xl px-1 py-1.5 text-xs text-fg-muted">
            <span>Auto-sim CPU picks after I draft</span>
            <span className="relative inline-flex">
              <input type="checkbox" className="peer sr-only" checked={autoSim} onChange={(e) => onAutoSimChange(e.target.checked)} />
              <span className="h-5 w-9 rounded-full bg-surface-3 ring-1 ring-line-strong transition peer-checked:bg-accent peer-focus-visible:ring-2 peer-focus-visible:ring-accent" />
              <span className="absolute left-0.5 top-0.5 size-4 rounded-full bg-white shadow transition peer-checked:translate-x-4" />
            </span>
          </label>
        </div>
      )}

      <dl className="grid grid-cols-2 gap-3">
        <div className="rounded-xl border border-line bg-surface-2/40 p-3">
          <dt className="text-[11px] text-fg-subtle">Your next pick</dt>
          <dd className="mt-1 text-lg font-bold tabular">{draft.nextUserPick ? pickLabel(draft.nextUserPick.round, draft.nextUserPick.pickInRound) : "—"}</dd>
          <dd className="text-[11px] text-fg-subtle">
            {draft.nextUserPick ? (draft.nextUserPick.picksUntil === 0 ? "Now" : `in ${plural(draft.nextUserPick.picksUntil, "pick")}`) : "No picks left"}
          </dd>
        </div>
        <div className="rounded-xl border border-line bg-surface-2/40 p-3">
          <dt className="text-[11px] text-fg-subtle">Your slot</dt>
          <dd className="mt-1 text-lg font-bold tabular">{ordinal(draft.userSlot)}</dd>
          <dd className="text-[11px] text-fg-subtle">of {draft.numTeams} teams</dd>
        </div>
      </dl>

      <div>
        <div className="flex justify-between text-[11px] text-fg-subtle tabular">
          <span>
            Round {Math.min(draft.rounds, Math.floor(draft.picksMade / draft.numTeams) + 1)} of {draft.rounds}
          </span>
          <span>
            {draft.picksMade}/{draft.totalPicks}
          </span>
        </div>
        <div className="mt-1.5 h-1.5 overflow-hidden rounded-full bg-surface-3">
          <motion.div className="h-full rounded-full bg-gradient-to-r from-accent to-sky-400" animate={{ width: `${pct}%` }} transition={{ duration: 0.3 }} />
        </div>
      </div>
    </div>
  );
}
