package closedpvp.modules.teams;

import arc.struct.Seq;
import closedpvp.match.MatchPlayers;
import closedpvp.rules.RulesSync;
import mindustry.game.Team;
import mindustry.gen.Player;

public final class TeamModule {
    private static final int FIRST_PLAYER_TEAM_ID = 7;

    public static Team allocate() {
        Seq<Team> available = new Seq<>();

        for (int id = FIRST_PLAYER_TEAM_ID; id < Team.all.length; id++) {
            Team team = Team.get(id);

            if (isAvailable(team)) {
                available.add(team);
            }
        }

        if (available.isEmpty()) {
            throw new IllegalStateException(
                "No free teams available for Closed PVP."
            );
        }

        return available.random();
    }

    public static void restore(Player player, Team team) {
        apply(player, team);
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
