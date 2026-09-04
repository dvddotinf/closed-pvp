package closedpvp.match;

import arc.Events;
import mindustry.game.EventType.PlayEvent;
import mindustry.game.EventType.PlayerJoin;

public final class MatchBootstrap {
    public static void register() {
        Events.on(PlayEvent.class, event -> {
            MatchPlayers.reset();
        });

        Events.on(PlayerJoin.class, event -> {
            MatchPlayers.getOrCreate(event.player);
        });
    }

    private MatchBootstrap() {
    }
}
