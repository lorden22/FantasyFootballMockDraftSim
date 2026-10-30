import type { ReactNode } from "react";
import { RefreshCw, TriangleAlert } from "lucide-react";
import { Button } from "./Button";
import { cn } from "../../lib/cn";

export function EmptyState({ icon, title, body, action, className }: { icon?: ReactNode; title: string; body?: ReactNode; action?: ReactNode; className?: string }) {
  return (
    <div className={cn("flex flex-col items-center justify-center px-6 py-12 text-center", className)}>
      {icon && <div className="mb-4 flex size-12 items-center justify-center rounded-2xl bg-surface-2 text-fg-muted ring-1 ring-line">{icon}</div>}
      <h3 className="text-sm font-semibold text-fg">{title}</h3>
      {body && <p className="mt-1 max-w-sm text-sm text-fg-subtle">{body}</p>}
      {action && <div className="mt-5">{action}</div>}
    </div>
  );
}

export function ErrorState({ message, onRetry, className }: { message: string; onRetry?: () => void; className?: string }) {
  return (
    <div role="alert" className={cn("flex flex-col items-center justify-center px-6 py-12 text-center", className)}>
      <div className="mb-4 flex size-12 items-center justify-center rounded-2xl bg-danger/10 text-danger ring-1 ring-danger/25">
        <TriangleAlert className="size-5" aria-hidden />
      </div>
      <h3 className="text-sm font-semibold text-fg">We hit a snag</h3>
      <p className="mt-1 max-w-sm text-sm text-fg-subtle">{message}</p>
      {onRetry && (
        <Button variant="outline" size="sm" className="mt-5" icon={<RefreshCw className="size-3.5" />} onClick={onRetry}>
          Try again
        </Button>
      )}
    </div>
  );
}
