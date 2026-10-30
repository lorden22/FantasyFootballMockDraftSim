import { useState, type FormEvent, type ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { motion } from "motion/react";
import { KeyRound, Lock, Mail, TriangleAlert, UserRound } from "lucide-react";
import { AppShell } from "../../components/AppShell";
import { PageTransition, stagger } from "../../components/PageTransition";
import { Card } from "../../components/ui/Card";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { Modal } from "../../components/ui/Modal";
import { useToast } from "../../components/ui/Toast";
import { FormAlert } from "../auth/AuthLayout";
import { PasswordRules } from "../auth/PasswordRules";
import { useAuth } from "../../auth/AuthContext";
import { accountApi } from "../../api/endpoints";
import { ApiError, errorMessage } from "../../api/client";
import { emailError, passwordOk } from "../../lib/password";
import { formatDate } from "../../lib/format";
import { useDocumentTitle } from "../../lib/useDocumentTitle";

export default function AccountPage() {
  useDocumentTitle("Account");
  const { user } = useAuth();
  if (!user) return null;
  return (
    <AppShell>
      <PageTransition>
        <h1 className="text-3xl font-bold tracking-tight">Account settings</h1>
        <p className="mt-1 text-sm text-fg-subtle">Manage your sign-in details and data.</p>
        <motion.div variants={stagger.container} initial="hidden" animate="show" className="mt-8 space-y-6">
          <motion.div variants={stagger.item}>
            <Section icon={<UserRound className="size-5" />} title="Profile" description="Your username can't be changed.">
              <dl className="grid gap-4 sm:grid-cols-2">
                <div>
                  <dt className="text-xs text-fg-subtle">Username</dt>
                  <dd className="mt-1 font-semibold">{user.username}</dd>
                </div>
                <div>
                  <dt className="text-xs text-fg-subtle">Member since</dt>
                  <dd className="mt-1 font-semibold">{formatDate(user.createdAt)}</dd>
                </div>
              </dl>
            </Section>
          </motion.div>
          <motion.div variants={stagger.item}>
            <EmailSection />
          </motion.div>
          <motion.div variants={stagger.item}>
            <PasswordSection />
          </motion.div>
          <motion.div variants={stagger.item}>
            <DangerSection />
          </motion.div>
        </motion.div>
      </PageTransition>
    </AppShell>
  );
}

function Section({ icon, title, description, children, danger = false }: { icon: ReactNode; title: string; description: string; children: ReactNode; danger?: boolean }) {
  return (
    <Card className={danger ? "border-danger/30" : undefined}>
      <div className="grid grid-cols-1 gap-6 p-6 md:grid-cols-[240px_minmax(0,1fr)]">
        <div>
          <div className={`flex size-10 items-center justify-center rounded-xl ring-1 ${danger ? "bg-danger/10 text-danger ring-danger/25" : "bg-surface-2 text-fg-muted ring-line"}`}>{icon}</div>
          <h2 className="mt-3 font-semibold">{title}</h2>
          <p className="mt-1 text-sm text-fg-subtle">{description}</p>
        </div>
        <div className="min-w-0">{children}</div>
      </div>
    </Card>
  );
}

function EmailSection() {
  const { user, setUser } = useAuth();
  const toast = useToast();
  const [email, setEmail] = useState(user?.email ?? "");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [fields, setFields] = useState<Record<string, string>>({});
  const [busy, setBusy] = useState(false);
  const changed = email.trim() !== (user?.email ?? "");

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    const value = email.trim();
    const invalid = emailError(value);
    if (invalid) return setFields({ email: invalid });
    if (!password) return setFields({ currentPassword: "Confirm with your current password" });
    setBusy(true);
    setError(null);
    setFields({});
    try {
      const u = await accountApi.updateEmail(value || null, password);
      setUser(u);
      setPassword("");
      toast.success(value ? "Email updated" : "Email removed");
    } catch (err) {
      if (err instanceof ApiError) setFields(err.fields);
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Section icon={<Mail className="size-5" />} title="Email" description="Used only for password recovery. Leave blank to remove it.">
      <form onSubmit={submit} className="space-y-4" noValidate>
        {error && <FormAlert>{error}</FormAlert>}
        <Input label="Email address" type="email" autoComplete="email" maxLength={254} value={email} onChange={(e) => setEmail(e.target.value)} error={fields.email} />
        {changed && (
          <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: "auto" }}>
            <Input label="Current password" type="password" autoComplete="current-password" value={password} onChange={(e) => setPassword(e.target.value)} error={fields.currentPassword} />
          </motion.div>
        )}
        <div className="flex justify-end">
          <Button type="submit" disabled={!changed} loading={busy}>
            Save email
          </Button>
        </div>
      </form>
    </Section>
  );
}

