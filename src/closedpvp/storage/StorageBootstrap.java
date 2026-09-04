package closedpvp.storage;

import arc.Events;
import mindustry.game.EventType.DisposeEvent;

public final class StorageBootstrap {
    public static void register() {
        Database.open();

        Events.on(DisposeEvent.class, event -> {
            Database.close();
        });
    }

    private StorageBootstrap() {
    }
}
