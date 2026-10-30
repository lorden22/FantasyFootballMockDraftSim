import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { Lock, Mail, User as UserIcon } from "lucide-react";
import { AuthLayout, FormAlert } from "./AuthLayout";
import { PasswordRules } from "./PasswordRules";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { useAuth } from "../../auth/AuthContext";
import { ApiError, errorMessage } from "../../api/client";
import { emailError, passwordOk, usernameError } from "../../lib/password";
import { useToast } from "../../components/ui/Toast";
import { useDocumentTitle } from "../../lib/useDocumentTitle";

export function RegisterPage() {
  useDocumentTitle("Create account");
  const { register } = useAuth();
  const toast = useToast();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [touched, setTouched] = useState<Record<string, boolean>>({});
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const uErr = touched.username ? usernameError(username) : null;
  const eErr = touched.email ? emailError(email.trim()) : null;

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setTouched({ username: true, email: true, password: true });
    if (usernameError(username) || emailError(email.trim()) || !passwordOk(password, username)) {
      setError("Please fix the highlighted fields.");
      return;
    }
    setBusy(true);
    setError(null);
    setFieldErrors({});
    try {
      await register(username, password, email.trim() || null);
      // <GuestOnly> takes the user into the app once they're signed in.
      toast.success("Account created", "Let's run your first mock draft.");
    } catch (err) {
      if (err instanceof ApiError) setFieldErrors(err.fields);
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthLayout
      title="Create your account"
      subtitle="Free forever. No credit card required."
      footer={
        <>
          Already have an account?{" "}
          <Link to="/login" className="font-semibold text-accent hover:underline">
            Sign in
          </Link>
        </>
      }
    >
      <form onSubmit={submit} className="space-y-5" noValidate>
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
          onBlur={() => setTouched((t) => ({ ...t, username: true }))}
          error={fieldErrors.username ?? uErr}
          hint="3–30 characters: letters, numbers and underscores"
          autoFocus
          required
        />
        <Input
          label="Email (optional)"
          name="email"
          type="email"
          autoComplete="email"
          maxLength={254}
          icon={<Mail className="size-4" />}
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          onBlur={() => setTouched((t) => ({ ...t, email: true }))}
          error={fieldErrors.email ?? eErr}
          hint="Only used to reset your password. We never send marketing email."
        />
        <div className="space-y-3">
          <Input
            label="Password"
            name="password"
            type="password"
            autoComplete="new-password"
            maxLength={72}
            icon={<Lock className="size-4" />}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={fieldErrors.password}
            required
          />
          <PasswordRules password={password} username={username} />
        </div>
        <Button type="submit" size="lg" className="w-full" loading={busy}>
          Create account
        </Button>
      </form>
    </AuthLayout>
  );
}
