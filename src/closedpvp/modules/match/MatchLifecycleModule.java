package closedpvp.modules.match;

import arc.Events;
import closedpvp.match.MatchPlayers;
import closedpvp.modules.player.PlayerLifecycleModule;
import mindustry.game.EventType.PlayEvent;
import mindustry.gen.Groups;

public final class MatchLifecycleModule {
    public static void register() {
        Events.on(PlayEvent.class, event -> {
            MatchPlayers.reset();

            Groups.player.each(PlayerLifecycleModule::enterMatch);
        });
    }

    private MatchLifecycleModule() {
    }
}
