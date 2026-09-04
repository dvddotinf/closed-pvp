package closedpvp;

import closedpvp.filters.ActionFilters;
import closedpvp.patches.ClosedPvpPatches;
import closedpvp.rules.RulesBootstrap;
import mindustry.mod.Plugin;

public class ClosedPvpPlugin extends Plugin {
    @Override
    public void init() {
        ClosedPvpPatches.register();
        RulesBootstrap.register();
        ActionFilters.register();
    }
}
