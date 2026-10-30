import { forwardRef, useId, useState, type InputHTMLAttributes, type ReactNode } from "react";
import { Eye, EyeOff } from "lucide-react";
import { cn } from "../../lib/cn";

type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  hint?: ReactNode;
  error?: string | null;
  icon?: ReactNode;
  hideLabel?: boolean;
};

export const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
  { label, hint, error, icon, hideLabel, className, id, type = "text", ...rest },
  ref,
) {
  const autoId = useId();
  const inputId = id ?? autoId;
  const describedBy = error ? `${inputId}-error` : hint ? `${inputId}-hint` : undefined;
  const [reveal, setReveal] = useState(false);
  const isPassword = type === "password";

  return (
    <div className={cn("space-y-1.5", className)}>
      <label htmlFor={inputId} className={cn("block text-sm font-medium text-fg-muted", hideLabel && "sr-only")}>
        {label}
      </label>
      <div className="relative">
        {icon && (
          <span aria-hidden className="pointer-events-none absolute inset-y-0 left-3 flex items-center text-fg-subtle">
            {icon}
          </span>
        )}
        <input
          ref={ref}
          id={inputId}
          type={isPassword && reveal ? "text" : type}
          aria-invalid={error ? true : undefined}
          aria-describedby={describedBy}
          className={cn(
            "h-11 w-full rounded-xl border bg-bg-soft px-3.5 text-sm text-fg placeholder:text-fg-subtle",
            "transition-colors outline-none focus:border-accent focus:ring-4 focus:ring-accent/15",
            error ? "border-danger/70" : "border-line hover:border-line-strong",
            icon && "pl-10",
            isPassword && "pr-11",
          )}
          {...rest}
        />
        {isPassword && (
          <button
            type="button"
            onClick={() => setReveal((v) => !v)}
            className="absolute inset-y-0 right-1.5 my-auto flex size-8 items-center justify-center rounded-lg text-fg-subtle hover:bg-surface-2 hover:text-fg"
            aria-label={reveal ? "Hide password" : "Show password"}
          >
            {reveal ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        )}
      </div>
      {error ? (
        <p id={`${inputId}-error`} className="text-xs font-medium text-danger">
          {error}
        </p>
      ) : hint ? (
        <div id={`${inputId}-hint`} className="text-xs text-fg-subtle">
          {hint}
        </div>
      ) : null}
    </div>
  );
});
