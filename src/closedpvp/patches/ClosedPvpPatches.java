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
        });
    }

    private static PatchAsset createCorePlacementPatch() {
        StringBuilder json = new StringBuilder("{\n")
            .append("\"name\": \"Closed PVP core placement\",\n")
            .append("\"block.core-shard.buildVisibility\": \"shown\"");

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

    private ClosedPvpPatches() {
    }
}
