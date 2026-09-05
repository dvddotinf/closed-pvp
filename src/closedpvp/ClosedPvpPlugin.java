package closedpvp;

import arc.util.CommandHandler;
import closedpvp.dev.DevCommands;
import closedpvp.filters.ActionFilters;
import closedpvp.modules.match.MatchLifecycleModule;
import closedpvp.modules.player.PlayerLifecycleModule;
import closedpvp.patches.ClosedPvpPatches;
import closedpvp.rules.RulesBootstrap;
import closedpvp.storage.StorageBootstrap;
import closedpvp.modules.zone.ZoneModule;
import mindustry.mod.Plugin;

public class ClosedPvpPlugin extends Plugin {
    @Override
    public void init() {
        // Infrastructure.
        ClosedPvpPatches.register();
        StorageBootstrap.register();
        RulesBootstrap.register();
        ActionFilters.register();

        // Gameplay.
        MatchLifecycleModule.register();
        PlayerLifecycleModule.register();
	ZoneModule.register();
    }

    @Override
    public void registerClientCommands(CommandHandler handler) {
        DevCommands.register(handler);
    }
}
