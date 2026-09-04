package closedpvp;

import closedpvp.filters.ActionFilters;
import closedpvp.match.MatchBootstrap;
import closedpvp.patches.ClosedPvpPatches;
import closedpvp.rules.RulesBootstrap;
import closedpvp.storage.StorageBootstrap;
import mindustry.mod.Plugin;

public class ClosedPvpPlugin extends Plugin {
    @Override
    public void init() {
        ClosedPvpPatches.register();

        StorageBootstrap.register();
        MatchBootstrap.register();

        RulesBootstrap.register();
        ActionFilters.register();
    }
}
