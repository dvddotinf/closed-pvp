package closedpvp.modules.zone;

public final class ZoneConfig {
    // До этого момента зона вообще не двигается.
    public static final float START_DELAY_SECONDS = 600f;

    // 0.1 tile/s = 1 tile за 10 секунд.
    public static final float SHRINK_TILES_PER_SECOND = 0.121212f;

    // Финальный half-size квадрата.
    public static final float MIN_RADIUS = 20f;

    public static final float REFERENCE_DEPTH = 15f;
    public static final float REFERENCE_DAMAGE = 0.02f;
    public static final float DAMAGE_EXPONENT = 2.322f;

    private ZoneConfig() {
    }
}
