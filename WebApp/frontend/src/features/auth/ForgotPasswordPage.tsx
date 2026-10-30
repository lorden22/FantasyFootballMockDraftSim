import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { ArrowLeft, Mail, MailCheck } from "lucide-react";
import { motion } from "motion/react";
import { AuthLayout, FormAlert } from "./AuthLayout";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { authApi } from "../../api/endpoints";
import { errorMessage } from "../../api/client";
import { emailError } from "../../lib/password";
import { useDocumentTitle } from "../../lib/useDocumentTitle";

export function ForgotPasswordPage() {
  useDocumentTitle("Reset password");
  const [email, setEmail] = useState("");
  const [sent, setSent] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    const value = email.trim();
    const invalid = !value ? "Enter the email on your account." : emailError(value);
    if (invalid) {
      setError(invalid);
      return;
    }
    setBusy(true);
    setError(null);
    try {
      await authApi.requestReset(value);
      setSent(true);
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthLayout
      title={sent ? "Check your inbox" : "Forgot your password?"}
      subtitle={sent ? undefined : "Enter your account email and we'll send you a secure reset link."}
      footer={
        <Link to="/login" className="inline-flex items-center gap-1.5 font-semibold text-fg-muted hover:text-fg">
          <ArrowLeft className="size-4" /> Back to sign in
        </Link>
      }
    >
      {sent ? (
        <motion.div initial={{ opacity: 0, scale: 0.96 }} animate={{ opacity: 1, scale: 1 }} className="text-center">
          <div className="mx-auto flex size-14 items-center justify-center rounded-2xl bg-accent/10 text-accent ring-1 ring-accent/25">
            <MailCheck className="size-6" aria-hidden />
          </div>
          <p className="mt-5 text-sm text-fg-muted" role="status">
            If an account exists for <span className="font-semibold text-fg">{email.trim()}</span>, a reset link is on its way. It expires in 30 minutes.
          </p>
          <Button variant="outline" className="mt-6" onClick={() => setSent(false)}>
            Use a different email
          </Button>
        </motion.div>
      ) : (
        <form onSubmit={submit} className="space-y-5" noValidate>
          {error && <FormAlert>{error}</FormAlert>}
          <Input
            label="Email"
            name="email"
            type="email"
            autoComplete="email"
            maxLength={254}
            icon={<Mail className="size-4" />}
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoFocus
            required
          />
          <Button type="submit" size="lg" className="w-full" loading={busy}>
            Send reset link
          </Button>
        </form>
      )}
    </AuthLayout>
  );
}
