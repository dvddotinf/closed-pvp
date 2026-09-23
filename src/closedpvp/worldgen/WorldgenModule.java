package closedpvp.worldgen;

import arc.files.Fi;
import arc.struct.StringMap;
import arc.util.Log;
import mindustry.Vars;
import mindustry.maps.Map;
import mindustry.world.WorldParams;

import java.util.concurrent.ThreadLocalRandom;

import arc.Events;
import arc.struct.Seq;

import mindustry.game.EventType.DataPatchLoadEvent;
import mindustry.mod.data.DataAsset;

public final class WorldgenModule {
    private static boolean registered;

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;

        for (GeneratedMapDefinition definition : MapRegistry.all()) {
            register(definition);
        }
    }

    private static void register(
        GeneratedMapDefinition definition
    ) {
        Fi file = Vars.customMapDirectory
            .child("__closed-pvp-generated__")
            .child(definition.id() + ".msav");

        StringMap tags = StringMap.of(
            "name", definition.name(),
            "author", "Closed PVP",
            "description", definition.description()
        );

        Map map = new Map(
            file,
            definition.width(),
            definition.height(),
            tags,
            true
        );

        Vars.maps.all().add(map);
        Vars.maps.all().sort();

        Vars.world.addMapLoader(
            map,
            () -> load(map, definition)
        );

        Log.info(
            "[ClosedPVP] Registered generated map '@' (@x@).",
            definition.name(),
            definition.width(),
            definition.height()
        );
    }

    private static void load(
	    Map map,
	    GeneratedMapDefinition definition
	) {
	    int seed =
		ThreadLocalRandom.current().nextInt();

	    Log.info(
		"[ClosedPVP] Generating map '@' with seed @.",
		definition.name(),
		seed
	    );

	    Vars.state.map = map;

	    // Generated maps bypass SaveIO, so Mindustry does not load
	    // data patches automatically. Reproduce that lifecycle here.
	    Seq<DataAsset> assets = new Seq<>();

	    Events.fire(
		new DataPatchLoadEvent(assets)
	    );

	    Vars.state.data.load(assets);

	    ClosedPvpGenerator generator =
		definition.generator().create(seed);

	    Vars.world.loadGenerator(
		definition.width(),
		definition.height(),
		tiles -> generator.generate(
		    tiles,
		    new WorldParams()
		)
	    );
    }

    private WorldgenModule() {
    }
}
