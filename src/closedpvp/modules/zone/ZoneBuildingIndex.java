package closedpvp.modules.zone;

import arc.struct.IntSet;
import mindustry.gen.Building;
import mindustry.gen.Groups;

public final class ZoneBuildingIndex {
    private final ZoneModel zone;
    private final IntSet[] rings;

    public ZoneBuildingIndex(ZoneModel zone) {
        this.zone = zone;

        rings = new IntSet[zone.maxRing() + 1];

        for (int i = 0; i < rings.length; i++) {
            rings[i] = new IntSet();
        }
    }

    public void rebuild() {
        clear();

        Groups.build.each(this::track);
    }

    public void track(Building build) {
        if (build == null || !build.isValid()) {
            return;
        }

        int ring = zone.ring(
            build.tileX(),
            build.tileY()
        );

        if (ring < 0 || ring >= rings.length) {
            return;
        }

        rings[ring].add(build.pos());
    }

    public IntSet ring(int ring) {
        return rings[ring];
    }

    public int ringCount() {
        return rings.length;
    }

    public void clear() {
        for (IntSet ring : rings) {
            ring.clear();
        }
    }
}
