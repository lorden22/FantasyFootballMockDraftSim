import { useMemo, useState } from "react";
import type { Pick, Position } from "../../api/types";
import { POSITIONS } from "../../api/types";
import { PositionBadge } from "../ui/Badge";
import { formatPoints, pickLabel } from "../../lib/format";
import { cn } from "../../lib/cn";

export function PickLogTable({
  picks,
  userSlot,
  newestFirst = false,
  dense = false,
  showFilters = false,
}: {
  picks: Pick[];
  userSlot: number;
  newestFirst?: boolean;
  dense?: boolean;
  showFilters?: boolean;
}) {
  const [pos, setPos] = useState<Position | "ALL">("ALL");
  const rows = useMemo(() => {
    const filtered = pos === "ALL" ? picks : picks.filter((p) => p.player.position === pos);
    return newestFirst ? [...filtered].reverse() : filtered;
  }, [picks, pos, newestFirst]);

  return (
    <div>
      {showFilters && (
        <div className="mb-3 flex flex-wrap gap-1.5" role="group" aria-label="Filter by position">
          {(["ALL", ...POSITIONS] as const).map((p) => (
            <button
              key={p}
              type="button"
              aria-pressed={pos === p}
              onClick={() => setPos(p)}
              className={cn(
                "h-7 rounded-lg px-2.5 text-xs font-semibold transition",
                pos === p ? "bg-surface-3 text-fg ring-1 ring-line-strong" : "text-fg-subtle hover:text-fg",
              )}
            >
              {p === "ALL" ? "All" : p}
            </button>
          ))}
        </div>
      )}
      {rows.length === 0 ? (
        <p className="py-8 text-center text-sm text-fg-subtle">No picks yet.</p>
      ) : (
        <table className="w-full table-fixed text-left text-sm">
          <thead className="sticky top-0 z-[1] bg-surface text-[11px] tracking-wide text-fg-subtle uppercase">
            <tr>
              <th scope="col" className="w-14 py-2 pr-2 font-semibold">Pick</th>
              <th scope="col" className="py-2 pr-2 font-semibold">Player</th>
              <th scope="col" className="hidden w-40 py-2 pr-2 font-semibold sm:table-cell">Team</th>
              <th scope="col" className="w-14 py-2 text-right font-semibold">Proj</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-line/70">
            {rows.map((p) => {
              const mine = p.slot === userSlot;
              return (
                <tr key={p.overall} className={cn(mine && "bg-accent/[0.06]")}>
                  <td className={cn("pr-2 text-xs font-semibold text-fg-subtle tabular", dense ? "py-1.5" : "py-2.5")}>{pickLabel(p.round, p.pickInRound)}</td>
                  <td className={cn("pr-2", dense ? "py-1.5" : "py-2.5")}>
                    <div className="flex min-w-0 items-center gap-2">
                      <PositionBadge position={p.player.position} size="xs" />
                      <span className="min-w-0">
                        <span className="block truncate font-medium">{p.player.name}</span>
                        <span className={cn("block truncate text-[11px] sm:hidden", mine ? "text-accent" : "text-fg-subtle")}>{p.teamName}</span>
                      </span>
                    </div>
                  </td>
                  <td className={cn("hidden max-w-36 truncate pr-2 text-xs sm:table-cell", mine ? "font-semibold text-accent" : "text-fg-muted", dense ? "py-1.5" : "py-2.5")}>
                    {p.teamName}
                  </td>
                  <td className={cn("text-right text-xs tabular text-fg-muted", dense ? "py-1.5" : "py-2.5")}>{formatPoints(p.player.projectedPoints)}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}
