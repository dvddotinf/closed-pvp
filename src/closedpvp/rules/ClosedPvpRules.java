package closedpvp.rules;

import mindustry.game.Rules;
import mindustry.game.Team;

public final class ClosedPvpRules {
    public static void apply(Rules rules) {
        // Closed PVP самостоятельно определяет поражение команд.
        rules.cleanupDeadTeams = false;
        rules.canGameOver = false;

        // Позволяем восстанавливать руины уничтоженных команд.
        rules.derelictRepair = true;

        // Spectator может бесплатно построить первое ядро.
        rules.teams.get(Team.derelict).infiniteResources = true;
    }

    private ClosedPvpRules() {
    }
}
