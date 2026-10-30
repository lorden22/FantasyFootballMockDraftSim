import { useEffect } from "react";

export function useDocumentTitle(title: string): void {
  useEffect(() => {
    document.title = title ? `${title} · MockDraft Pro` : "MockDraft Pro — 2026 Fantasy Football Mock Drafts";
  }, [title]);
}
