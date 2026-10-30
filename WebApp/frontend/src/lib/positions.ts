import type { Position } from "../api/types";

export const POSITION_STYLES: Record<Position, { label: string; text: string; bg: string; ring: string; dot: string }> = {
  QB: { label: "Quarterback", text: "text-pos-qb", bg: "bg-pos-qb/12", ring: "ring-pos-qb/30", dot: "bg-pos-qb" },
  RB: { label: "Running back", text: "text-pos-rb", bg: "bg-pos-rb/12", ring: "ring-pos-rb/30", dot: "bg-pos-rb" },
  WR: { label: "Wide receiver", text: "text-pos-wr", bg: "bg-pos-wr/12", ring: "ring-pos-wr/30", dot: "bg-pos-wr" },
  TE: { label: "Tight end", text: "text-pos-te", bg: "bg-pos-te/12", ring: "ring-pos-te/30", dot: "bg-pos-te" },
  K: { label: "Kicker", text: "text-pos-k", bg: "bg-pos-k/12", ring: "ring-pos-k/30", dot: "bg-pos-k" },
  DST: { label: "Defense / ST", text: "text-pos-dst", bg: "bg-pos-dst/12", ring: "ring-pos-dst/30", dot: "bg-pos-dst" },
};

/** Board-cell background tints (stronger than badges, used on the draft grid). */
export const POSITION_CELL: Record<Position, string> = {
  QB: "bg-pos-qb/15 border-pos-qb/35",
  RB: "bg-pos-rb/15 border-pos-rb/35",
  WR: "bg-pos-wr/15 border-pos-wr/35",
  TE: "bg-pos-te/15 border-pos-te/35",
  K: "bg-pos-k/15 border-pos-k/35",
  DST: "bg-pos-dst/15 border-pos-dst/35",
};
