import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { draftApi } from "../../api/endpoints";
import { ApiError, errorMessage } from "../../api/client";
import type { DraftState, Pick, Player, Roster } from "../../api/types";

export type DraftAction = "pick" | "auto" | "simulate" | null;

const AUTO_SIM_KEY = "md.autoSim";

function readAutoSim(): boolean {
  try {
    return window.localStorage.getItem(AUTO_SIM_KEY) !== "0";
  } catch {
    return true;
  }
}

/**
 * Owns all draft-room state. After every server action the new picks are "revealed" one by one so
 * CPU picks visibly land on the board, ticker and log instead of appearing all at once.
 */
export function useDraftRoom(draftId: number) {
  const [draft, setDraft] = useState<DraftState | null>(null);
  const [available, setAvailable] = useState<Player[]>([]);
  const [roster, setRoster] = useState<Roster | null>(null);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [action, setAction] = useState<DraftAction>(null);
  const [revealed, setRevealed] = useState(0);
  const [autoSim, setAutoSimState] = useState(readAutoSim);
  const revealTimer = useRef<number | null>(null);
  // Latest pick count, read by callbacks that may hold a stale `draft` closure (e.g. pick -> auto-sim).
  const pickCountRef = useRef(0);
  const mounted = useRef(true);

  useEffect(() => {
    mounted.current = true;
    return () => {
      mounted.current = false;
      if (revealTimer.current) window.clearInterval(revealTimer.current);
    };
  }, []);

  const setAutoSim = useCallback((v: boolean) => {
    setAutoSimState(v);
    try {
      window.localStorage.setItem(AUTO_SIM_KEY, v ? "1" : "0");
    } catch {
      /* preference only */
    }
  }, []);

  const startReveal = useCallback((from: number, to: number) => {
    if (revealTimer.current) window.clearInterval(revealTimer.current);
    const reduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    const count = to - from;
    if (count <= 1 || reduced) {
      setRevealed(to);
      return;
    }
    const step = Math.max(45, Math.min(160, Math.floor(2200 / count)));
    let n = from;
    setRevealed(n);
    revealTimer.current = window.setInterval(() => {
      n += 1;
      setRevealed(n);
      if (n >= to && revealTimer.current) {
        window.clearInterval(revealTimer.current);
        revealTimer.current = null;
      }
    }, step);
  }, []);

  const loadSide = useCallback(
    async (d: DraftState) => {
      const [avail, ros] = await Promise.all([draftApi.available(d.id), draftApi.roster(d.id, d.userSlot)]);
      if (!mounted.current) return;
      setAvailable(avail);
      setRoster(ros);
    },
    [],
  );

  const load = useCallback(async () => {
    setLoadError(null);
    try {
      const d = await draftApi.get(draftId);
      if (!mounted.current) return;
      setDraft(d);
      pickCountRef.current = d.picks.length;
      setRevealed(d.picks.length);
      await loadSide(d);
    } catch (e) {
      if (mounted.current) setLoadError(e instanceof ApiError && e.status === 404 ? "This draft doesn't exist or isn't yours." : errorMessage(e));
    }
  }, [draftId, loadSide]);

  useEffect(() => {
    void load();
  }, [load]);

  /** Apply a fresh server state and animate in whatever picks are new. */
  const apply = useCallback(
    async (next: DraftState) => {
      const before = pickCountRef.current;
      pickCountRef.current = next.picks.length;
      setDraft(next);
      startReveal(before, next.picks.length);
      await loadSide(next);
    },
    [loadSide, startReveal],
  );

  const run = useCallback(
    async (kind: Exclude<DraftAction, null>, fn: () => Promise<DraftState>): Promise<DraftState | null> => {
      setAction(kind);
      try {
        const next = await fn();
        if (!mounted.current) return null;
        await apply(next);
        return next;
      } catch (e) {
        // State may have moved on (double click, another tab): resync quietly, then surface the error.
        if (e instanceof ApiError && e.status === 409) void load();
        throw e;
      } finally {
        if (mounted.current) setAction(null);
      }
    },
    [apply, load],
  );

  const simulate = useCallback(() => run("simulate", () => draftApi.simulate(draftId)), [run, draftId]);

  const afterUserPick = useCallback(
    async (next: DraftState | null) => {
      if (next && autoSim && next.status === "IN_PROGRESS" && next.currentPick && !next.currentPick.isUser) {
        // Let the user's own pick land before the CPU run starts; keep controls locked meanwhile.
        setAction("simulate");
        await new Promise((r) => window.setTimeout(r, 450));
        if (mounted.current) await simulate();
      }
    },
    [autoSim, simulate],
  );

  const pick = useCallback(
    async (playerRank: number) => afterUserPick(await run("pick", () => draftApi.pick(draftId, playerRank))),
    [run, draftId, afterUserPick],
  );
  const autoPick = useCallback(async () => afterUserPick(await run("auto", () => draftApi.autoPick(draftId))), [run, draftId, afterUserPick]);

  const revealing = draft !== null && revealed < draft.picks.length;

  /** What the UI should render right now (picks limited to those revealed so far). */
  const view = useMemo(() => {
    if (!draft) return null;
    const visiblePicks = draft.picks.slice(0, revealed);
    const hiddenPicks: Pick[] = draft.picks.slice(revealed);
    // `available` can briefly lag behind `draft` (it is refetched after each action), so rebuild the
    // visible pool from scratch: server list minus revealed picks, plus players whose pick isn't shown yet.
    const drafted = new Set(visiblePicks.map((p) => p.player.rank));
    const pool = new Map<number, Player>();
    for (const p of available) if (!drafted.has(p.rank)) pool.set(p.rank, p);
    for (const p of hiddenPicks) pool.set(p.player.rank, p.player);
    const visibleAvailable = [...pool.values()].sort((a, b) => a.rank - b.rank);
    return {
      draft: { ...draft, picks: visiblePicks, picksMade: visiblePicks.length, currentPick: revealing ? null : draft.currentPick } as DraftState,
      available: visibleAvailable,
      lastPick: visiblePicks[visiblePicks.length - 1] ?? null,
    };
  }, [draft, available, revealed, revealing]);

  return { draft, view, roster, loadError, reload: load, action, revealing, pick, autoPick, simulate, autoSim, setAutoSim };
}
