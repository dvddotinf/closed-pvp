package closedpvp.modules.zone;

import arc.graphics.Color;
import mindustry.Vars;
import mindustry.game.MapObjectives.ShapeMarker;
import mindustry.gen.Call;
import mindustry.logic.LMarkerControl;

public final class ZoneVisualizer {
    private static final int MARKER_ID = -31401;

    // Для квадрата radius у ShapeMarker — расстояние
    // от центра до вершины, а наш radius — до стороны.
    private static final float SQRT_2 = 1.41421356f;

    private static boolean initialized;

    public static void initialize(ZoneModel zone, float t) {
        remove();

        float radius = markerRadius(zone.radius(t));

        ShapeMarker marker = new ShapeMarker(
            zone.centerX() * Vars.tilesize,
            zone.centerY() * Vars.tilesize,
            radius,
            45f
        );

        marker.sides = 4;
        marker.fill = false;
        marker.outline = false;
        marker.stroke = 2f;
        marker.color = Color.scarlet.cpy();

        Call.createMarker(MARKER_ID, marker);

        initialized = true;
    }

    public static void update(ZoneModel zone, float t) {
        if (!initialized) {
            return;
        }

        Call.updateMarker(
            MARKER_ID,
            LMarkerControl.radius,
            markerRadius(zone.radius(t)),
            Double.NaN,
            Double.NaN
        );
    }

    public static void remove() {
        if (!initialized) {
            return;
        }

        Call.removeMarker(MARKER_ID);
        initialized = false;
    }

    private static float markerRadius(float zoneRadius) {
        return zoneRadius * Vars.tilesize * SQRT_2;
    }

    private ZoneVisualizer() {
    }
}
