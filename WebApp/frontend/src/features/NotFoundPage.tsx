import { Link } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { Logo } from "../components/Logo";
import { PageTransition } from "../components/PageTransition";
import { useDocumentTitle } from "../lib/useDocumentTitle";

export function NotFoundPage() {
  useDocumentTitle("Page not found");
  return (
    <PageTransition className="relative flex min-h-dvh flex-col items-center justify-center px-6 text-center">
      <div className="grid-bg pointer-events-none absolute inset-0" aria-hidden />
      <Link to="/" className="relative mb-10">
        <Logo />
      </Link>
      <p className="relative text-sm font-semibold tracking-widest text-accent uppercase">Flag on the play</p>
      <h1 className="relative mt-3 text-6xl font-black tracking-tight tabular sm:text-8xl">404</h1>
      <p className="relative mt-4 max-w-md text-fg-muted">That page went undrafted. It may have moved, or the link is wrong.</p>
      <Link to="/" className="relative mt-8 inline-flex items-center gap-2 rounded-xl border border-line-strong px-4 py-2.5 text-sm font-semibold hover:bg-surface-2">
        <ArrowLeft className="size-4" /> Back to home
      </Link>
    </PageTransition>
  );
}
