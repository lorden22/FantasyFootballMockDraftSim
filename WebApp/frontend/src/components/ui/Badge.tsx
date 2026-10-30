import type { ReactNode } from "react";
import { cn } from "../../lib/cn";
import type { Position } from "../../api/types";
import { POSITION_STYLES } from "../../lib/positions";

export function Badge({ children, className, tone = "neutral" }: { children: ReactNode; className?: string; tone?: "neutral" | "accent" | "warning" | "danger" | "info" }) {
  const tones = {
    neutral: "bg-surface-3 text-fg-muted ring-line-strong",
    accent: "bg-accent/12 text-accent ring-accent/30",
    warning: "bg-warning/12 text-warning ring-warning/30",
    danger: "bg-danger/12 text-danger ring-danger/30",
    info: "bg-info/12 text-info ring-info/30",
  } as const;
  return (
    <span className={cn("inline-flex items-center gap-1 rounded-md px-2 py-0.5 text-[11px] font-semibold ring-1 ring-inset", tones[tone], className)}>
      {children}
    </span>
  );
}

export function PositionBadge({ position, className, size = "sm" }: { position: Position; className?: string; size?: "xs" | "sm" }) {
  const s = POSITION_STYLES[position];
  return (
    <span
      title={s.label}
      className={cn(
        "inline-flex items-center justify-center rounded-md font-bold tracking-wide ring-1 ring-inset tabular",
        size === "xs" ? "h-5 min-w-8 px-1 text-[10px]" : "h-6 min-w-10 px-1.5 text-[11px]",
        s.text,
        s.bg,
        s.ring,
        className,
      )}
    >
      {position}
    </span>
  );
}
