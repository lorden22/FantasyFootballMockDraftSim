import { useEffect, useRef, useState, type ReactNode } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { AnimatePresence, motion } from "motion/react";
import { ChevronDown, LayoutGrid, LogOut, Settings } from "lucide-react";
import { Logo } from "./Logo";
import { useAuth } from "../auth/AuthContext";
import { cn } from "../lib/cn";
import { useToast } from "./ui/Toast";

export function AppShell({ children, wide = false }: { children: ReactNode; wide?: boolean }) {
  return (
    <div className="flex min-h-dvh flex-col">
      <TopNav />
      <main id="main" className={cn("mx-auto w-full flex-1 px-4 pb-16 pt-6 sm:px-6", wide ? "max-w-[1600px]" : "max-w-6xl")}>
        {children}
      </main>
    </div>
  );
}

function TopNav() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!menuOpen) return;
    const onDown = (e: MouseEvent) => {
      if (!menuRef.current?.contains(e.target as Node)) setMenuOpen(false);
    };
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && setMenuOpen(false);
    document.addEventListener("mousedown", onDown);
    document.addEventListener("keydown", onKey);
    return () => {
      document.removeEventListener("mousedown", onDown);
      document.removeEventListener("keydown", onKey);
    };
  }, [menuOpen]);

  const doLogout = async () => {
    setMenuOpen(false);
    await logout();
    toast.info("Signed out", "See you on draft day.");
    navigate("/", { replace: true });
  };

  return (
    <header className="sticky top-0 z-40 border-b border-line/80 bg-bg/75 backdrop-blur-xl">
      <a href="#main" className="sr-only focus:not-sr-only focus:absolute focus:left-4 focus:top-3 focus:z-50 focus:rounded-lg focus:bg-surface-3 focus:px-3 focus:py-2">
        Skip to content
      </a>
      <div className="mx-auto flex h-16 max-w-[1600px] items-center justify-between gap-4 px-4 sm:px-6">
        <div className="flex items-center gap-8">
          <Link to={user ? "/dashboard" : "/"} aria-label="MockDraft Pro home">
            <Logo />
          </Link>
          {user && (
            <nav aria-label="Primary" className="hidden items-center gap-1 sm:flex">
              <NavItem to="/dashboard" icon={<LayoutGrid className="size-4" />}>
                Dashboard
              </NavItem>
              <NavItem to="/account" icon={<Settings className="size-4" />}>
                Account
              </NavItem>
            </nav>
          )}
        </div>

        {user ? (
          <div className="relative" ref={menuRef}>
            <button
              type="button"
              onClick={() => setMenuOpen((v) => !v)}
              aria-haspopup="menu"
              aria-expanded={menuOpen}
              className="flex items-center gap-2.5 rounded-xl border border-line bg-surface/60 py-1.5 pl-1.5 pr-3 text-sm hover:border-line-strong"
            >
              <span className="flex size-7 items-center justify-center rounded-lg bg-gradient-to-br from-accent to-sky-400 text-xs font-bold text-accent-fg uppercase">
                {user.username.slice(0, 2)}
              </span>
              <span className="hidden max-w-32 truncate font-medium sm:inline">{user.username}</span>
              <ChevronDown className={cn("size-4 text-fg-subtle transition-transform", menuOpen && "rotate-180")} aria-hidden />
            </button>
            <AnimatePresence>
              {menuOpen && (
                <motion.div
                  role="menu"
                  initial={{ opacity: 0, y: -6, scale: 0.98 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0, y: -4, scale: 0.98 }}
                  transition={{ duration: 0.14 }}
                  className="absolute right-0 mt-2 w-56 overflow-hidden rounded-xl border border-line-strong bg-surface-2 p-1.5 shadow-2xl"
                >
                  <div className="px-3 py-2">
                    <p className="truncate text-sm font-semibold">{user.username}</p>
                    <p className="truncate text-xs text-fg-subtle">{user.email ?? "No email on file"}</p>
                  </div>
                  <div className="my-1 h-px bg-line" />
                  <MenuLink to="/dashboard" onClick={() => setMenuOpen(false)} icon={<LayoutGrid className="size-4" />}>
                    Dashboard
                  </MenuLink>
                  <MenuLink to="/account" onClick={() => setMenuOpen(false)} icon={<Settings className="size-4" />}>
                    Account settings
                  </MenuLink>
                  <button
                    role="menuitem"
                    type="button"
                    onClick={doLogout}
                    className="flex w-full items-center gap-2.5 rounded-lg px-3 py-2 text-sm text-fg-muted hover:bg-surface-3 hover:text-fg"
                  >
                    <LogOut className="size-4" /> Sign out
                  </button>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        ) : (
          <div className="flex items-center gap-2">
            <Link to="/login" className="rounded-xl px-3 py-2 text-sm font-medium text-fg-muted hover:text-fg">
              Sign in
            </Link>
            <Link to="/register" className="rounded-xl bg-accent px-4 py-2 text-sm font-semibold text-accent-fg hover:bg-[#4ade9f]">
              Get started
            </Link>
          </div>
        )}
      </div>
    </header>
  );
}

function NavItem({ to, icon, children }: { to: string; icon: ReactNode; children: ReactNode }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        cn(
          "flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
          isActive ? "bg-surface-2 text-fg" : "text-fg-subtle hover:text-fg",
        )
      }
    >
      {icon}
      {children}
    </NavLink>
  );
}

function MenuLink({ to, icon, children, onClick }: { to: string; icon: ReactNode; children: ReactNode; onClick: () => void }) {
  return (
    <Link role="menuitem" to={to} onClick={onClick} className="flex items-center gap-2.5 rounded-lg px-3 py-2 text-sm text-fg-muted hover:bg-surface-3 hover:text-fg">
      {icon}
      {children}
    </Link>
  );
}
