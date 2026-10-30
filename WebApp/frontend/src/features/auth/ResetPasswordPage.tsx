import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Lock, ShieldAlert } from "lucide-react";
import { AuthLayout, FormAlert } from "./AuthLayout";
import { PasswordRules } from "./PasswordRules";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { Spinner } from "../../components/ui/Spinner";
import { authApi } from "../../api/endpoints";
import { errorMessage } from "../../api/client";
import { passwordOk } from "../../lib/password";
import { useDocumentTitle } from "../../lib/useDocumentTitle";

type Phase = "checking" | "invalid" | "ready";

const TOKEN_RE = /^[A-Za-z0-9_-]{16,128}$/;

function readTokenFromUrl(): string | null {
  const token = new URLSearchParams(window.location.search).get("token");
  return token && TOKEN_RE.test(token) ? token : null;
}

/** Scrub the token from the address bar / history so it can't leak via Referer, history sync or screenshots. */
function stripTokenFromUrl(): void {
  const url = new URL(window.location.href);
  if (!url.searchParams.has("token")) return;
  url.searchParams.delete("token");
  window.history.replaceState(window.history.state, "", url.pathname + url.search + url.hash);
}

export function ResetPasswordPage() {
  useDocumentTitle("Choose a new password");
  const navigate = useNavigate();
  const [token] = useState(readTokenFromUrl);

  const [phase, setPhase] = useState<Phase>("checking");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    stripTokenFromUrl();
    if (!token) {
      setPhase("invalid");
      return;
    }
    let cancelled = false;
    authApi
      .validateReset(token)
      .then((r) => !cancelled && setPhase(r.valid ? "ready" : "invalid"))
      .catch(() => !cancelled && setPhase("invalid"));
    return () => {
      cancelled = true;
    };
  }, [token]);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    if (!passwordOk(password, "")) {
      setError("Your new password doesn't meet the requirements.");
      return;
    }
    if (password !== confirm) {
      setError("Passwords don't match.");
      return;
    }
    setBusy(true);
    setError(null);
    try {
      await authApi.confirmReset(token!, password);
      navigate("/login?reset=1", { replace: true });
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  if (phase === "checking") {
    return (
      <AuthLayout title="Verifying link">
        <div className="flex items-center gap-3 text-sm text-fg-muted">
          <Spinner /> Checking your reset link…
        </div>
      </AuthLayout>
    );
  }

  if (phase === "invalid") {
    return (
      <AuthLayout title="Link expired" subtitle="This reset link is invalid or has already been used.">
        <div className="flex flex-col items-center text-center">
          <div className="flex size-14 items-center justify-center rounded-2xl bg-warning/10 text-warning ring-1 ring-warning/25">
            <ShieldAlert className="size-6" aria-hidden />
          </div>
          <p className="mt-4 text-sm text-fg-muted">For your security, reset links expire after 30 minutes and work only once.</p>
          <Link to="/forgot-password" className="mt-6 inline-flex h-11 items-center rounded-xl bg-accent px-5 text-sm font-semibold text-accent-fg hover:bg-[#4ade9f]">
            Request a new link
          </Link>
        </div>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout title="Choose a new password" subtitle="You'll be signed out of all other devices.">
      <form onSubmit={submit} className="space-y-5" noValidate>
        {error && <FormAlert>{error}</FormAlert>}
        <div className="space-y-3">
          <Input
            label="New password"
            type="password"
            autoComplete="new-password"
            maxLength={72}
            icon={<Lock className="size-4" />}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoFocus
            required
          />
          <PasswordRules password={password} username="" />
        </div>
        <Input
          label="Confirm new password"
          type="password"
          autoComplete="new-password"
          maxLength={72}
          icon={<Lock className="size-4" />}
          value={confirm}
          onChange={(e) => setConfirm(e.target.value)}
          error={confirm && confirm !== password ? "Passwords don't match" : null}
          required
        />
        <Button type="submit" size="lg" className="w-full" loading={busy}>
          Update password
        </Button>
      </form>
    </AuthLayout>
  );
}
