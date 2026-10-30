import { motion } from "motion/react";
import { LogoMark } from "./Logo";

export function FullPageLoader() {
  return (
    <div className="flex min-h-dvh items-center justify-center" role="status" aria-label="Loading">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 0.15 }}
        className="flex flex-col items-center gap-4"
      >
        <motion.div animate={{ rotate: [0, -8, 8, 0] }} transition={{ repeat: Infinity, duration: 1.6, ease: "easeInOut" }}>
          <LogoMark className="size-12" />
        </motion.div>
        <div className="h-1 w-28 overflow-hidden rounded-full bg-surface-2">
          <motion.div
            className="h-full w-1/2 rounded-full bg-accent"
            animate={{ x: ["-100%", "200%"] }}
            transition={{ repeat: Infinity, duration: 1.1, ease: "easeInOut" }}
          />
        </div>
      </motion.div>
    </div>
  );
}
