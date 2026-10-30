import { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { AnimatePresence, motion } from "motion/react";
import { ArrowLeft, ClipboardList, Grid3x3, LayoutPanelLeft, PartyPopper, Trophy } from "lucide-react";
import { AppShell } from "../../components/AppShell";
import { PageTransition } from "../../components/PageTransition";
import { Card } from "../../components/ui/Card";
import { Tabs } from "../../components/ui/Tabs";
import { Skeleton, SkeletonRows } from "../../components/ui/Skeleton";
import { ErrorState } from "../../components/ui/States";
import { useToast } from "../../components/ui/Toast";
import { DraftBoardGrid } from "../../components/draft/DraftBoardGrid";
import { RosterView } from "../../components/draft/RosterView";
import { PickLogTable } from "../../components/draft/PickLogTable";
import { PlayerBoard } from "./PlayerBoard";
import { OnTheClock } from "./OnTheClock";
import { PickTicker } from "./PickTicker";
import { useDraftRoom } from "./useDraftRoom";
import { errorMessage } from "../../api/client";
import type { Position, Roster } from "../../api/types";
import { formatPoints, ordinal, pickLabel } from "../../lib/format";
import { useMediaQuery } from "../../lib/useMediaQuery";
import { useDocumentTitle } from "../../lib/useDocumentTitle";
import { NotFoundPage } from "../NotFoundPage";

type View = "room" | "board";
type SideTab = "roster" | "log";
type MobileTab = "players" | "clock" | "roster" | "log" | "board";

export default function DraftRoomPage() {
  const { id } = useParams();
  const draftId = Number(id);
  if (!Number.isInteger(draftId) || draftId <= 0) return <NotFoundPage />;
  return <DraftRoom draftId={draftId} />;
}

function DraftRoom({ draftId }: { draftId: number }) {
  const room = useDraftRoom(draftId);
  const toast = useToast();
  const navigate = useNavigate();
  const desktop = useMediaQuery("(min-width: 1024px)");
  const [view, setView] = useState<View>("room");
  const [sideTab, setSideTab] = useState<SideTab>("roster");
  const [mobileTab, setMobileTab] = useState<MobileTab>("players");
  const [selectedRank, setSelectedRank] = useState<number | null>(null);
  const [announcement, setAnnouncement] = useState("");

  const v = room.view;
  useDocumentTitle(v ? (v.draft.status === "COMPLETE" ? "Draft complete" : v.draft.currentPick?.isUser ? "Your pick!" : "Draft room") : "Draft room");

  const selected = useMemo(() => v?.available.find((p) => p.rank === selectedRank) ?? null, [v, selectedRank]);

  // Clear a selection that is no longer available (drafted by someone else).
  useEffect(() => {
    if (selectedRank !== null && v && !v.available.some((p) => p.rank === selectedRank)) setSelectedRank(null);
  }, [v, selectedRank]);

  // Screen-reader announcements for each revealed pick and when it becomes the user's turn.
  useEffect(() => {
    if (!v?.lastPick) return;
    const p = v.lastPick;
    setAnnouncement(`Pick ${pickLabel(p.round, p.pickInRound)}: ${p.teamName} selects ${p.player.name}, ${p.player.position}.`);
  }, [v?.lastPick]);
  useEffect(() => {
    if (v?.draft.currentPick?.isUser) {
      setAnnouncement("You're on the clock.");
      if (!desktop) setMobileTab("players");
    }
  }, [v?.draft.currentPick?.overall, v?.draft.currentPick?.isUser, desktop]);

  const positionCounts = useMemo(() => countPositions(room.roster), [room.roster]);

  const handle = useCallback(
    async (fn: () => Promise<void>, failTitle: string) => {
      try {
        await fn();
      } catch (e) {
        toast.error(failTitle, errorMessage(e));
      }
    },
    [toast],
  );

  const draftPlayer = useCallback(
    (rank: number) => {
      const name = v?.available.find((p) => p.rank === rank)?.name;
      void handle(async () => {
        await room.pick(rank);
        setSelectedRank(null);
        if (name) toast.success(`You drafted ${name}`);
      }, "Couldn't make that pick");
    },
    [handle, room, toast, v],
  );

  if (room.loadError) {
    return (
      <AppShell>
        <Card className="mx-auto mt-10 max-w-lg">
          <ErrorState message={room.loadError} onRetry={room.reload} />
          <div className="pb-8 text-center">
            <Link to="/dashboard" className="text-sm font-semibold text-accent hover:underline">
              Back to dashboard
            </Link>
          </div>
        </Card>
      </AppShell>
    );
  }

  if (!v || !room.draft) return <DraftRoomSkeleton />;

  const draft = v.draft;
  const complete = room.draft.status === "COMPLETE" && !room.revealing;
  const userTurn = !!draft.currentPick?.isUser;

  const clock = (
    <OnTheClock
      draft={draft}
      revealing={room.revealing}
      action={room.action}
      selected={selected}
      autoSim={room.autoSim}
      onAutoSimChange={room.setAutoSim}
      onDraft={() => selected && draftPlayer(selected.rank)}
      onAutoPick={() => void handle(room.autoPick, "Auto-pick failed")}
      onSimulate={() => void handle(async () => void (await room.simulate()), "Simulation failed")}
    />
  );
  const board = (
    <PlayerBoard
      players={v.available}
      selectedRank={selectedRank}
      onSelect={setSelectedRank}
      onDraft={draftPlayer}
      canDraft={userTurn && room.action === null && !room.revealing}
      positionCounts={positionCounts}
      className="h-full"
    />
  );
  const rosterPanel = room.roster ? <RosterView roster={room.roster} compact /> : <SkeletonRows rows={9} />;
  const logPanel = <PickLogTable picks={draft.picks} userSlot={draft.userSlot} newestFirst dense />;

  return (
    <AppShell wide>
      <PageTransition>
        <p className="sr-only" aria-live="polite" aria-atomic="true">
          {announcement}
        </p>

        {/* Header */}
        <div className="mb-5 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-3">
            <Link to="/dashboard" className="flex size-9 items-center justify-center rounded-xl border border-line text-fg-muted hover:bg-surface-2 hover:text-fg" aria-label="Back to dashboard">
              <ArrowLeft className="size-4" />
            </Link>
            <div className="min-w-0">
              <h1 className="truncate text-xl font-bold tracking-tight">{draft.teamName}</h1>
              <p className="text-xs text-fg-subtle">
                {draft.numTeams}-team snake · {ordinal(draft.userSlot)} pick · {draft.rounds} rounds
              </p>
            </div>
          </div>
          {desktop && (
            <Tabs<View>
              ariaLabel="Draft room view"
              value={view}
              onChange={setView}
              items={[
                { id: "room", label: <span className="flex items-center gap-1.5"><LayoutPanelLeft className="size-4" />Draft room</span> },
                { id: "board", label: <span className="flex items-center gap-1.5"><Grid3x3 className="size-4" />Draft board</span> },
              ]}
            />
          )}
        </div>

        <AnimatePresence>{complete && <CompletionBanner draftId={draft.id} roster={room.roster} onReview={() => navigate(`/drafts/${draft.id}/review`)} />}</AnimatePresence>

        {/* Ticker */}
        <Card className="mb-5 px-4 py-3">
          <PickTicker picks={draft.picks} userSlot={draft.userSlot} />
        </Card>

        {desktop ? (
          view === "board" ? (
            <Card className="p-4">
              <DraftBoardGrid draft={draft} className="max-h-[calc(100dvh-300px)]" />
            </Card>
          ) : (
            <div className="grid h-[calc(100dvh-270px)] min-h-[560px] grid-cols-[minmax(0,1.35fr)_minmax(300px,0.9fr)_minmax(300px,1fr)] gap-5">
              <Card className="min-h-0 overflow-hidden">{board}</Card>
              <Card className="min-h-0 overflow-y-auto p-5">{clock}</Card>
              <Card className="flex min-h-0 flex-col overflow-hidden">
                <div className="border-b border-line p-3">
                  <Tabs<SideTab>
                    ariaLabel="Team panels"
                    size="sm"
                    className="w-full"
                    value={sideTab}
                    onChange={setSideTab}
                    items={[
                      { id: "roster", label: "My roster" },
                      { id: "log", label: "Draft log", badge: <span className="rounded bg-surface-3 px-1 text-[10px] tabular text-fg-subtle">{draft.picks.length}</span> },
                    ]}
                  />
                </div>
                <div className="min-h-0 flex-1 overflow-y-auto p-4">{sideTab === "roster" ? rosterPanel : logPanel}</div>
              </Card>
            </div>
          )
        ) : (
          <div className="space-y-4">
            <div className="sticky top-16 z-30 -mx-4 bg-bg/90 px-4 py-2 backdrop-blur">
              <Tabs<MobileTab>
                ariaLabel="Draft room sections"
                size="sm"
                className="w-full"
                value={mobileTab}
                onChange={setMobileTab}
                items={[
                  { id: "players", label: "Players" },
                  { id: "clock", label: userTurn ? <span className="text-accent">Clock •</span> : "Clock" },
                  { id: "roster", label: "Roster" },
                  { id: "log", label: "Log" },
                  { id: "board", label: "Board" },
                ]}
              />
            </div>
            {mobileTab === "players" && (
              <>
                <Card className="p-4">{clock}</Card>
                <Card className="h-[70dvh] overflow-hidden">{board}</Card>
              </>
            )}
            {mobileTab === "clock" && <Card className="p-4">{clock}</Card>}
            {mobileTab === "roster" && <Card className="p-4">{rosterPanel}</Card>}
            {mobileTab === "log" && <Card className="p-4">{logPanel}</Card>}
            {mobileTab === "board" && (
              <Card className="p-3">
                <DraftBoardGrid draft={draft} className="max-h-[75dvh]" />
              </Card>
            )}
          </div>
        )}
      </PageTransition>
    </AppShell>
  );
}

function CompletionBanner({ draftId, roster, onReview }: { draftId: number; roster: Roster | null; onReview: () => void }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: -12, scale: 0.98 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      exit={{ opacity: 0 }}
      transition={{ type: "spring", stiffness: 300, damping: 26 }}
      className="relative mb-5 overflow-hidden rounded-2xl border border-accent/40 bg-gradient-to-r from-emerald-500/15 via-surface to-sky-500/10 p-6"
    >
      <Confetti />
      <div className="relative flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-4">
          <div className="flex size-14 items-center justify-center rounded-2xl bg-accent/15 text-accent ring-1 ring-accent/30">
            <PartyPopper className="size-7" aria-hidden />
          </div>
          <div>
            <h2 className="text-xl font-bold">Draft complete!</h2>
            <p className="text-sm text-fg-muted">
              Your starters project for{" "}
              <span className="font-semibold text-fg tabular">{roster ? formatPoints(roster.projectedStarterPoints) : "—"}</span> points. See how you stack up against the league.
            </p>
          </div>
        </div>
        <div className="flex gap-2">
          <Link to="/dashboard" className="inline-flex h-11 items-center rounded-xl border border-line-strong px-4 text-sm font-semibold hover:bg-surface-2">
            Dashboard
          </Link>
          <button
            type="button"
            onClick={onReview}
            data-draft={draftId}
            className="inline-flex h-11 items-center gap-2 rounded-xl bg-accent px-5 text-sm font-semibold text-accent-fg hover:bg-[#4ade9f]"
          >
            <Trophy className="size-4" aria-hidden /> Review draft
          </button>
        </div>
      </div>
    </motion.div>
  );
}

