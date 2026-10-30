import { api } from "./client";
import type { DraftState, DraftSummary, Player, Roster, User } from "./types";

export const authApi = {
  me: () => api<User>("/api/auth/me", { authRedirect: false }),
  login: (username: string, password: string) =>
    api<User>("/api/auth/login", { method: "POST", body: { username, password }, authRedirect: false }),
  register: (username: string, password: string, email: string | null) =>
    api<User>("/api/auth/register", { method: "POST", body: { username, password, email }, authRedirect: false }),
  logout: () => api<void>("/api/auth/logout", { method: "POST", authRedirect: false }),
  requestReset: (email: string) =>
    api<void>("/api/auth/password-reset", { method: "POST", body: { email }, authRedirect: false }),
  validateReset: (token: string) =>
    api<{ valid: boolean }>("/api/auth/password-reset/validate", { method: "POST", body: { token }, authRedirect: false }),
  confirmReset: (token: string, password: string) =>
    api<void>("/api/auth/password-reset/confirm", { method: "POST", body: { token, password }, authRedirect: false }),
};

export const accountApi = {
  updateEmail: (email: string | null, currentPassword: string) =>
    api<User>("/api/account/email", { method: "PUT", body: { email, currentPassword } }),
  updatePassword: (currentPassword: string, newPassword: string) =>
    api<void>("/api/account/password", { method: "PUT", body: { currentPassword, newPassword } }),
  deleteAccount: (password: string) => api<void>("/api/account/delete", { method: "POST", body: { password } }),
};

export const draftApi = {
  list: () => api<DraftSummary[]>("/api/drafts"),
  create: (teamName: string, numTeams: number, draftPosition: number | null) =>
    api<DraftState>("/api/drafts", { method: "POST", body: { teamName, numTeams, draftPosition } }),
  get: (id: number) => api<DraftState>(`/api/drafts/${id}`),
  remove: (id: number) => api<void>(`/api/drafts/${id}`, { method: "DELETE" }),
  available: (id: number) => api<Player[]>(`/api/drafts/${id}/available`),
  pick: (id: number, playerRank: number) =>
    api<DraftState>(`/api/drafts/${id}/picks`, { method: "POST", body: { playerRank } }),
  autoPick: (id: number) => api<DraftState>(`/api/drafts/${id}/auto-pick`, { method: "POST" }),
  simulate: (id: number) => api<DraftState>(`/api/drafts/${id}/simulate`, { method: "POST" }),
  roster: (id: number, slot: number) => api<Roster>(`/api/drafts/${id}/teams/${slot}/roster`),
};
