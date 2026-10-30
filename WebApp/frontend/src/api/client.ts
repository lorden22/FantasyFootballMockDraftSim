import type { ApiErrorBody } from "./types";

const CSRF_COOKIE = "XSRF-TOKEN";
const CSRF_HEADER = "X-XSRF-TOKEN";
const MUTATING = new Set(["POST", "PUT", "PATCH", "DELETE"]);

export class ApiError extends Error {
  readonly status: number;
  readonly fields: Record<string, string>;

  constructor(status: number, message: string, fields: Record<string, string> = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fields = fields;
  }
}

/** Handler invoked when a protected call returns 401 (session expired / signed out elsewhere). */
let unauthorizedHandler: (() => void) | null = null;
export function onUnauthorized(handler: (() => void) | null): void {
  unauthorizedHandler = handler;
}

function readCookie(name: string): string | null {
  const prefix = `${name}=`;
  for (const part of document.cookie.split(";")) {
    const c = part.trim();
    if (c.startsWith(prefix)) return decodeURIComponent(c.slice(prefix.length));
  }
  return null;
}

let csrfPromise: Promise<void> | null = null;

/** Make sure the CSRF cookie exists. Safe to call repeatedly. */
export function ensureCsrf(force = false): Promise<void> {
  if (!force && readCookie(CSRF_COOKIE)) return Promise.resolve();
  if (!csrfPromise || force) {
    csrfPromise = fetch("/api/auth/csrf", { credentials: "same-origin", cache: "no-store" })
      .then(() => undefined)
      .catch(() => undefined)
      .finally(() => {
        csrfPromise = null;
      });
  }
  return csrfPromise;
}

type RequestOptions = {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
  /** When false, a 401 will not trigger the global sign-out redirect (used by login / me). */
  authRedirect?: boolean;
  signal?: AbortSignal;
};

async function parseError(res: Response): Promise<ApiError> {
  let body: Partial<ApiErrorBody> | null = null;
  try {
    body = (await res.json()) as Partial<ApiErrorBody>;
  } catch {
    body = null;
  }
  const fallback =
    res.status === 429
      ? "Too many attempts. Please wait a minute and try again."
      : res.status >= 500
        ? "Something went wrong on our end. Please try again."
        : res.statusText || "Request failed";
  return new ApiError(res.status, body?.message || fallback, body?.fields ?? {});
}

async function send(path: string, opts: RequestOptions, retried: boolean): Promise<Response> {
  const method = opts.method ?? "GET";
  const headers: Record<string, string> = { Accept: "application/json" };
  if (opts.body !== undefined) headers["Content-Type"] = "application/json";
  if (MUTATING.has(method)) {
    await ensureCsrf();
    const token = readCookie(CSRF_COOKIE);
    if (token) headers[CSRF_HEADER] = token;
  }

  let res: Response;
  try {
    res = await fetch(path, {
      method,
      headers,
      body: opts.body === undefined ? undefined : JSON.stringify(opts.body),
      credentials: "same-origin",
      cache: "no-store",
      signal: opts.signal,
    });
  } catch (e) {
    if ((e as Error).name === "AbortError") throw e;
    throw new ApiError(0, "Can't reach the server. Check your connection and try again.");
  }

  // A 403 on a mutating call usually means a stale/missing CSRF token: refresh once and retry.
  if (res.status === 403 && MUTATING.has(method) && !retried) {
    await ensureCsrf(true);
    return send(path, opts, true);
  }
  return res;
}

export async function api<T>(path: string, opts: RequestOptions = {}): Promise<T> {
  const res = await send(path, opts, false);
  if (!res.ok) {
    const err = await parseError(res);
    if (res.status === 401 && opts.authRedirect !== false) unauthorizedHandler?.();
    throw err;
  }
  if (res.status === 204 || res.status === 202) return undefined as T;
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export function errorMessage(e: unknown): string {
  if (e instanceof ApiError) return e.message;
  if (e instanceof Error) return e.message;
  return "Something went wrong.";
}
