import { motion } from "motion/react";
import type { Roster, RosterSlot } from "../../api/types";
import { PositionBadge } from "../ui/Badge";
import { formatPoints, pickLabel } from "../../lib/format";
import { cn } from "../../lib/cn";

export function RosterView({ roster, compact = false }: { roster: Roster; compact?: boolean }) {
  const filled = roster.starters.filter((s) => s.pick).length;
  return (
    <div className="space-y-5">
      <div className="flex items-end justify-between gap-3">
        <div>
          <p className="text-xs font-semibold tracking-widest text-fg-subtle uppercase">Starting lineup</p>
          <p className="mt-0.5 text-xs text-fg-subtle">
            {filled}/{roster.starters.length} slots filled
          </p>
        </div>
        <div className="text-right">
          <p className="text-xl font-bold tabular text-fg">{formatPoints(roster.projectedStarterPoints)}</p>
          <p className="text-[11px] text-fg-subtle">proj. starter pts</p>
        </div>
      </div>
      <ul className="space-y-1.5">
        {roster.starters.map((s, i) => (
          <SlotRow key={`${s.label}-${i}`} slot={s} index={i} compact={compact} />
        ))}
      </ul>
      <div>
        <p className="mb-2 text-xs font-semibold tracking-widest text-fg-subtle uppercase">Bench ({roster.bench.length})</p>
        {roster.bench.length === 0 ? (
          <p className="rounded-lg border border-dashed border-line px-3 py-3 text-xs text-fg-subtle">No bench players yet.</p>
        ) : (
          <ul className="space-y-1.5">
            {roster.bench.map((s, i) => (
              <SlotRow key={`bn-${s.pick?.overall ?? i}`} slot={s} index={i} compact={compact} />
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

function SlotRow({ slot, index, compact }: { slot: RosterSlot; index: number; compact: boolean }) {
  const p = slot.pick;
  return (
    <motion.li
      initial={{ opacity: 0, x: -6 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ delay: Math.min(index * 0.02, 0.2) }}
      className={cn(
        "flex items-center gap-3 rounded-lg border px-2.5",
        compact ? "py-1.5" : "py-2",
        p ? "border-line bg-surface-2/60" : "border-dashed border-line/80",
      )}
    >
      <span className="w-10 shrink-0 text-[11px] font-bold text-fg-subtle">{slot.label}</span>
      {p ? (
        <>
          <PositionBadge position={p.player.position} size="xs" />
          <span className="min-w-0 flex-1 truncate text-sm font-medium">{p.player.name}</span>
          <span className="hidden text-xs text-fg-subtle tabular sm:inline">{pickLabel(p.round, p.pickInRound)}</span>
          <span className="w-12 text-right text-xs font-semibold tabular text-fg-muted">{formatPoints(p.player.projectedPoints)}</span>
        </>
      ) : (
        <span className="text-xs text-fg-subtle">Empty</span>
      )}
    </motion.li>
  );
}
