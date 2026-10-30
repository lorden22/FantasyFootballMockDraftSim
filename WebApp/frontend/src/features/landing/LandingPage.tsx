import { Link } from "react-router-dom";
import { motion } from "motion/react";
import { ArrowRight, Bot, ChartColumn, History, ShieldCheck, Sparkles, Timer, Trophy, Users } from "lucide-react";
import { AppShell } from "../../components/AppShell";
import { PageTransition, stagger } from "../../components/PageTransition";
import { PositionBadge } from "../../components/ui/Badge";
import { useAuth } from "../../auth/AuthContext";
import { useDocumentTitle } from "../../lib/useDocumentTitle";
import type { Position } from "../../api/types";

const FEATURES = [
  { icon: Bot, title: "Smart CPU managers", body: "Opponents draft like real people — they reach, fill positional needs and never take a kicker in round three." },
  { icon: Timer, title: "Draft in minutes", body: "Simulate straight to your pick, auto-draft when you're stuck, and finish a 15-round draft over a coffee." },
  { icon: ChartColumn, title: "Live draft board", body: "Every pick lands on a color-coded snake board so you can spot position runs before they happen." },
  { icon: Users, title: "4 to 20 team leagues", body: "Practice for your exact league size and draft slot — or let us randomize it like draft night." },
  { icon: History, title: "Full draft history", body: "Every mock is saved. Revisit boards, rosters and pick logs to learn what works from each slot." },
  { icon: ShieldCheck, title: "Private by design", body: "Secure accounts, encrypted sessions and no ads. Your draft strategy stays yours." },
];

const STEPS = [
  { n: "01", title: "Set up your league", body: "Name your team, choose league size and draft slot." },
  { n: "02", title: "Draft on the clock", body: "Search and filter 500+ ranked players with projections and ADP." },
  { n: "03", title: "Review your roster", body: "See your starting lineup, bench and every team's haul." },
];

const DEMO_PICKS: { round: string; name: string; pos: Position; team: string }[] = [
  { round: "1.01", name: "Christian McCaffrey", pos: "RB", team: "Gridiron Gurus" },
  { round: "1.02", name: "Justin Jefferson", pos: "WR", team: "End Zone Elite" },
  { round: "1.03", name: "Ja'Marr Chase", pos: "WR", team: "Your Team" },
  { round: "1.04", name: "Travis Kelce", pos: "TE", team: "Blitz Brigade" },
  { round: "1.05", name: "Josh Allen", pos: "QB", team: "Hail Marys" },
];

