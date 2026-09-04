package closedpvp.rules;

import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType.PlayEvent;
import mindustry.game.EventType.PlayerJoin;

public final class RulesBootstrap {
    public static void register() {
        Events.on(PlayEvent.class, event -> {
            ClosedPvpRules.apply(Vars.state.rules);
            RulesSync.syncAll();
        });

        Events.on(PlayerJoin.class, event -> {
            RulesSync.sync(event.player);
        });
    }

    private RulesBootstrap() {
    }
}
