package closedpvp.filters;

import closedpvp.match.MatchPlayerState;
import closedpvp.match.MatchPlayers;
import closedpvp.modules.founding.FoundingModule;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.net.Administration.ActionType;
import mindustry.net.Administration.PlayerAction;

public final class FoundingActionFilter {
    public static boolean allow(PlayerAction action) {
        if (action.type != ActionType.placeBlock) {
            return true;
        }

        if (action.player.team() != Team.derelict) {
            return true;
        }

        if (action.block != Blocks.coreNucleus) {
            return true;
        }

        MatchPlayerState state = MatchPlayers.getOrCreate(action.player);

        if (state.hasTeam()) {
            return false;
        }

        FoundingModule.found(
            action.player,
            action.tile,
            action.rotation
        );

        // ВАЖНО:
        // исходный vanilla BuildPlan всегда поглощаем.
        //
        // Если founding успешен, Nucleus уже поставлен нами.
        // Если неуспешен, строить его vanilla-путём тоже нельзя.
        return false;
    }

    private FoundingActionFilter() {
    }
}
