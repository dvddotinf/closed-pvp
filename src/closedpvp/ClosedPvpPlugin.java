package closedpvp;

import closedpvp.filters.ActionFilters;
import closedpvp.modules.match.MatchLifecycleModule;
import closedpvp.modules.player.PlayerLifecycleModule;
import closedpvp.patches.ClosedPvpPatches;
import closedpvp.rules.RulesBootstrap;
import closedpvp.storage.StorageBootstrap;
import mindustry.mod.Plugin;

public class ClosedPvpPlugin extends Plugin {
    @Override
    public void init() {
        // Infrastructure.
        ClosedPvpPatches.register();
        StorageBootstrap.register();
        RulesBootstrap.register();
        ActionFilters.register();

        // Gameplay lifecycle.
        MatchLifecycleModule.register();
        PlayerLifecycleModule.register();
    }
}
