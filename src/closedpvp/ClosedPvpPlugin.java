package closedpvp;

import arc.Events;
import arc.util.CommandHandler;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType.DataPatchLoadEvent;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.mod.data.PatchAsset;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;

public class ClosedPvpPlugin extends Plugin {
    @Override
    public void init() {
        Events.on(DataPatchLoadEvent.class, event -> {
            StringBuilder json = new StringBuilder("{\n")
		    .append("\"name\": \"Closed PVP core placement\",\n")
		    .append("\"block.core-shard.buildVisibility\": \"shown\",\n")
		    .append("\"block.core-shard.requirements\": [\"copper/10\"]");

            for (Block block : Vars.content.blocks()) {
                if (block instanceof Floor floor && floor.hasSurface()) {
                    json.append(",\n\"block.")
                        .append(block.name)
                        .append(".allowCorePlacement\": true");
                }
            }

            PatchAsset patch = new PatchAsset(json.append("\n}").toString());
            patch.setPath("closed-pvp/generated-core-placement.json");
            event.assets.add(patch);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        handler.<Player>register(
            "coredebug",
            "Debug Core Shard placement under player.",
            (args, player) -> {
                Tile tile = Vars.world.tileWorld(player.x, player.y);
                CoreBlock core = (CoreBlock) Blocks.coreShard;

                if (tile == null) {
                    player.sendMessage("[scarlet]No tile.");
                    return;
                }

                StringBuilder out = new StringBuilder()
                    .append("[accent]Core Shard debug[]\n")
                    .append("tile: ").append(tile.x).append(", ").append(tile.y).append("\n")
                    .append("floor: ").append(tile.floor().name).append("\n")
                    .append("floor.allowCorePlacement: ")
                    .append(tile.floor().allowCorePlacement).append("\n\n")
                    .append("buildVisibility: ").append(core.buildVisibility).append("\n")
                    .append("isHidden: ").append(core.isHidden()).append("\n")
                    .append("isVisible: ").append(core.isVisible()).append("\n")
                    .append("isPlaceable: ").append(core.isPlaceable()).append("\n")
                    .append("environmentBuildable: ").append(core.environmentBuildable()).append("\n\n")
                    .append("canPlaceOn: ")
                    .append(core.canPlaceOn(tile, player.team(), 0)).append("\n")
                    .append("validPlaceIgnoreUnits: ")
                    .append(Build.validPlaceIgnoreUnits(core, player.team(), tile.x, tile.y, 0, true, true))
                    .append("\n")
                    .append("validPlace: ")
                    .append(Build.validPlace(core, player.team(), tile.x, tile.y, 0))
                    .append("\n\n")
                    .append("3x3 floors:\n");

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        Tile nearby = Vars.world.tile(tile.x + dx, tile.y + dy);
                        if (nearby == null) {
                            out.append("null ");
                        } else {
                            out.append(nearby.floor().name)
                                .append("=")
                                .append(nearby.floor().allowCorePlacement)
                                .append(" ");
                        }
                    }
                    out.append("\n");
                }

                player.sendMessage(out.toString());
            }
        );
    }
}