function Confetti() {
  const colors = ["#34d399", "#38bdf8", "#fbbf24", "#fb7185", "#a78bfa"];
  return (
    <div className="pointer-events-none absolute inset-0 overflow-hidden" aria-hidden>
      {Array.from({ length: 28 }, (_, i) => (
        <motion.span
          key={i}
          className="absolute top-0 block h-2.5 w-1.5 rounded-sm"
          style={{ left: `${(i * 37) % 100}%`, backgroundColor: colors[i % colors.length] }}
          initial={{ y: -20, opacity: 0, rotate: 0 }}
          animate={{ y: 160, opacity: [0, 1, 1, 0], rotate: (i % 2 ? 1 : -1) * 260 }}
          transition={{ duration: 1.8 + (i % 5) * 0.25, delay: (i % 7) * 0.08, ease: "easeOut" }}
        />
      ))}
    </div>
  );
}

function DraftRoomSkeleton() {
  return (
    <AppShell wide>
      <div className="mb-5 flex items-center gap-3">
        <Skeleton className="size-9" />
        <div className="space-y-2">
          <Skeleton className="h-5 w-48" />
          <Skeleton className="h-3 w-32" />
        </div>
      </div>
      <Skeleton className="mb-5 h-20 w-full rounded-2xl" />
      <div className="grid gap-5 lg:grid-cols-3">
        <Card className="p-4">
          <SkeletonRows rows={10} />
        </Card>
        <Card className="space-y-4 p-5">
          <Skeleton className="h-36 w-full rounded-2xl" />
          <Skeleton className="h-12 w-full" />
          <Skeleton className="h-20 w-full" />
        </Card>
        <Card className="p-4">
          <SkeletonRows rows={9} />
        </Card>
      </div>
      <span className="sr-only" role="status">
        <ClipboardList /> Loading draft room
      </span>
    </AppShell>
  );
}

function countPositions(roster: Roster | null): Partial<Record<Position, number>> {
  const counts: Partial<Record<Position, number>> = {};
  if (!roster) return counts;
  for (const s of [...roster.starters, ...roster.bench]) {
    if (s.pick) counts[s.pick.player.position] = (counts[s.pick.player.position] ?? 0) + 1;
  }
  return counts;
}
