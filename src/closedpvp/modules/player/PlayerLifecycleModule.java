package closedpvp.modules.player;

import arc.Events;
import closedpvp.match.MatchPlayerState;
import closedpvp.match.MatchPlayers;
import closedpvp.modules.spectator.SpectatorModule;
import closedpvp.modules.teams.TeamModule;
import closedpvp.storage.PlayerRepository;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;
import mindustry.gen.Player;

public final class PlayerLifecycleModule {
    public static void register() {
        Events.on(PlayerJoin.class, event -> {
            join(event.player);
        });

        Events.on(PlayerLeave.class, event -> {
            leave(event.player);
        });
    }

    public static void enterMatch(Player player) {
        PlayerRepository.upsert(player);

        MatchPlayerState state = MatchPlayers.getOrCreate(player);

        if (state.hasTeam()) {
            TeamModule.restore(player, state.team());
        } else {
            SpectatorModule.enter(player);
        }
    }

    private static void join(Player player) {
        enterMatch(player);
    }

    private static void leave(Player player) {
        PlayerRepository.touch(player);
    }

    private PlayerLifecycleModule() {
    }
}
