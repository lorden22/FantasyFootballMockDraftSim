type ClassValue = string | number | bigint | boolean | null | undefined;

/** Join truthy class names. */
export function cn(...classes: ClassValue[]): string {
  return classes.filter((c) => typeof c === "string" && c.length > 0).join(" ");
}
