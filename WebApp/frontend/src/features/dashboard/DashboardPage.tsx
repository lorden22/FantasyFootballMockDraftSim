import { useCallback, useEffect, useMemo, useState, type ReactNode } from "react";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "motion/react";
import { ArrowRight, CalendarDays, ClipboardList, History, Play, Plus, Trash2, Trophy } from "lucide-react";
import { AppShell } from "../../components/AppShell";
import { PageTransition, stagger } from "../../components/PageTransition";
import { Card, CardHeader } from "../../components/ui/Card";
import { Button } from "../../components/ui/Button";
import { Badge } from "../../components/ui/Badge";
import { ConfirmDialog } from "../../components/ui/Modal";
import { Skeleton, SkeletonRows } from "../../components/ui/Skeleton";
import { EmptyState, ErrorState } from "../../components/ui/States";
import { useToast } from "../../components/ui/Toast";
import { NewDraftModal } from "./NewDraftModal";
import { draftApi } from "../../api/endpoints";
import { errorMessage } from "../../api/client";
import type { DraftSummary } from "../../api/types";
import { useAuth } from "../../auth/AuthContext";
import { formatDate, formatDateTime, ordinal, plural } from "../../lib/format";
import { useDocumentTitle } from "../../lib/useDocumentTitle";

export default function DashboardPage() {
  useDocumentTitle("Dashboard");
  const { user } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const [drafts, setDrafts] = useState<DraftSummary[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [newOpen, setNewOpen] = useState(false);
  const [toDelete, setToDelete] = useState<DraftSummary | null>(null);
  const [deleting, setDeleting] = useState(false);

  const load = useCallback(async () => {
    setError(null);
    try {
      setDrafts(await draftApi.list());
    } catch (e) {
      setError(errorMessage(e));
    }
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  const current = useMemo(() => drafts?.find((d) => d.status === "IN_PROGRESS") ?? null, [drafts]);
  const completed = useMemo(() => drafts?.filter((d) => d.status === "COMPLETE") ?? [], [drafts]);
  const stats = useMemo(() => {
    const sizes = completed.map((d) => d.numTeams);
    const avgSlot = completed.length ? completed.reduce((s, d) => s + d.userSlot, 0) / completed.length : 0;
    return {
      total: completed.length,
      favoriteSize: sizes.length ? mode(sizes) : null,
      avgSlot,
      last: completed[0]?.completedAt ?? completed[0]?.createdAt ?? null,
    };
  }, [completed]);

  const startNew = () => {
    if (current) {
      toast.info("Finish or delete your current draft first", "You can have one draft in progress at a time.");
      return;
    }
    setNewOpen(true);
  };

  const confirmDelete = async () => {
    if (!toDelete) return;
    setDeleting(true);
    try {
      await draftApi.remove(toDelete.id);
      setDrafts((ds) => ds?.filter((d) => d.id !== toDelete.id) ?? null);
      toast.success("Draft deleted");
      setToDelete(null);
    } catch (e) {
      toast.error("Couldn't delete draft", errorMessage(e));
    } finally {
      setDeleting(false);
    }
  };

  return (
    <AppShell>
      <PageTransition>
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
          <div>
            <p className="text-sm text-fg-subtle">2026 season</p>
            <h1 className="mt-1 text-3xl font-bold tracking-tight">
              Welcome, <span className="text-gradient">{user?.username}</span>
            </h1>
          </div>
          <Button size="lg" icon={<Plus className="size-4" />} onClick={startNew} disabled={drafts === null}>
            New mock draft
          </Button>
        </div>

        {error ? (
          <Card className="mt-8">
            <ErrorState message={error} onRetry={load} />
          </Card>
        ) : (
          <motion.div variants={stagger.container} initial="hidden" animate="show" className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-3">
            {/* Current draft */}
            <motion.div variants={stagger.item} className="lg:col-span-2">
              {drafts === null ? (
                <Card className="p-6">
                  <Skeleton className="h-5 w-40" />
                  <Skeleton className="mt-4 h-8 w-64" />
                  <Skeleton className="mt-6 h-2 w-full" />
                  <Skeleton className="mt-6 h-10 w-44" />
                </Card>
              ) : current ? (
                <CurrentDraftCard draft={current} onResume={() => navigate(`/drafts/${current.id}`)} onDelete={() => setToDelete(current)} />
              ) : (
                <Card className="relative overflow-hidden p-6 sm:p-8">
                  <div className="pointer-events-none absolute -right-16 -top-16 size-64 rounded-full bg-accent/10 blur-3xl" aria-hidden />
                  <Badge tone="neutral">No draft in progress</Badge>
                  <h2 className="mt-4 text-2xl font-bold tracking-tight">Ready for your next mock?</h2>
                  <p className="mt-2 max-w-lg text-sm text-fg-muted">
                    Pick a league size and draft slot. CPU managers fill out the rest of the room and draft around you.
                  </p>
                  <Button className="mt-6" icon={<Play className="size-4" />} onClick={startNew}>
                    Start a draft
                  </Button>
                </Card>
              )}
            </motion.div>

            {/* Stats */}
            <motion.div variants={stagger.item}>
              <Card className="h-full">
                <CardHeader title="Your numbers" subtitle="Across completed drafts" />
                <dl className="grid grid-cols-2 gap-px overflow-hidden rounded-b-[var(--radius-card)] bg-line">
                  <Stat label="Drafts completed" value={drafts === null ? null : String(stats.total)} icon={<Trophy className="size-4" />} />
                  <Stat label="Favorite size" value={drafts === null ? null : stats.favoriteSize ? `${stats.favoriteSize} teams` : "—"} icon={<ClipboardList className="size-4" />} />
                  <Stat label="Avg. draft slot" value={drafts === null ? null : stats.total ? stats.avgSlot.toFixed(1) : "—"} icon={<History className="size-4" />} />
                  <Stat label="Last draft" value={drafts === null ? null : formatDate(stats.last)} icon={<CalendarDays className="size-4" />} />
                </dl>
              </Card>
            </motion.div>

            {/* History */}
            <motion.div variants={stagger.item} className="lg:col-span-3">
              <Card>
                <CardHeader title="Draft history" subtitle={drafts ? plural(completed.length, "completed draft") : "Loading…"} />
                {drafts === null ? (
                  <SkeletonRows rows={4} className="p-5" />
                ) : completed.length === 0 ? (
                  <EmptyState
                    icon={<History className="size-5" />}
                    title="No completed drafts yet"
                    body="Finish a mock draft and it will show up here with its full board and rosters."
                  />
                ) : (
                  <ul className="divide-y divide-line">
                    {completed.map((d, i) => (
                      <motion.li
                        key={d.id}
                        initial={{ opacity: 0, y: 6 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: Math.min(i * 0.03, 0.3) }}
                        className="group flex flex-col gap-3 px-5 py-4 transition-colors hover:bg-surface-2/50 sm:flex-row sm:items-center"
                      >
                        <div className="flex min-w-0 flex-1 items-center gap-4">
                          <div className="flex size-11 shrink-0 flex-col items-center justify-center rounded-xl bg-surface-2 ring-1 ring-line">
                            <span className="text-sm font-bold leading-none tabular">{d.userSlot}</span>
                            <span className="text-[9px] text-fg-subtle uppercase">slot</span>
                          </div>
                          <div className="min-w-0">
                            <p className="truncate font-semibold">{d.teamName}</p>
                            <p className="text-xs text-fg-subtle">
                              {d.numTeams}-team league · {ordinal(d.userSlot)} pick · {formatDateTime(d.completedAt ?? d.createdAt)}
                            </p>
                          </div>
                        </div>
                        <div className="flex items-center gap-2 sm:opacity-80 sm:group-hover:opacity-100">
                          <Link
                            to={`/drafts/${d.id}/review`}
                            className="inline-flex h-8 items-center gap-1.5 rounded-lg border border-line-strong px-3 text-xs font-semibold hover:bg-surface-3"
                          >
                            Review <ArrowRight className="size-3.5" aria-hidden />
                          </Link>
                          <Button variant="ghost" size="sm" className="size-8 px-0" aria-label={`Delete draft ${d.teamName}`} onClick={() => setToDelete(d)}>
                            <Trash2 className="size-4" />
                          </Button>
                        </div>
                      </motion.li>
                    ))}
                  </ul>
                )}
              </Card>
            </motion.div>
          </motion.div>
        )}
      </PageTransition>

      <NewDraftModal open={newOpen} onClose={() => setNewOpen(false)} defaultTeamName={user ? `${user.username}'s Squad`.slice(0, 40) : "My Team"} />
      <ConfirmDialog
        open={toDelete !== null}
        onClose={() => !deleting && setToDelete(null)}
        onConfirm={confirmDelete}
        loading={deleting}
        title="Delete this draft?"
        description={
          <>
            <span className="font-semibold text-fg">{toDelete?.teamName}</span> and all of its picks will be permanently removed. This can't be undone.
          </>
        }
        confirmLabel="Delete draft"
      />
    </AppShell>
  );
}

function CurrentDraftCard({ draft, onResume, onDelete }: { draft: DraftSummary; onResume: () => void; onDelete: () => void }) {
  const pct = draft.totalPicks ? Math.round((draft.picksMade / draft.totalPicks) * 100) : 0;
  const round = Math.min(draft.rounds, Math.floor(draft.picksMade / draft.numTeams) + 1);
  return (
    <Card className="relative overflow-hidden border-accent/25 p-6 sm:p-8">
      <div className="pointer-events-none absolute -right-20 -top-20 size-72 rounded-full bg-accent/15 blur-3xl" aria-hidden />
      <div className="relative">
        <div className="flex items-center gap-2">
          <span className="relative flex size-2.5">
            <span className="absolute inline-flex size-full animate-ping rounded-full bg-accent opacity-60" />
            <span className="relative inline-flex size-2.5 rounded-full bg-accent" />
          </span>
          <span className="text-xs font-semibold tracking-widest text-accent uppercase">Draft in progress</span>
        </div>
        <h2 className="mt-4 text-2xl font-bold tracking-tight">{draft.teamName}</h2>
        <p className="mt-1 text-sm text-fg-muted">
          {draft.numTeams}-team league · picking {ordinal(draft.userSlot)} · Round {round} of {draft.rounds}
        </p>
        <div className="mt-6">
          <div className="flex justify-between text-xs text-fg-subtle tabular">
            <span>
              {draft.picksMade} of {draft.totalPicks} picks
            </span>
            <span>{pct}%</span>
          </div>
          <div className="mt-2 h-2 overflow-hidden rounded-full bg-surface-3" role="progressbar" aria-valuenow={pct} aria-valuemin={0} aria-valuemax={100} aria-label="Draft progress">
            <motion.div className="h-full rounded-full bg-gradient-to-r from-accent to-sky-400" initial={{ width: 0 }} animate={{ width: `${pct}%` }} transition={{ duration: 0.8, ease: "easeOut" }} />
          </div>
        </div>
        <div className="mt-7 flex flex-wrap gap-3">
          <Button size="lg" icon={<Play className="size-4" />} onClick={onResume}>
            Resume draft
          </Button>
          <Button size="lg" variant="ghost" icon={<Trash2 className="size-4" />} onClick={onDelete}>
            Delete
          </Button>
        </div>
      </div>
    </Card>
  );
}

function Stat({ label, value, icon }: { label: string; value: string | null; icon: ReactNode }) {
  return (
    <div className="bg-surface p-5">
      <dt className="flex items-center gap-1.5 text-xs text-fg-subtle">
        {icon}
        {label}
      </dt>
      <dd className="mt-2 text-xl font-bold tabular">{value === null ? <Skeleton className="h-6 w-16" /> : value}</dd>
    </div>
  );
}

function mode(nums: number[]): number {
  const counts = new Map<number, number>();
  let best = nums[0];
  for (const n of nums) {
    const c = (counts.get(n) ?? 0) + 1;
    counts.set(n, c);
    if (c > (counts.get(best) ?? 0)) best = n;
  }
  return best;
}
