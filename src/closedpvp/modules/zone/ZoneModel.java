package closedpvp.modules.zone;

import mindustry.Vars;

public final class ZoneModel {
    private final int centerX;
    private final int centerY;

    private final int initialRadius;
    private final int maxRing;

    public ZoneModel(
        int centerX,
        int centerY,
        int initialRadius,
        int maxRing
    ) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.initialRadius = initialRadius;
        this.maxRing = maxRing;
    }

    public static ZoneModel forCurrentWorld() {
        int width = Vars.world.width();
        int height = Vars.world.height();

        int centerX = width / 2;
        int centerY = height / 2;

        int horizontalRadius = Math.max(
            centerX,
            width - 1 - centerX
        );

        int verticalRadius = Math.max(
            centerY,
            height - 1 - centerY
        );

        int maxRing = Math.max(
            horizontalRadius,
            verticalRadius
        );

        return new ZoneModel(
            centerX,
            centerY,
            maxRing,
            maxRing
        );
    }

    public int ring(int tileX, int tileY) {
        return Math.max(
            Math.abs(tileX - centerX),
            Math.abs(tileY - centerY)
        );
    }

    public float radius(float t) {
        float shrinkTime = Math.max(
            0f,
            t - ZoneConfig.START_DELAY_SECONDS
        );

        float radius =
            initialRadius
            - shrinkTime * ZoneConfig.SHRINK_TILES_PER_SECOND;

        return Math.max(
            ZoneConfig.MIN_RADIUS,
            radius
        );
    }

    public float damage(float t, int ring) {
        float depth = ring - radius(t);

        if (depth <= 0f) {
            return 0f;
        }

        return ZoneConfig.REFERENCE_DAMAGE * (float) Math.pow(
            depth / ZoneConfig.REFERENCE_DEPTH,
            ZoneConfig.DAMAGE_EXPONENT
        );
    }

    public int centerX() {
        return centerX;
    }

    public int centerY() {
        return centerY;
    }

    public int initialRadius() {
        return initialRadius;
    }

    public int maxRing() {
        return maxRing;
    }
}
