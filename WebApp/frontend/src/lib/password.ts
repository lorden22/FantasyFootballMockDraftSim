const COMMON = new Set([
  "password", "password1", "password123", "12345678", "123456789", "1234567890", "qwerty123", "qwertyuiop",
  "11111111", "iloveyou", "football", "football1", "letmein1", "welcome1", "abc12345", "baseball", "sunshine",
  "admin123", "passw0rd", "fantasy1", "superbowl", "00000000", "87654321",
]);

export type PasswordRule = { id: string; label: string; ok: boolean };

export function passwordRules(password: string, username: string): PasswordRule[] {
  const bytes = new TextEncoder().encode(password).length;
  return [
    { id: "len", label: "8–72 characters", ok: password.length >= 8 && bytes <= 72 },
    { id: "user", label: "Different from your username", ok: password.length > 0 && password.toLowerCase() !== username.trim().toLowerCase() },
    { id: "common", label: "Not a common password", ok: password.length > 0 && !COMMON.has(password.toLowerCase()) },
  ];
}

export function passwordOk(password: string, username: string): boolean {
  return passwordRules(password, username).every((r) => r.ok);
}

export function usernameError(username: string): string | null {
  if (!username) return "Username is required";
  if (!/^[A-Za-z0-9_]{3,30}$/.test(username)) return "3–30 characters: letters, numbers and underscores";
  return null;
}

export function emailError(email: string): string | null {
  if (!email) return null;
  if (email.length > 254 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return "Enter a valid email address";
  return null;
}
