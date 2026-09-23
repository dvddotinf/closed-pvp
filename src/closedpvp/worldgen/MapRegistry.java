package closedpvp.worldgen;

import arc.struct.Seq;
import closedpvp.worldgen.maps.prototype.PrototypeMap;

public final class MapRegistry {
    private static final Seq<GeneratedMapDefinition> maps = Seq.with(
        PrototypeMap.definition()
    );

    public static Seq<GeneratedMapDefinition> all() {
        return maps;
    }

    private MapRegistry() {
    }
}
