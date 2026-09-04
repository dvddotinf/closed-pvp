package closedpvp.modules.teams;

import closedpvp.match.MatchPlayers;
import closedpvp.rules.RulesSync;
import mindustry.game.Team;
import mindustry.gen.Player;

public final class TeamModule {
    private static final int FIRST_PLAYER_TEAM_ID = 7;

    public static Team allocate() {
        for (int id = FIRST_PLAYER_TEAM_ID; id < Team.all.length; id++) {
            Team team = Team.get(id);

            if (isAvailable(team)) {
                return team;
            }
        }

        throw new IllegalStateException(
            "No free teams available for Closed PVP."
        );
    }

    public static void restore(Player player, Team team) {
        apply(player, team);

        // Если у команды уже существует core,
        // используем обычный vanilla respawn.
        player.checkSpawn();
    }

    public static void apply(Player player, Team team) {
        player.clearUnit();
        player.team(team);

        RulesSync.sync(player);
    }

    private static boolean isAvailable(Team team) {
        if (MatchPlayers.isTeamAssigned(team)) {
            return false;
        }

        var data = team.data();

        // Не переиспользуем team, пока от предыдущего владельца
        // в мире ещё что-либо осталось.
        if (team.active()) {
            return false;
        }

        if (!data.buildings.isEmpty()) {
            return false;
        }

        if (!data.units.isEmpty()) {
            return false;
        }

        return true;
    }

    private TeamModule() {
    }
}
