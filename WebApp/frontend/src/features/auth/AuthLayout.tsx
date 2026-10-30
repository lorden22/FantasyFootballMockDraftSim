import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import { motion } from "motion/react";
import { Logo } from "../../components/Logo";
import { PageTransition } from "../../components/PageTransition";

export function AuthLayout({ title, subtitle, children, footer }: { title: string; subtitle?: ReactNode; children: ReactNode; footer?: ReactNode }) {
  return (
    <PageTransition className="relative flex min-h-dvh flex-col items-center justify-center overflow-x-clip px-4 py-12">
      <div className="grid-bg pointer-events-none absolute inset-0 -z-10" aria-hidden />
      <div className="pointer-events-none absolute left-1/2 top-1/3 -z-10 size-[480px] -translate-x-1/2 -translate-y-1/2 rounded-full bg-accent/10 blur-3xl" aria-hidden />
      <Link to="/" className="mb-8" aria-label="MockDraft Pro home">
        <Logo />
      </Link>
      <motion.div
        initial={{ opacity: 0, y: 12, scale: 0.99 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.35, ease: [0.22, 1, 0.36, 1] }}
        className="card w-full max-w-md p-6 sm:p-8"
      >
        <h1 className="text-2xl font-bold tracking-tight">{title}</h1>
        {subtitle && <p className="mt-1.5 text-sm text-fg-muted">{subtitle}</p>}
        <div className="mt-7">{children}</div>
      </motion.div>
      {footer && <div className="mt-6 text-sm text-fg-muted">{footer}</div>}
    </PageTransition>
  );
}

export function FormAlert({ children, tone = "error" }: { children: ReactNode; tone?: "error" | "success" | "info" }) {
  const tones = {
    error: "border-danger/30 bg-danger/10 text-rose-200",
    success: "border-accent/30 bg-accent/10 text-emerald-200",
    info: "border-info/30 bg-info/10 text-sky-200",
  };
  return (
    <motion.div
      role={tone === "error" ? "alert" : "status"}
      initial={{ opacity: 0, height: 0 }}
      animate={{ opacity: 1, height: "auto" }}
      className={`overflow-hidden rounded-xl border px-4 py-3 text-sm ${tones[tone]}`}
    >
      {children}
    </motion.div>
  );
}
