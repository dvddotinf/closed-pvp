package closedpvp.modules.spectator;

import closedpvp.match.MatchPlayers;
import closedpvp.rules.RulesSync;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.gen.Unit;

public final class SpectatorModule {
    public static void enter(Player player) {
	    Unit previous = player.unit();

	    float x;
	    float y;

	    if (previous != null && previous.isValid()) {
		x = previous.x();
		y = previous.y();
	    } else {
		x = Vars.world.unitWidth() / 2f;
		y = Vars.world.unitHeight() / 2f;
	    }

	    MatchPlayers.clearTeam(player);

	    player.clearUnit();
	    player.team(Team.derelict);

	    Unit evoke = UnitTypes.evoke.create(Team.derelict);
	    evoke.set(x, y);
	    evoke.add();

	    Units.notifyUnitSpawn(evoke);

	    player.unit(evoke);

	    RulesSync.sync(player);
    }

    private static boolean insideWorld(float x, float y) {
	return x >= 0f
	    && y >= 0f
	    && x < Vars.world.unitWidth()
	    && y < Vars.world.unitHeight();
    }

    private SpectatorModule() {
    }
}
