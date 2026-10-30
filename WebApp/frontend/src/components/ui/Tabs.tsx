import { useId, useRef, type KeyboardEvent, type ReactNode } from "react";
import { motion } from "motion/react";
import { cn } from "../../lib/cn";

export type TabItem<T extends string> = { id: T; label: ReactNode; badge?: ReactNode };

export function Tabs<T extends string>({
  items,
  value,
  onChange,
  className,
  size = "md",
  ariaLabel,
}: {
  items: TabItem<T>[];
  value: T;
  onChange: (id: T) => void;
  className?: string;
  size?: "sm" | "md";
  ariaLabel: string;
}) {
  const group = useId();
  const refs = useRef<(HTMLButtonElement | null)[]>([]);

  const onKeyDown = (e: KeyboardEvent<HTMLDivElement>) => {
    const idx = items.findIndex((i) => i.id === value);
    let next = idx;
    if (e.key === "ArrowRight") next = (idx + 1) % items.length;
    else if (e.key === "ArrowLeft") next = (idx - 1 + items.length) % items.length;
    else if (e.key === "Home") next = 0;
    else if (e.key === "End") next = items.length - 1;
    else return;
    e.preventDefault();
    onChange(items[next].id);
    refs.current[next]?.focus();
  };

  return (
    <div role="tablist" aria-label={ariaLabel} onKeyDown={onKeyDown} className={cn("inline-flex rounded-xl border border-line bg-bg-soft p-1", className)}>
      {items.map((item, i) => {
        const active = item.id === value;
        return (
          <button
            key={item.id}
            ref={(el) => {
              refs.current[i] = el;
            }}
            role="tab"
            type="button"
            aria-selected={active}
            tabIndex={active ? 0 : -1}
            onClick={() => onChange(item.id)}
            className={cn(
              "relative flex flex-1 items-center justify-center gap-1.5 rounded-lg font-medium whitespace-nowrap transition-colors",
              size === "sm" ? "h-7 px-2.5 text-xs" : "h-8 px-3.5 text-sm",
              active ? "text-fg" : "text-fg-subtle hover:text-fg-muted",
            )}
          >
            {active && (
              <motion.span
                layoutId={`tab-pill-${group}`}
                className="absolute inset-0 rounded-lg bg-surface-3 ring-1 ring-line-strong"
                transition={{ type: "spring", stiffness: 500, damping: 38 }}
              />
            )}
            <span className="relative">{item.label}</span>
            {item.badge !== undefined && <span className="relative">{item.badge}</span>}
          </button>
        );
      })}
    </div>
  );
}
