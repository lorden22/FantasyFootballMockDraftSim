import { useCallback, useEffect, useMemo, useState, type ReactNode } from "react";
import { Link, useParams } from "react-router-dom";
import { motion } from "motion/react";
import { ArrowLeft, Crown, Grid3x3, ListOrdered, Play, Users } from "lucide-react";
import { AppShell } from "../../components/AppShell";
import { PageTransition } from "../../components/PageTransition";
import { Card, CardHeader } from "../../components/ui/Card";
import { Badge } from "../../components/ui/Badge";
import { Tabs } from "../../components/ui/Tabs";
import { Skeleton, SkeletonRows } from "../../components/ui/Skeleton";
import { ErrorState } from "../../components/ui/States";
import { DraftBoardGrid } from "../../components/draft/DraftBoardGrid";
import { PickLogTable } from "../../components/draft/PickLogTable";
import { RosterView } from "../../components/draft/RosterView";
import { draftApi } from "../../api/endpoints";
import { ApiError, errorMessage } from "../../api/client";
import type { DraftState, Roster } from "../../api/types";
import { formatDateTime, formatPoints, ordinal } from "../../lib/format";
import { cn } from "../../lib/cn";
import { useDocumentTitle } from "../../lib/useDocumentTitle";
import { NotFoundPage } from "../NotFoundPage";

type Tab = "teams" | "board" | "log";

export default function DraftReviewPage() {
  const { id } = useParams();
  const draftId = Number(id);
  if (!Number.isInteger(draftId) || draftId <= 0) return <NotFoundPage />;
  return <DraftReview draftId={draftId} />;
}

