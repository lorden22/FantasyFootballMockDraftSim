import { Check, X } from "lucide-react";
import { passwordRules } from "../../lib/password";
import { cn } from "../../lib/cn";

export function PasswordRules({ password, username }: { password: string; username: string }) {
  const rules = passwordRules(password, username);
  const score = rules.filter((r) => r.ok).length + (password.length >= 12 ? 1 : 0);
  const strength = password.length === 0 ? 0 : score;
  const labels = ["", "Weak", "Fair", "Good", "Strong"];
  const colors = ["bg-surface-3", "bg-danger", "bg-warning", "bg-sky-400", "bg-accent"];
  return (
    <div className="space-y-2.5" aria-live="polite">
      <div className="flex items-center gap-2">
        <div className="flex flex-1 gap-1">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className={cn("h-1 flex-1 rounded-full transition-colors duration-300", i <= strength ? colors[strength] : "bg-surface-3")} />
          ))}
        </div>
        <span className="w-12 text-right text-xs text-fg-subtle">{labels[strength]}</span>
      </div>
      <ul className="space-y-1">
        {rules.map((r) => (
          <li key={r.id} className={cn("flex items-center gap-2 text-xs transition-colors", r.ok ? "text-accent" : "text-fg-subtle")}>
            {r.ok ? <Check className="size-3.5" aria-hidden /> : <X className="size-3.5" aria-hidden />}
            <span>
              {r.label}
              <span className="sr-only">{r.ok ? " (met)" : " (not met)"}</span>
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
