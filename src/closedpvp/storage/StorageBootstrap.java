package closedpvp.storage;

import arc.Events;
import mindustry.game.EventType.DisposeEvent;
import mindustry.game.EventType.PlayerJoin;
import mindustry.game.EventType.PlayerLeave;

public final class StorageBootstrap {
    public static void register() {
        Database.open();

        Events.on(PlayerJoin.class, event -> {
            PlayerRepository.upsert(event.player);
        });

        Events.on(PlayerLeave.class, event -> {
            PlayerRepository.touch(event.player);
        });

        Events.on(DisposeEvent.class, event -> {
            Database.close();
        });
    }

    private StorageBootstrap() {
    }
}