export function LandingPage() {
  useDocumentTitle("");
  const { user } = useAuth();
  const primary = user ? { to: "/dashboard", label: "Go to dashboard" } : { to: "/register", label: "Start drafting free" };

  return (
    <AppShell>
      <PageTransition>
        {/* Hero */}
        <section className="relative isolate overflow-x-clip pb-20 pt-10 sm:pt-16">
          <div className="grid-bg pointer-events-none absolute inset-x-0 -top-24 -z-10 h-[520px]" aria-hidden />
          <div className="grid items-center gap-12 lg:grid-cols-[1.1fr_1fr]">
            <motion.div variants={stagger.container} initial="hidden" animate="show">
              <motion.div variants={stagger.item}>
                <span className="inline-flex items-center gap-2 rounded-full border border-accent/30 bg-accent/10 px-3 py-1 text-xs font-semibold text-accent">
                  <Sparkles className="size-3.5" aria-hidden /> 2026 season rankings are live
                </span>
              </motion.div>
              <motion.h1 variants={stagger.item} className="mt-6 text-4xl font-black leading-[1.05] tracking-tight sm:text-6xl">
                Win your draft
                <br />
                <span className="text-gradient">before it starts.</span>
              </motion.h1>
              <motion.p variants={stagger.item} className="mt-6 max-w-xl text-lg text-fg-muted">
                MockDraft Pro puts you in a realistic fantasy football draft room against intelligent CPU managers.
                Practice every slot, test every strategy, and walk into draft night ready.
              </motion.p>
              <motion.div variants={stagger.item} className="mt-8 flex flex-wrap items-center gap-3">
                <Link
                  to={primary.to}
                  className="group inline-flex h-12 items-center gap-2 rounded-xl bg-accent px-6 font-semibold text-accent-fg shadow-[0_10px_30px_-10px_var(--color-accent-glow)] transition hover:bg-[#4ade9f]"
                >
                  {primary.label}
                  <ArrowRight className="size-4 transition-transform group-hover:translate-x-0.5" aria-hidden />
                </Link>
                {!user && (
                  <Link to="/login" className="inline-flex h-12 items-center rounded-xl border border-line-strong px-6 font-semibold text-fg hover:bg-surface-2">
                    I have an account
                  </Link>
                )}
              </motion.div>
              <motion.dl variants={stagger.item} className="mt-10 grid max-w-md grid-cols-3 gap-6">
                {[
                  ["540+", "ranked players"],
                  ["15", "round drafts"],
                  ["< 5 min", "per mock"],
                ].map(([v, l]) => (
                  <div key={l}>
                    <dt className="sr-only">{l}</dt>
                    <dd className="text-2xl font-bold tabular">{v}</dd>
                    <dd className="text-xs text-fg-subtle">{l}</dd>
                  </div>
                ))}
              </motion.dl>
            </motion.div>

            <HeroBoard />
          </div>
        </section>

        {/* Features */}
        <section aria-labelledby="features-heading" className="py-16">
          <div className="max-w-2xl">
            <p className="text-sm font-semibold tracking-widest text-accent uppercase">Built for draft prep</p>
            <h2 id="features-heading" className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
              Everything you need to draft with confidence
            </h2>
          </div>
          <motion.div
            className="mt-10 grid gap-4 sm:grid-cols-2 lg:grid-cols-3"
            variants={stagger.container}
            initial="hidden"
            whileInView="show"
            viewport={{ once: true, margin: "-80px" }}
          >
            {FEATURES.map(({ icon: Icon, title, body }) => (
              <motion.article
                key={title}
                variants={stagger.item}
                whileHover={{ y: -4 }}
                className="card group p-6 transition-colors hover:border-line-strong"
              >
                <div className="flex size-11 items-center justify-center rounded-xl bg-accent/10 text-accent ring-1 ring-accent/25 transition group-hover:shadow-[var(--shadow-glow)]">
                  <Icon className="size-5" aria-hidden />
                </div>
                <h3 className="mt-5 font-semibold">{title}</h3>
                <p className="mt-2 text-sm leading-relaxed text-fg-muted">{body}</p>
              </motion.article>
            ))}
          </motion.div>
        </section>

        {/* How it works */}
        <section aria-labelledby="how-heading" className="py-16">
          <h2 id="how-heading" className="text-3xl font-bold tracking-tight sm:text-4xl">
            How it works
          </h2>
          <ol className="mt-10 grid gap-4 md:grid-cols-3">
            {STEPS.map((s, i) => (
              <motion.li
                key={s.n}
                initial={{ opacity: 0, y: 16 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ delay: i * 0.08 }}
                className="relative rounded-2xl border border-line bg-gradient-to-b from-surface to-bg-soft p-6"
              >
                <span className="text-4xl font-black text-surface-3 tabular">{s.n}</span>
                <h3 className="mt-3 font-semibold">{s.title}</h3>
                <p className="mt-1.5 text-sm text-fg-muted">{s.body}</p>
              </motion.li>
            ))}
          </ol>
        </section>

        {/* CTA */}
        <section className="py-16">
          <div className="relative overflow-hidden rounded-3xl border border-accent/25 bg-gradient-to-br from-emerald-500/15 via-surface to-sky-500/10 px-6 py-14 text-center sm:px-12">
            <div className="grid-bg pointer-events-none absolute inset-0 opacity-60" aria-hidden />
            <Trophy className="relative mx-auto size-10 text-accent" aria-hidden />
            <h2 className="relative mt-5 text-3xl font-bold tracking-tight sm:text-4xl">Your league-mates are already practicing.</h2>
            <p className="relative mx-auto mt-3 max-w-xl text-fg-muted">Create a free account and run your first mock draft in under a minute.</p>
            <Link
              to={primary.to}
              className="relative mt-8 inline-flex h-12 items-center gap-2 rounded-xl bg-accent px-6 font-semibold text-accent-fg hover:bg-[#4ade9f]"
            >
              {primary.label} <ArrowRight className="size-4" aria-hidden />
            </Link>
          </div>
        </section>

        <footer className="flex flex-col items-center justify-between gap-3 border-t border-line pt-8 text-xs text-fg-subtle sm:flex-row">
          <p>© 2026 MockDraft Pro. Not affiliated with the NFL.</p>
          <p>Projections are estimates for entertainment and practice purposes.</p>
        </footer>
      </PageTransition>
    </AppShell>
  );
}

function HeroBoard() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 24, rotate: 1.5 }}
      animate={{ opacity: 1, y: 0, rotate: 0 }}
      transition={{ duration: 0.6, delay: 0.15, ease: [0.22, 1, 0.36, 1] }}
      className="relative"
      aria-hidden
    >
      <div className="absolute -inset-6 -z-10 rounded-[2rem] bg-accent/10 blur-3xl" />
      <div className="card overflow-hidden">
        <div className="flex items-center justify-between border-b border-line px-5 py-4">
          <div>
            <p className="text-xs font-semibold tracking-widest text-fg-subtle uppercase">Round 1 · 12 teams</p>
            <p className="mt-0.5 font-semibold">Live draft room</p>
          </div>
          <span className="flex items-center gap-2 rounded-full bg-accent/10 px-3 py-1 text-xs font-semibold text-accent ring-1 ring-accent/30">
            <span className="size-2 animate-pulse rounded-full bg-accent" /> On the clock
          </span>
        </div>
        <ul className="divide-y divide-line">
          {DEMO_PICKS.map((p, i) => (
            <motion.li
              key={p.round}
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.5 + i * 0.12 }}
              className={`flex items-center gap-4 px-5 py-3.5 ${p.team === "Your Team" ? "bg-accent/[0.06]" : ""}`}
            >
              <span className="w-10 text-xs font-semibold text-fg-subtle tabular">{p.round}</span>
              <PositionBadge position={p.pos} />
              <span className="flex-1 truncate text-sm font-medium">{p.name}</span>
              <span className={`text-xs ${p.team === "Your Team" ? "font-semibold text-accent" : "text-fg-subtle"}`}>{p.team}</span>
            </motion.li>
          ))}
        </ul>
        <div className="border-t border-line bg-bg-soft/60 px-5 py-4">
          <div className="flex items-center justify-between text-xs text-fg-subtle">
            <span>Your next pick</span>
            <span className="font-semibold text-fg tabular">2.10 · in 16 picks</span>
          </div>
          <div className="mt-2 h-1.5 overflow-hidden rounded-full bg-surface-3">
            <motion.div className="h-full rounded-full bg-gradient-to-r from-accent to-sky-400" initial={{ width: "0%" }} animate={{ width: "28%" }} transition={{ delay: 1.2, duration: 1 }} />
          </div>
        </div>
      </div>
    </motion.div>
  );
}
