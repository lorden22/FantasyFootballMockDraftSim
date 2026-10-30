import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { ApiError, ensureCsrf, onUnauthorized } from "../api/client";
import { authApi } from "../api/endpoints";
import type { User } from "../api/types";

type AuthState = {
  user: User | null;
  /** True until the initial /api/auth/me check finishes. */
  loading: boolean;
  /** Set when a protected call returned 401, so the login screen can explain why. */
  sessionExpired: boolean;
  login: (username: string, password: string) => Promise<User>;
  register: (username: string, password: string, email: string | null) => Promise<User>;
  logout: () => Promise<void>;
  setUser: (u: User | null) => void;
};

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [sessionExpired, setSessionExpired] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      await ensureCsrf();
      try {
        const me = await authApi.me();
        if (!cancelled) setUser(me);
      } catch (e) {
        if (!cancelled && !(e instanceof ApiError && e.status === 401)) setUser(null);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  // A protected call returned 401: the session expired. Dropping the user makes <RequireAuth> send them
  // to /login (with a `next` back to where they were).
  useEffect(() => {
    onUnauthorized(() => {
      setSessionExpired(true);
      setUser(null);
    });
    return () => onUnauthorized(null);
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    const u = await authApi.login(username, password);
    await ensureCsrf(true); // token rotates on authentication
    setSessionExpired(false);
    setUser(u);
    return u;
  }, []);

  const register = useCallback(async (username: string, password: string, email: string | null) => {
    const u = await authApi.register(username, password, email);
    await ensureCsrf(true);
    setSessionExpired(false);
    setUser(u);
    return u;
  }, []);

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      setUser(null);
      await ensureCsrf(true);
    }
  }, []);

  const value = useMemo(
    () => ({ user, loading, sessionExpired, login, register, logout, setUser }),
    [user, loading, sessionExpired, login, register, logout],
  );
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthState {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside <AuthProvider>");
  return ctx;
}
