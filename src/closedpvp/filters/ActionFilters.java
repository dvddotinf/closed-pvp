package closedpvp.filters;

import mindustry.Vars;

public final class ActionFilters {
    public static void register() {
        Vars.netServer.admins.addActionFilter(
            FoundingActionFilter::allow
        );

        Vars.netServer.admins.addActionFilter(
            CorePlacementFilter::allow
        );

        Vars.netServer.admins.addActionFilter(
            SpectatorBuildFilter::allow
        );

        Vars.netServer.admins.addActionFilter(
            SpectatorRespawnFilter::allow
        );
    }

    private ActionFilters() {
    }
}
