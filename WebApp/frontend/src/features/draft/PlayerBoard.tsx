import { memo, useDeferredValue, useMemo, useState, type KeyboardEvent } from "react";
import { AnimatePresence, motion } from "motion/react";
import { ArrowDown, ArrowUp, Search, X } from "lucide-react";
import type { Player, Position } from "../../api/types";
import { POSITIONS } from "../../api/types";
import { PositionBadge } from "../../components/ui/Badge";
import { POSITION_STYLES } from "../../lib/positions";
import { formatPoints } from "../../lib/format";
import { cn } from "../../lib/cn";

type SortKey = "rank" | "proj" | "name";
const PAGE = 120;

export const PlayerBoard = memo(function PlayerBoard({
  players,
  selectedRank,
  onSelect,
  onDraft,
  canDraft,
  positionCounts,
  className,
}: {
  players: Player[];
  selectedRank: number | null;
  onSelect: (rank: number | null) => void;
  onDraft: (rank: number) => void;
  canDraft: boolean;
  positionCounts: Partial<Record<Position, number>>;
  className?: string;
}) {
  const [query, setQuery] = useState("");
  const [pos, setPos] = useState<Position | "ALL">("ALL");
  const [sort, setSort] = useState<{ key: SortKey; dir: 1 | -1 }>({ key: "rank", dir: 1 });
  const [limit, setLimit] = useState(PAGE);
  const q = useDeferredValue(query.trim().toLowerCase());

  const rows = useMemo(() => {
    let list = players;
    if (pos !== "ALL") list = list.filter((p) => p.position === pos);
    if (q) list = list.filter((p) => p.name.toLowerCase().includes(q));
    const sorted = [...list].sort((a, b) => {
      const d =
        sort.key === "rank" ? a.rank - b.rank : sort.key === "proj" ? b.projectedPoints - a.projectedPoints : a.name.localeCompare(b.name);
      return d * sort.dir;
    });
    return sorted;
  }, [players, pos, q, sort]);

  const toggleSort = (key: SortKey) => setSort((s) => (s.key === key ? { key, dir: (s.dir * -1) as 1 | -1 } : { key, dir: 1 }));

  const onRowKey = (e: KeyboardEvent<HTMLButtonElement>, rank: number) => {
    if (e.key === "Enter" && selectedRank === rank && canDraft) {
      e.preventDefault();
      onDraft(rank);
    } else if (e.key === "ArrowDown" || e.key === "ArrowUp") {
      e.preventDefault();
      const sib = (e.key === "ArrowDown" ? e.currentTarget.parentElement?.nextElementSibling : e.currentTarget.parentElement?.previousElementSibling)?.querySelector("button");
      (sib as HTMLButtonElement | null)?.focus();
    }
  };

  return (
    <section aria-label="Available players" className={cn("flex min-h-0 flex-col", className)}>
      <div className="space-y-3 border-b border-line p-4">
        <div className="relative">
          <Search className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-fg-subtle" aria-hidden />
          <label htmlFor="player-search" className="sr-only">
            Search players
          </label>
          <input
            id="player-search"
            type="search"
            value={query}
            onChange={(e) => {
              setQuery(e.target.value);
              setLimit(PAGE);
            }}
            placeholder="Search players…"
            autoComplete="off"
            maxLength={60}
            className="h-10 w-full rounded-xl border border-line bg-bg-soft pl-9 pr-9 text-sm outline-none placeholder:text-fg-subtle focus:border-accent focus:ring-4 focus:ring-accent/15"
          />
          {query && (
            <button type="button" onClick={() => setQuery("")} className="absolute right-2 top-1/2 -translate-y-1/2 rounded-md p-1 text-fg-subtle hover:text-fg" aria-label="Clear search">
              <X className="size-4" />
            </button>
          )}
        </div>
        <div className="flex flex-wrap gap-1.5" role="group" aria-label="Filter by position">
          {(["ALL", ...POSITIONS] as const).map((p) => {
            const active = pos === p;
            const count = p === "ALL" ? null : positionCounts[p] ?? 0;
            return (
              <button
                key={p}
                type="button"
                aria-pressed={active}
                onClick={() => {
                  setPos(p);
                  setLimit(PAGE);
                }}
                className={cn(
                  "inline-flex h-7 items-center gap-1.5 rounded-lg px-2.5 text-xs font-bold transition",
                  active
                    ? p === "ALL"
                      ? "bg-fg text-bg"
                      : cn(POSITION_STYLES[p].bg, POSITION_STYLES[p].text, "ring-1 ring-inset", POSITION_STYLES[p].ring)
                    : "text-fg-subtle hover:bg-surface-2 hover:text-fg",
                )}
              >
                {p === "ALL" ? "All" : p}
                {count !== null && (
                  <span className={cn("rounded px-1 text-[10px] tabular", active ? "bg-black/20" : "bg-surface-3 text-fg-subtle")} title={`${count} on your roster`}>
                    {count}
                  </span>
                )}
              </button>
            );
          })}
        </div>
      </div>

      <div className="grid grid-cols-[2.75rem_minmax(0,1fr)_3.25rem_3.5rem_3rem] items-center gap-2 border-b border-line bg-surface-2/50 px-4 py-2 text-[11px] font-semibold tracking-wide text-fg-subtle uppercase">
        <SortHeader label="Rk" active={sort.key === "rank"} dir={sort.dir} onClick={() => toggleSort("rank")} />
        <SortHeader label="Player" active={sort.key === "name"} dir={sort.dir} onClick={() => toggleSort("name")} />
        <span>Pos</span>
        <SortHeader label="Proj" active={sort.key === "proj"} dir={sort.dir} onClick={() => toggleSort("proj")} className="justify-end" />
        <span className="text-right">ADP</span>
      </div>

      <div className="min-h-0 flex-1 overflow-y-auto">
        {rows.length === 0 ? (
          <p className="px-4 py-10 text-center text-sm text-fg-subtle">No players match your filters.</p>
        ) : (
          <ul className="divide-y divide-line/60">
            <AnimatePresence initial={false}>
              {rows.slice(0, limit).map((p) => {
                const selected = p.rank === selectedRank;
                return (
                  <motion.li
                    key={p.rank}
                    layout="position"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0, x: 24, backgroundColor: "rgb(52 211 153 / 0.15)", transition: { duration: 0.25 } }}
                    transition={{ layout: { duration: 0.25, ease: [0.22, 1, 0.36, 1] } }}
                  >
                    <button
                      type="button"
                      aria-pressed={selected}
                      onClick={() => onSelect(selected ? null : p.rank)}
                      onDoubleClick={() => canDraft && onDraft(p.rank)}
                      onKeyDown={(e) => onRowKey(e, p.rank)}
                      className={cn(
                        "grid w-full grid-cols-[2.75rem_minmax(0,1fr)_3.25rem_3.5rem_3rem] items-center gap-2 px-4 py-2.5 text-left text-sm transition-colors",
                        selected ? "bg-accent/12 shadow-[inset_3px_0_0_var(--color-accent)]" : "hover:bg-surface-2/70",
                      )}
                    >
                      <span className="text-xs font-semibold text-fg-subtle tabular">{p.rank}</span>
                      <span className={cn("truncate font-medium", selected && "text-accent")}>{p.name}</span>
                      <PositionBadge position={p.position} size="xs" />
                      <span className="text-right text-xs font-semibold tabular">{formatPoints(p.projectedPoints)}</span>
                      <span className="text-right text-xs text-fg-subtle tabular">{p.adp}</span>
                    </button>
                  </motion.li>
                );
              })}
            </AnimatePresence>
          </ul>
        )}
        {rows.length > limit && (
          <div className="p-4">
            <button type="button" onClick={() => setLimit((l) => l + PAGE)} className="w-full rounded-xl border border-line py-2 text-xs font-semibold text-fg-muted hover:bg-surface-2">
              Show more ({rows.length - limit} remaining)
            </button>
          </div>
        )}
      </div>
    </section>
  );
});

function SortHeader({ label, active, dir, onClick, className }: { label: string; active: boolean; dir: 1 | -1; onClick: () => void; className?: string }) {
  return (
    <button type="button" onClick={onClick} className={cn("flex items-center gap-1 uppercase hover:text-fg", active && "text-fg", className)} aria-label={`Sort by ${label}`}>
      {label}
      {active && (dir === 1 ? <ArrowUp className="size-3" aria-hidden /> : <ArrowDown className="size-3" aria-hidden />)}
    </button>
  );
}
