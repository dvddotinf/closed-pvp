package closedpvp.dev;

import arc.util.CommandHandler;
import closedpvp.match.MatchPlayerState;
import closedpvp.match.MatchPlayers;
import closedpvp.modules.defeat.DefeatModule;
import mindustry.gen.Player;

public final class DevCommands {
    public static void register(CommandHandler handler) {
        handler.<Player>register(
            "devlose",
            "Defeat your current Closed PVP team.",
            (args, player) -> {
                MatchPlayerState state = MatchPlayers.get(player);

                if (state == null || !state.hasTeam()) {
                    player.sendMessage("[scarlet]You do not have a team.");
                    return;
                }

                DefeatModule.defeat(state.team());
            }
        );
    }

    private DevCommands() {
    }
}