function DraftReview({ draftId }: { draftId: number }) {
  useDocumentTitle("Draft review");
  const [draft, setDraft] = useState<DraftState | null>(null);
  const [rosters, setRosters] = useState<Roster[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [tab, setTab] = useState<Tab>("teams");
  const [slot, setSlot] = useState<number | null>(null);

  const load = useCallback(async () => {
    setError(null);
    try {
      const d = await draftApi.get(draftId);
      setDraft(d);
      setSlot((s) => s ?? d.userSlot);
      const all = await Promise.all(d.teams.map((t) => draftApi.roster(d.id, t.slot)));
      setRosters(all);
    } catch (e) {
      setError(e instanceof ApiError && e.status === 404 ? "This draft doesn't exist or isn't yours." : errorMessage(e));
    }
  }, [draftId]);

  useEffect(() => {
    void load();
  }, [load]);

  const standings = useMemo(
    () => (rosters ? [...rosters].sort((a, b) => b.projectedStarterPoints - a.projectedStarterPoints) : null),
    [rosters],
  );
  const userRank = standings && draft ? standings.findIndex((r) => r.slot === draft.userSlot) + 1 : null;
  const selectedRoster = rosters?.find((r) => r.slot === slot) ?? null;

  if (error) {
    return (
      <AppShell>
        <Card className="mx-auto mt-10 max-w-lg">
          <ErrorState message={error} onRetry={load} />
        </Card>
      </AppShell>
    );
  }

  return (
    <AppShell wide>
      <PageTransition>
        <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div className="flex items-start gap-3">
            <Link to="/dashboard" className="mt-1 flex size-9 shrink-0 items-center justify-center rounded-xl border border-line text-fg-muted hover:bg-surface-2 hover:text-fg" aria-label="Back to dashboard">
              <ArrowLeft className="size-4" />
            </Link>
            <div className="min-w-0">
              {draft ? (
                <>
                  <div className="flex flex-wrap items-center gap-2">
                    <h1 className="truncate text-2xl font-bold tracking-tight">{draft.teamName}</h1>
                    <Badge tone={draft.status === "COMPLETE" ? "accent" : "warning"}>{draft.status === "COMPLETE" ? "Complete" : "In progress"}</Badge>
                  </div>
                  <p className="mt-1 text-sm text-fg-subtle">
                    {draft.numTeams}-team league · {ordinal(draft.userSlot)} pick · {formatDateTime(draft.completedAt ?? draft.createdAt)}
                  </p>
                </>
              ) : (
                <>
                  <Skeleton className="h-7 w-56" />
                  <Skeleton className="mt-2 h-4 w-40" />
                </>
              )}
            </div>
          </div>
          {draft?.status === "IN_PROGRESS" && (
            <Link to={`/drafts/${draft.id}`} className="inline-flex h-10 items-center gap-2 rounded-xl bg-accent px-4 text-sm font-semibold text-accent-fg hover:bg-[#4ade9f]">
              <Play className="size-4" aria-hidden /> Resume draft
            </Link>
          )}
        </div>

        {/* Summary strip */}
        <div className="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
          <SummaryTile label="Projected finish" value={userRank ? `${ordinal(userRank)} of ${draft?.numTeams}` : null} icon={<Crown className="size-4" />} highlight />
          <SummaryTile
            label="Your starter points"
            value={rosters && draft ? formatPoints(rosters.find((r) => r.slot === draft.userSlot)?.projectedStarterPoints ?? 0) : null}
            icon={<Users className="size-4" />}
          />
          <SummaryTile label="Picks made" value={draft ? `${draft.picksMade} / ${draft.totalPicks}` : null} icon={<ListOrdered className="size-4" />} />
        </div>

        <Tabs<Tab>
          ariaLabel="Review sections"
          className="mb-5"
          value={tab}
          onChange={setTab}
          items={[
            { id: "teams", label: <span className="flex items-center gap-1.5"><Users className="size-4" />Teams</span> },
            { id: "board", label: <span className="flex items-center gap-1.5"><Grid3x3 className="size-4" />Board</span> },
            { id: "log", label: <span className="flex items-center gap-1.5"><ListOrdered className="size-4" />Pick log</span> },
          ]}
        />

        {!draft ? (
          <Card className="p-5">
            <SkeletonRows rows={8} />
          </Card>
        ) : tab === "board" ? (
          <Card className="p-4">
            <DraftBoardGrid draft={draft} className="max-h-[72dvh]" />
          </Card>
        ) : tab === "log" ? (
          <Card className="p-5">
            <PickLogTable picks={draft.picks} userSlot={draft.userSlot} showFilters />
          </Card>
        ) : (
          <div className="grid grid-cols-1 gap-5 lg:grid-cols-[minmax(260px,340px)_minmax(0,1fr)]">
            <Card className="overflow-hidden">
              <CardHeader title="League standings" subtitle="By projected starter points" />
              {!standings ? (
                <SkeletonRows rows={draft.numTeams} className="p-4" />
              ) : (
                <ol className="max-h-[60dvh] divide-y divide-line overflow-y-auto">
                  {standings.map((r, i) => (
                    <li key={r.slot}>
                      <button
                        type="button"
                        aria-pressed={slot === r.slot}
                        onClick={() => setSlot(r.slot)}
                        className={cn("flex w-full items-center gap-3 px-4 py-3 text-left transition-colors", slot === r.slot ? "bg-surface-2" : "hover:bg-surface-2/50")}
                      >
                        <span className={cn("w-6 text-center text-sm font-bold tabular", i === 0 ? "text-pos-te" : "text-fg-subtle")}>{i + 1}</span>
                        <span className="min-w-0 flex-1">
                          <span className={cn("block truncate text-sm font-semibold", r.isUser && "text-accent")}>{r.teamName}</span>
                          <span className="block text-[11px] text-fg-subtle">Slot {r.slot}</span>
                        </span>
                        <span className="text-sm font-semibold tabular">{formatPoints(r.projectedStarterPoints)}</span>
                      </button>
                      {standings[0] && (
                        <div className="h-0.5 bg-surface-2">
                          <motion.div
                            className={cn("h-full", r.isUser ? "bg-accent" : "bg-line-strong")}
                            initial={{ width: 0 }}
                            animate={{ width: `${(r.projectedStarterPoints / Math.max(1, standings[0].projectedStarterPoints)) * 100}%` }}
                            transition={{ duration: 0.6, delay: i * 0.03 }}
                          />
                        </div>
                      )}
                    </li>
                  ))}
                </ol>
              )}
            </Card>
            <Card>
              <CardHeader
                title={selectedRoster ? selectedRoster.teamName : "Roster"}
                subtitle={selectedRoster ? `Draft slot ${selectedRoster.slot}${selectedRoster.isUser ? " · Your team" : ""}` : undefined}
                action={
                  draft && (
                    <label className="flex items-center gap-2 text-xs text-fg-subtle">
                      <span className="sr-only sm:not-sr-only">Team</span>
                      <select
                        value={slot ?? ""}
                        onChange={(e) => setSlot(Number(e.target.value))}
                        className="h-8 max-w-44 rounded-lg border border-line bg-bg-soft px-2 text-xs text-fg outline-none focus:border-accent"
                      >
                        {draft.teams.map((t) => (
                          <option key={t.slot} value={t.slot}>
                            {t.slot}. {t.name}
                            {t.isUser ? " (you)" : ""}
                          </option>
                        ))}
                      </select>
                    </label>
                  )
                }
              />
              <div className="p-5">{selectedRoster ? <RosterView roster={selectedRoster} /> : <SkeletonRows rows={9} />}</div>
            </Card>
          </div>
        )}
      </PageTransition>
    </AppShell>
  );
}

function SummaryTile({ label, value, icon, highlight = false }: { label: string; value: string | null; icon: ReactNode; highlight?: boolean }) {
  return (
    <Card className={cn("p-5", highlight && "border-accent/30")}>
      <p className="flex items-center gap-1.5 text-xs text-fg-subtle">
        {icon}
        {label}
      </p>
      <div className={cn("mt-2 text-2xl font-bold tabular", highlight && "text-accent")}>{value ?? <Skeleton className="h-7 w-24" />}</div>
    </Card>
  );
}
