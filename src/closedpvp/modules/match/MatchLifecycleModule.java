package closedpvp.modules.match;

import arc.Events;
import closedpvp.match.MatchPlayers;
import closedpvp.match.MatchState;
import closedpvp.modules.player.PlayerLifecycleModule;
import mindustry.game.EventType.PlayEvent;
import mindustry.game.EventType.ResetEvent;
import mindustry.gen.Groups;

public final class MatchLifecycleModule {
    public static void register() {
        Events.on(PlayEvent.class, event -> {
            MatchPlayers.reset();
            MatchState.start();

            Groups.player.each(PlayerLifecycleModule::enterMatch);
        });

        Events.on(ResetEvent.class, event -> {
            MatchState.stop();
        });
    }

    private MatchLifecycleModule() {
    }
}
