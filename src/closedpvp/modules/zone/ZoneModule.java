package closedpvp.modules.zone;

import arc.Events;
import arc.struct.IntSet;
import closedpvp.match.MatchState;
import mindustry.Vars;
import mindustry.game.EventType.TileChangeEvent;
import mindustry.game.EventType.PlayEvent;
import mindustry.game.EventType.ResetEvent;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;

public final class ZoneModule {
    private static ZoneModel zone;
    private static ZoneBuildingIndex buildings;

    private static long lastDamageSecond = -1L;
    private static long lastVisualTick = -1L;

    public static void register() {
        Events.on(PlayEvent.class, event -> {
            initialize();
        });

        Events.on(TileChangeEvent.class, event -> {
	    if (buildings == null) {
		return;
	    }

	    Building build = event.tile.build;

	    if (
		build != null
		&& build.isValid()
		&& build.tile == event.tile
	    ) {
		buildings.track(build);
	    }
	});

        Events.on(ResetEvent.class, event -> {
            reset();
        });

        Events.run(
            Trigger.update,
            ZoneModule::update
        );
    }

    private static void initialize() {
        zone = ZoneModel.forCurrentWorld();
        buildings = new ZoneBuildingIndex(zone);

        buildings.rebuild();

        lastDamageSecond = -1L;

	ZoneVisualizer.initialize(
            zone,
            MatchState.elapsedSeconds()
        );
    }

    private static void update() {
        if (
            zone == null
            || buildings == null
            || !MatchState.active()
        ) {
            return;
        }

        float t = MatchState.elapsedSeconds();
        long second = (long) t;
	ZoneVisualizer.update(zone, t);


        // Ровно один damage pass на каждую секунду.
        if (second == lastDamageSecond) {
            return;
        }

        lastDamageSecond = second;

        damage(t);
    }

    private static void damage(float t) {
        float radius = zone.radius(t);

        int firstOutsideRing =
            Math.max(
                0,
                (int) Math.floor(radius) + 1
            );

        for (
            int ring = firstOutsideRing;
            ring < buildings.ringCount();
            ring++
        ) {
            float damageFraction = zone.damage(t, ring);

            if (damageFraction <= 0f) {
                continue;
            }

            damageRing(
                buildings.ring(ring),
                damageFraction
            );
        }
    }

    private static void damageRing(
        IntSet positions,
        float damageFraction
    ) {
        IntSet.IntSetIterator iterator = positions.iterator();

        while (iterator.hasNext) {
            int pos = iterator.next();

            Building build = Vars.world.build(pos);

            // Lazy cleanup индекса.
            if (build == null || !build.isValid()) {
                iterator.remove();
                continue;
            }

            build.damage(
                build.maxHealth * damageFraction
            );

            if (!build.isValid()) {
                iterator.remove();
            }
        }
    }

    private static void reset() {
        zone = null;
        buildings = null;

        lastDamageSecond = -1L;
	ZoneVisualizer.remove();
    }

    private ZoneModule() {
    }
}
