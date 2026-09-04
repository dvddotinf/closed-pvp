package closedpvp.filters;

import mindustry.Vars;

public final class ActionFilters {
    public static void register() {
        Vars.netServer.admins.addActionFilter(
            SpectatorBuildFilter::allow
        );
    }

    private ActionFilters() {
    }
}
