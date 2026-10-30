import { cn } from "../lib/cn";

export function LogoMark({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 32 32" aria-hidden className={cn("size-8", className)}>
      <defs>
        <linearGradient id="md-logo-g" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0" stopColor="#6ee7b7" />
          <stop offset="1" stopColor="#10b981" />
        </linearGradient>
      </defs>
      <rect width="32" height="32" rx="9" fill="#0e1626" stroke="#1f2b44" />
      <path d="M8 22c3-9 13-13 16-12-1 3-5 13-12 16-1.5.6-4.6-1.6-4-4z" fill="url(#md-logo-g)" />
      <path d="M12.5 19.5l7-7M14 16.5l1.5 1.5M16 14.5l1.5 1.5" stroke="#0e1626" strokeWidth="1.6" strokeLinecap="round" />
    </svg>
  );
}

export function Logo({ className, compact = false }: { className?: string; compact?: boolean }) {
  return (
    <span className={cn("inline-flex items-center gap-2.5", className)}>
      <LogoMark />
      {!compact && (
        <span className="text-[15px] font-bold tracking-tight text-fg">
          MockDraft<span className="text-accent"> Pro</span>
        </span>
      )}
    </span>
  );
}
