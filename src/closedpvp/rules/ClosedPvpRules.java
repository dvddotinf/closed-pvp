package closedpvp.rules;

import mindustry.game.Rules;
import mindustry.game.Team;

public final class ClosedPvpRules {
    private static final float UNIT_POWER_MULTIPLIER = 1.41421356f;
    
    public static void apply(Rules rules) {
        // Closed PVP самостоятельно определяет поражение команд.
        rules.cleanupDeadTeams = false;
        rules.canGameOver = false;

        // Позволяем восстанавливать руины уничтоженных команд.
        rules.derelictRepair = true;

        // Spectator может бесплатно построить первое ядро.
        rules.teams.get(Team.derelict).infiniteResources = true;

	rules.pvpAutoPause = false;

	rules.waves = false;

	// Units balance
	rules.unitHealthMultiplier = UNIT_POWER_MULTIPLIER;
	rules.unitDamageMultiplier = UNIT_POWER_MULTIPLIER;
	rules.unitCostMultiplier = 2f;
	rules.unitBuildSpeedMultiplier = 2f;
    }

    private ClosedPvpRules() {
    }
}
