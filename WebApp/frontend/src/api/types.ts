// Mirrors docs/API.md — keep in sync with the backend contract.

export type Position = "QB" | "RB" | "WR" | "TE" | "K" | "DST";
export const POSITIONS: readonly Position[] = ["QB", "RB", "WR", "TE", "K", "DST"] as const;

export type User = { username: string; email: string | null; createdAt: string };

export type Player = {
  rank: number;
  name: string;
  position: Position;
  projectedPoints: number;
  adp: string;
};

export type Team = { slot: number; name: string; isUser: boolean };

export type Pick = {
  overall: number;
  round: number;
  pickInRound: number;
  slot: number;
  teamName: string;
  player: Player;
};

export type DraftStatus = "IN_PROGRESS" | "COMPLETE";

export type DraftSummary = {
  id: number;
  teamName: string;
  numTeams: number;
  rounds: number;
  userSlot: number;
  status: DraftStatus;
  createdAt: string;
  completedAt: string | null;
  picksMade: number;
  totalPicks: number;
};

export type CurrentPick = {
  overall: number;
  round: number;
  pickInRound: number;
  slot: number;
  teamName: string;
  isUser: boolean;
};

export type NextUserPick = { overall: number; round: number; pickInRound: number; picksUntil: number };

export type DraftState = DraftSummary & {
  teams: Team[];
  picks: Pick[];
  currentPick: CurrentPick | null;
  nextUserPick: NextUserPick | null;
};

export type RosterLabel = "QB" | "RB1" | "RB2" | "WR1" | "WR2" | "TE" | "FLEX" | "K" | "DST" | "BN";
export type RosterSlot = { label: RosterLabel; pick: Pick | null };

export type Roster = {
  slot: number;
  teamName: string;
  isUser: boolean;
  starters: RosterSlot[];
  bench: RosterSlot[];
  projectedStarterPoints: number;
};

export type ApiErrorBody = {
  status: number;
  error: string;
  message: string;
  fields?: Record<string, string>;
};
