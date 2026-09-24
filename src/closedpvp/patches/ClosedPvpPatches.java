package closedpvp.patches;

import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType.DataPatchLoadEvent;
import mindustry.mod.data.PatchAsset;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;

public final class ClosedPvpPatches {
	public static void register() {
		Events.on(DataPatchLoadEvent.class, event -> {
			event.assets.add(createCorePlacementPatch());
			event.assets.add(createUnitProductionLiquidPatch());
		});
	}

	private static PatchAsset createCorePlacementPatch() {
		StringBuilder json = new StringBuilder("{\n")
			.append("\"name\": \"Closed PVP core placement\",\n")
			.append("\"block.core-shard.buildVisibility\": \"shown\",\n")
			.append("\"block.core-shard.requirements\": [\"copper/1000\", \"lead/1000\", \"silicon/500\"],\n")
			.append("\"unit.evoke.useUnitCap\": false");

		for (Block block : Vars.content.blocks()) {
			if (block instanceof Floor floor && floor.hasSurface()) {
				json.append(",\n\"block.")
					.append(block.name)
					.append(".allowCorePlacement\": true");
			}
		}

		json.append("\n}");

		PatchAsset patch = new PatchAsset(json.toString());
		patch.setPath("closed-pvp/core-placement.json");

		return patch;
	}

	private static PatchAsset createUnitProductionLiquidPatch() {
		String json = """
			{
			"name": "Closed PVP unit production liquids",

			"block.exponential-reconstructor.consumes": {
				"remove": "liquid",
				"liquid": "cryofluid/0.5"
			},

			"block.tetrative-reconstructor.consumes": {
				"remove": "liquid",
				"liquid": "cryofluid/1.5"
			},

			"block.tank-refabricator.consumes": {
				"remove": "liquid",
				"liquid": "hydrogen/0.025"
			},

			"block.ship-refabricator.consumes": {
				"remove": "liquid",
				"liquid": "hydrogen/0.025"
			},

			"block.mech-refabricator.consumes": {
				"remove": "liquid",
				"liquid": "hydrogen/0.025"
			},

			"block.prime-refabricator.consumes": {
				"remove": "liquid",
				"liquid": "nitrogen/0.083333336"
			},

			"block.tank-assembler.consumes": {
				"remove": "liquid",
				"liquid": "cyanogen/0.075"
			},

			"block.ship-assembler.consumes": {
				"remove": "liquid",
				"liquid": "cyanogen/0.1"
			},

			"block.mech-assembler.consumes": {
				"remove": "liquid",
				"liquid": "cyanogen/0.1"
			}
			}
			""";

		PatchAsset patch = new PatchAsset(json);
		patch.setPath("closed-pvp/unit-production-liquids.json");

		return patch;
	}

	private ClosedPvpPatches() {
	}
}
