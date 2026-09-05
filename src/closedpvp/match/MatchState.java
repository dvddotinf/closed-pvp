package closedpvp.match;

import arc.util.Time;

public final class MatchState {
    private static long startedAtMillis = -1L;

    public static void start() {
        startedAtMillis = Time.millis();
    }

    public static void stop() {
        startedAtMillis = -1L;
    }

    public static boolean active() {
        return startedAtMillis >= 0L;
    }

    public static long startedAtMillis() {
        return startedAtMillis;
    }

    public static float elapsedSeconds() {
        if (!active()) {
            return 0f;
        }

        return (Time.millis() - startedAtMillis) / 1000f;
    }

    private MatchState() {
    }
}
