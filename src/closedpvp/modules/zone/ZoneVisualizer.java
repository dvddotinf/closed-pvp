package closedpvp.modules.zone;

import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.game.MapObjectives.TextureMarker;
import mindustry.gen.Call;
import mindustry.graphics.Layer;
import mindustry.logic.LMarkerControl;

public final class ZoneVisualizer {
    private static final int TOP_ID = -31402;
    private static final int BOTTOM_ID = -31403;
    private static final int LEFT_ID = -31404;
    private static final int RIGHT_ID = -31405;

    /*
     * Shield shader expects an opaque mask.
     *
     * Do not lower alpha here:
     * transparency is produced by the shield shader itself.
     */
    private static final Color ZONE_COLOR =
        Color.scarlet.cpy();

    private static boolean initialized;

    public static void initialize(ZoneModel zone, float t) {
        remove();

        createZonePart(TOP_ID);
        createZonePart(BOTTOM_ID);
        createZonePart(LEFT_ID);
        createZonePart(RIGHT_ID);

        initialized = true;

        update(zone, t);
    }

    public static void update(ZoneModel zone, float t) {
        if (!initialized) {
            return;
        }

        updateZone(
            zone,
            zone.radius(t)
        );
    }

    public static void remove() {
        if (!initialized) {
            return;
        }

        Call.removeMarker(TOP_ID);
        Call.removeMarker(BOTTOM_ID);
        Call.removeMarker(LEFT_ID);
        Call.removeMarker(RIGHT_ID);

        initialized = false;
    }

    /*
     * Creates one opaque white rectangle tinted red
     * and places it onto Mindustry's shield render layer.
     *
     * The client then processes this layer through the
     * normal animated shield shader.
     */
    private static void createZonePart(int id) {
        TextureMarker marker = new TextureMarker();

        marker.setTexture("white");
        marker.color = ZONE_COLOR.cpy();

        Call.createMarker(id, marker);

        Call.updateMarker(
            id,
            LMarkerControl.drawLayer,
            Layer.shields,
            Double.NaN,
            Double.NaN
        );
    }

    /*
     * The dangerous area is represented by four rectangles:
     *
     * +---------------------------+
     * |            TOP            |
     * +------+-------------+------+
     * | LEFT |             |RIGHT |
     * |      |  SAFE ZONE  |      |
     * |      |             |      |
     * +------+-------------+------+
     * |          BOTTOM           |
     * +---------------------------+
     *
     * Top/bottom span the whole map.
     * Left/right only span between them, preventing
     * rectangles from overlapping in the corners.
     */
    private static void updateZone(
        ZoneModel zone,
        float radius
    ) {
        float worldWidth = Vars.world.width();
        float worldHeight = Vars.world.height();

        float minX = Mathf.clamp(
            zone.centerX() - radius,
            0f,
            worldWidth
        );

        float maxX = Mathf.clamp(
            zone.centerX() + radius,
            0f,
            worldWidth
        );

        float minY = Mathf.clamp(
            zone.centerY() - radius,
            0f,
            worldHeight
        );

        float maxY = Mathf.clamp(
            zone.centerY() + radius,
            0f,
            worldHeight
        );

        // Everything above the safe zone.
        setRectangle(
            TOP_ID,
            worldWidth / 2f,
            (maxY + worldHeight) / 2f,
            worldWidth,
            worldHeight - maxY
        );

        // Everything below the safe zone.
        setRectangle(
            BOTTOM_ID,
            worldWidth / 2f,
            minY / 2f,
            worldWidth,
            minY
        );

        // Everything to the left of the safe zone,
        // excluding top/bottom regions.
        setRectangle(
            LEFT_ID,
            minX / 2f,
            (minY + maxY) / 2f,
            minX,
            maxY - minY
        );

        // Everything to the right of the safe zone,
        // excluding top/bottom regions.
        setRectangle(
            RIGHT_ID,
            (maxX + worldWidth) / 2f,
            (minY + maxY) / 2f,
            worldWidth - maxX,
            maxY - minY
        );
    }

    /*
     * Marker controls use tile coordinates here.
     *
     * Width/height must not be exactly zero:
     * TextureMarker interprets zero size specially.
     */
    private static void setRectangle(
        int id,
        float centerX,
        float centerY,
        float width,
        float height
    ) {
        width = Math.max(
            width,
            0.001f
        );

        height = Math.max(
            height,
            0.001f
        );

        Call.updateMarker(
            id,
            LMarkerControl.pos,
            centerX,
            centerY,
            Double.NaN
        );

        Call.updateMarker(
            id,
            LMarkerControl.textureSize,
            width,
            height,
            Double.NaN
        );
    }

    private ZoneVisualizer() {
    }
}
