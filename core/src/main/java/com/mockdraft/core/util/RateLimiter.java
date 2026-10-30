package com.mockdraft.core.util;

import java.time.Clock;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fixed-window, in-memory rate limiter. Memory is bounded: expired windows are swept and the table is
 * capped, so attackers cannot grow it without limit by varying keys.
 */
public final class RateLimiter {

    private static final int MAX_KEYS = 100_000;

    private record Window(long startMillis, int count) {}

    private final int limit;
    private final long windowMillis;
    private final Clock clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public RateLimiter(int limit, Duration window, Clock clock) {
        this.limit = limit;
        this.windowMillis = window.toMillis();
        this.clock = clock;
    }

    /** Records an attempt and returns true if it is within the limit. */
    public boolean tryAcquire(String key) {
        long now = clock.millis();
        if (windows.size() > MAX_KEYS) {
            sweep(now);
            if (windows.size() > MAX_KEYS) {
                return false; // under attack; fail closed
            }
        }
        Window w = windows.compute(key, (k, existing) ->
                existing == null || now - existing.startMillis() >= windowMillis
                        ? new Window(now, 1)
                        : new Window(existing.startMillis(), existing.count() + 1));
        return w.count() <= limit;
    }

    public void reset(String key) {
        windows.remove(key);
    }

    private void sweep(long now) {
        Iterator<Map.Entry<String, Window>> it = windows.entrySet().iterator();
        while (it.hasNext()) {
            if (now - it.next().getValue().startMillis() >= windowMillis) {
                it.remove();
            }
        }
    }
}
