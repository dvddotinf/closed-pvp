package closedpvp.filters;

import mindustry.content.Blocks;
import mindustry.net.Administration.ActionType;
import mindustry.net.Administration.PlayerAction;
import mindustry.world.Block;
import mindustry.world.blocks.storage.CoreBlock;

public final class CorePlacementFilter {
    public static boolean allow(PlayerAction action) {
        if (action.type != ActionType.placeBlock) {
            return true;
        }

        if (!(action.block instanceof CoreBlock core)) {
            return true;
        }

        if (action.tile == null) {
            return false;
        }

        // Core Shard is the only core that may be placed
        // directly on ordinary terrain.
        if (core == Blocks.coreShard) {
            return true;
        }

        Block previous = action.tile.block();

        // All higher-tier cores may only be placed as upgrades.
        if (!(previous instanceof CoreBlock previousCore)) {
            return false;
        }

        // Mirror vanilla CoreBlock upgrade requirement.
        return core.size > previousCore.size;
    }

    private CorePlacementFilter() {
    }
}
