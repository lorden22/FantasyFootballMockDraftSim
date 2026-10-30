import { AnimatePresence, motion } from "motion/react";
import type { Pick } from "../../api/types";
import { POSITION_STYLES } from "../../lib/positions";
import { pickLabel } from "../../lib/format";
import { cn } from "../../lib/cn";

export function PickTicker({ picks, userSlot, max = 14 }: { picks: Pick[]; userSlot: number; max?: number }) {
  const recent = picks.slice(-max).reverse();
  return (
    <div className="relative">
      <div className="pointer-events-none absolute inset-y-0 right-0 z-10 w-12 bg-gradient-to-l from-surface to-transparent" aria-hidden />
      <ol className="flex gap-2 overflow-x-auto pb-1" aria-label="Most recent picks">
        {recent.length === 0 && <li className="py-3 text-xs text-fg-subtle">Picks will appear here as the draft unfolds.</li>}
        <AnimatePresence initial={false}>
          {recent.map((p) => {
            const mine = p.slot === userSlot;
            const s = POSITION_STYLES[p.player.position];
            return (
              <motion.li
                key={p.overall}
                layout
                initial={{ opacity: 0, scale: 0.6, x: -30 }}
                animate={{ opacity: 1, scale: 1, x: 0 }}
                transition={{ type: "spring", stiffness: 500, damping: 32 }}
                className={cn("w-40 shrink-0 rounded-xl border bg-surface-2/80 p-2.5", mine ? "border-accent/50" : "border-line")}
              >
                <div className="flex items-center justify-between text-[10px] font-semibold tabular">
                  <span className="text-fg-subtle">{pickLabel(p.round, p.pickInRound)}</span>
                  <span className={s.text}>{p.player.position}</span>
                </div>
                <p className="mt-1 truncate text-xs font-semibold">{p.player.name}</p>
                <p className={cn("truncate text-[10px]", mine ? "font-semibold text-accent" : "text-fg-subtle")}>{p.teamName}</p>
              </motion.li>
            );
          })}
        </AnimatePresence>
      </ol>
    </div>
  );
}
