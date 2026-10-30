import type { ReactNode } from "react";
import { Navigate, useLocation, useSearchParams } from "react-router-dom";
import { useAuth } from "./AuthContext";
import { FullPageLoader } from "../components/FullPageLoader";
import { safeNextPath } from "../lib/safeRedirect";

export function RequireAuth({ children }: { children: ReactNode }) {
  const { user, loading, sessionExpired } = useAuth();
  const location = useLocation();
  if (loading) return <FullPageLoader />;
  if (!user) {
    const next = encodeURIComponent(location.pathname + location.search);
    return <Navigate to={`/login?next=${next}${sessionExpired ? "&expired=1" : ""}`} replace />;
  }
  return <>{children}</>;
}

/** For /login and /register: once signed in, continue to the (validated) `next` page or the dashboard. */
export function GuestOnly({ children }: { children: ReactNode }) {
  const { user, loading } = useAuth();
  const [params] = useSearchParams();
  if (loading) return <FullPageLoader />;
  if (user) return <Navigate to={safeNextPath(params.get("next"))} replace />;
  return <>{children}</>;
}
