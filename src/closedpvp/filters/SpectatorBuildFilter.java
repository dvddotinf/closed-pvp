package closedpvp.filters;

import arc.struct.ObjectSet;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.net.Administration.ActionType;
import mindustry.net.Administration.PlayerAction;
import mindustry.world.Block;

public final class SpectatorBuildFilter {
    private static final ObjectSet<Block> allowedBlocks = ObjectSet.with(
        Blocks.coreShard
    );

    public static boolean allow(PlayerAction action) {
        if (action.player.team() != Team.derelict) {
            return true;
        }

        if (action.type != ActionType.placeBlock) {
            return true;
        }

        return allowedBlocks.contains(action.block);
    }

    private SpectatorBuildFilter() {
    }
}
