package closedpvp.modules.defeat;

import arc.struct.Seq;
import closedpvp.match.MatchPlayers;
import closedpvp.modules.spectator.SpectatorModule;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public final class DefeatModule {
    public static void defeat(Team team) {
        if (team == null || team == Team.derelict) {
            return;
        }

        Seq<Player> onlinePlayers = new Seq<>();

        Groups.player.each(player -> {
            if (player.team() == team) {
                onlinePlayers.add(player);
            }
        });

        // Уничтожаем присутствие команды в мире:
        // cores -> destroyed
        // buildings -> derelict / partially destroyed
        // units -> gradually killed
        team.data().destroyToDerelict();

        // Offline и online игроки больше не принадлежат этой
        // команде в состоянии текущего матча.
        MatchPlayers.clearTeam(team);

        // Online игроков немедленно возвращаем в spectator state.
        for (Player player : onlinePlayers) {
            SpectatorModule.enter(player);
        }
    }

    private DefeatModule() {
    }
}
