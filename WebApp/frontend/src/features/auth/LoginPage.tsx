import { useState, type FormEvent } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { Lock, User as UserIcon } from "lucide-react";
import { AuthLayout, FormAlert } from "./AuthLayout";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { useAuth } from "../../auth/AuthContext";
import { errorMessage } from "../../api/client";
import { useToast } from "../../components/ui/Toast";
import { useDocumentTitle } from "../../lib/useDocumentTitle";

export function LoginPage() {
  useDocumentTitle("Sign in");
  const { login } = useAuth();
  const toast = useToast();
  const [params] = useSearchParams();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const expired = params.get("expired") === "1";
  const reset = params.get("reset") === "1";

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password) {
      setError("Enter your username and password.");
      return;
    }
    setBusy(true);
    setError(null);
    try {
      const u = await login(username.trim(), password);
      // <GuestOnly> redirects to the validated `next` page as soon as the user is set.
      toast.success(`Welcome back, ${u.username}`);
    } catch (err) {
      setError(errorMessage(err));
      setPassword("");
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthLayout
      title="Welcome back"
      subtitle="Sign in to pick up where you left off."
      footer={
        <>
          New to MockDraft Pro?{" "}
          <Link to="/register" className="font-semibold text-accent hover:underline">
            Create an account
          </Link>
        </>
      }
    >
      <form onSubmit={submit} className="space-y-5" noValidate>
        {expired && !error && <FormAlert tone="info">Your session expired. Please sign in again.</FormAlert>}
        {reset && !error && <FormAlert tone="success">Password updated. Sign in with your new password.</FormAlert>}
        {error && <FormAlert>{error}</FormAlert>}
        <Input
          label="Username"
          name="username"
          autoComplete="username"
          autoCapitalize="none"
          spellCheck={false}
          maxLength={30}
          icon={<UserIcon className="size-4" />}
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          autoFocus
          required
        />
        <div>
          <Input
            label="Password"
            name="password"
            type="password"
            autoComplete="current-password"
            maxLength={128}
            icon={<Lock className="size-4" />}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <div className="mt-2 text-right">
            <Link to="/forgot-password" className="text-xs font-medium text-fg-muted hover:text-accent">
              Forgot password?
            </Link>
          </div>
        </div>
        <Button type="submit" size="lg" className="w-full" loading={busy}>
          Sign in
        </Button>
      </form>
    </AuthLayout>
  );
}
