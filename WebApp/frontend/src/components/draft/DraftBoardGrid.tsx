import { memo, useMemo } from "react";
import { motion } from "motion/react";
import type { DraftState, Pick } from "../../api/types";
import { POSITION_CELL, POSITION_STYLES } from "../../lib/positions";
import { pickLabel } from "../../lib/format";
import { cn } from "../../lib/cn";

/** Snake draft board: rows = rounds, columns = teams by draft slot. */
export const DraftBoardGrid = memo(function DraftBoardGrid({ draft, className }: { draft: DraftState; className?: string }) {
  const byOverall = useMemo(() => {
    const m = new Map<number, Pick>();
    for (const p of draft.picks) m.set(p.overall, p);
    return m;
  }, [draft.picks]);

  const n = draft.numTeams;
  const current = draft.currentPick?.overall ?? null;

  const overallFor = (round: number, slot: number) => {
    const pickInRound = round % 2 === 1 ? slot : n - slot + 1;
    return (round - 1) * n + pickInRound;
  };

  return (
    <div className={cn("overflow-auto rounded-xl border border-line", className)}>
      <table className="w-full border-separate border-spacing-0 text-left" style={{ minWidth: Math.max(640, n * 112 + 56) }}>
        <caption className="sr-only">Draft board, {draft.rounds} rounds by {n} teams</caption>
        <thead className="sticky top-0 z-10">
          <tr>
            <th scope="col" className="sticky left-0 z-20 w-14 border-b border-line bg-surface-2 px-2 py-2.5 text-[11px] font-semibold text-fg-subtle">
              Rd
            </th>
            {draft.teams.map((t) => (
              <th
                key={t.slot}
                scope="col"
                className={cn(
                  "border-b border-l border-line px-2 py-2.5 text-[11px] font-semibold",
                  t.isUser ? "bg-accent/15 text-accent" : "bg-surface-2 text-fg-muted",
                )}
              >
                <span className="block text-[10px] font-medium text-fg-subtle">#{t.slot}</span>
                <span className="block truncate" title={t.name}>
                  {t.name}
                </span>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {Array.from({ length: draft.rounds }, (_, r) => r + 1).map((round) => (
            <tr key={round}>
              <th scope="row" className="sticky left-0 z-10 border-b border-line bg-surface px-2 text-center text-xs font-semibold text-fg-subtle tabular">
                {round}
                <span className="block text-[10px] font-normal">{round % 2 === 1 ? "→" : "←"}</span>
              </th>
              {draft.teams.map((t) => {
                const overall = overallFor(round, t.slot);
                const pick = byOverall.get(overall);
                const isCurrent = overall === current;
                return (
                  <td key={t.slot} className={cn("h-16 border-b border-l border-line p-1 align-top", t.isUser && "bg-accent/[0.04]")}>
                    {pick ? (
                      <motion.div
                        initial={{ opacity: 0, scale: 0.85 }}
                        animate={{ opacity: 1, scale: 1 }}
                        transition={{ type: "spring", stiffness: 400, damping: 28 }}
                        className={cn("flex h-full flex-col justify-between rounded-lg border px-2 py-1.5", POSITION_CELL[pick.player.position])}
                        title={`${pick.player.name} · ${pick.player.position} · ${pickLabel(pick.round, pick.pickInRound)}`}
                      >
                        <span className="truncate text-xs font-semibold leading-tight text-fg">{pick.player.name}</span>
                        <span className="flex items-center justify-between text-[10px] tabular">
                          <span className={cn("font-bold", POSITION_STYLES[pick.player.position].text)}>{pick.player.position}</span>
                          <span className="text-fg-subtle">{pickLabel(pick.round, pick.pickInRound)}</span>
                        </span>
                      </motion.div>
                    ) : (
                      <div
                        className={cn(
                          "flex h-full items-center justify-center rounded-lg border border-dashed text-[10px] tabular",
                          isCurrent ? "animate-pulse-ring border-accent bg-accent/10 font-semibold text-accent" : "border-line text-fg-subtle/60",
                        )}
                      >
                        {isCurrent ? "On clock" : pickLabel(round, round % 2 === 1 ? t.slot : n - t.slot + 1)}
                      </div>
                    )}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
});
