package closedpvp.filters;

import closedpvp.match.MatchPlayerState;
import closedpvp.match.MatchPlayers;
import mindustry.net.Administration.ActionType;
import mindustry.net.Administration.PlayerAction;

public final class SpectatorRespawnFilter {
    public static boolean allow(PlayerAction action) {
        if (action.type != ActionType.respawn) {
            return true;
        }

        MatchPlayerState state = MatchPlayers.get(action.player);

        // Игрок без команды находится в spectator/founding state.
        return state != null && state.hasTeam();
    }

    private SpectatorRespawnFilter() {
    }
}