function PasswordSection() {
  const { user } = useAuth();
  const toast = useToast();
  const [current, setCurrent] = useState("");
  const [next, setNext] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [fields, setFields] = useState<Record<string, string>>({});
  const [busy, setBusy] = useState(false);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    if (!current) return setFields({ currentPassword: "Enter your current password" });
    if (!passwordOk(next, user?.username ?? "")) return setError("Your new password doesn't meet the requirements.");
    if (next !== confirm) return setFields({ confirm: "Passwords don't match" });
    setBusy(true);
    setError(null);
    setFields({});
    try {
      await accountApi.updatePassword(current, next);
      setCurrent("");
      setNext("");
      setConfirm("");
      toast.success("Password changed", "Other devices have been signed out.");
    } catch (err) {
      if (err instanceof ApiError) setFields(err.fields);
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Section icon={<KeyRound className="size-5" />} title="Password" description="Changing your password signs you out everywhere else.">
      <form onSubmit={submit} className="space-y-4" noValidate>
        {error && <FormAlert>{error}</FormAlert>}
        <Input label="Current password" type="password" autoComplete="current-password" value={current} onChange={(e) => setCurrent(e.target.value)} error={fields.currentPassword} />
        <div className="grid gap-4 sm:grid-cols-2">
          <Input label="New password" type="password" autoComplete="new-password" maxLength={72} value={next} onChange={(e) => setNext(e.target.value)} error={fields.newPassword} />
          <Input
            label="Confirm new password"
            type="password"
            autoComplete="new-password"
            maxLength={72}
            value={confirm}
            onChange={(e) => setConfirm(e.target.value)}
            error={fields.confirm ?? (confirm && confirm !== next ? "Passwords don't match" : null)}
          />
        </div>
        {next && <PasswordRules password={next} username={user?.username ?? ""} />}
        <div className="flex justify-end">
          <Button type="submit" loading={busy} disabled={!current || !next}>
            Update password
          </Button>
        </div>
      </form>
    </Section>
  );
}

function DangerSection() {
  const { setUser } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const [open, setOpen] = useState(false);
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const close = () => {
    if (busy) return;
    setOpen(false);
    setPassword("");
    setError(null);
  };

  const confirmDelete = async (e: FormEvent) => {
    e.preventDefault();
    if (!password) return setError("Enter your password to confirm.");
    setBusy(true);
    setError(null);
    try {
      await accountApi.deleteAccount(password);
      setUser(null);
      toast.info("Account deleted", "All of your drafts have been removed.");
      navigate("/", { replace: true });
    } catch (err) {
      setError(errorMessage(err));
      setBusy(false);
    }
  };

  return (
    <Section icon={<TriangleAlert className="size-5" />} title="Delete account" description="Permanently remove your account and every draft you've made." danger>
      <div className="flex flex-col items-start gap-4 sm:flex-row sm:items-center sm:justify-between">
        <p className="text-sm text-fg-muted">This action is immediate and can't be undone.</p>
        <Button variant="danger" onClick={() => setOpen(true)}>
          Delete account
        </Button>
      </div>
      <Modal
        open={open}
        onClose={close}
        locked={busy}
        size="sm"
        title="Delete your account?"
        description="Your profile and all drafts will be erased permanently."
        footer={
          <>
            <Button variant="ghost" onClick={close} disabled={busy}>
              Cancel
            </Button>
            <Button variant="danger" type="submit" form="delete-account-form" loading={busy}>
              Delete forever
            </Button>
          </>
        }
      >
        <form id="delete-account-form" onSubmit={confirmDelete} className="space-y-4" noValidate>
          {error && <FormAlert>{error}</FormAlert>}
          <Input label="Password" type="password" autoComplete="current-password" icon={<Lock className="size-4" />} value={password} onChange={(e) => setPassword(e.target.value)} data-autofocus />
        </form>
      </Modal>
    </Section>
  );
}
