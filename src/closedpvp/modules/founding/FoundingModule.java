package closedpvp.modules.founding;

import mindustry.gen.Call;
import closedpvp.match.MatchPlayerState;
import closedpvp.match.MatchPlayers;
import closedpvp.modules.teams.TeamModule;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

public final class FoundingModule {
    public static boolean found(Player player, Tile tile, int rotation) {
        MatchPlayerState state = MatchPlayers.getOrCreate(player);

        if (state.hasTeam()) {
            return false;
        }

        if (!Build.validPlace(
            Blocks.coreNucleus,
            Team.derelict,
            tile.x,
            tile.y,
            rotation
        )) {
            return false;
        }

        Team team;

        try {
            team = TeamModule.allocate();
        } catch (IllegalStateException exception) {
            player.sendMessage("[scarlet]No free teams available.");
            return false;
        }

        Unit spectatorUnit = player.unit();

        // Сначала фиксируем состояние матча.
        MatchPlayers.assignTeam(player, team);

        // Переводим игрока и его Evoke в новую команду.
        // Player.team() также меняет team controlled unit.
        player.team(team);

        // Исходный BuildPlan мы не используем вообще.
        // Nucleus сразу появляется полностью построенным
        // и сразу принадлежит правильной команде.
        ConstructBlock.constructed(
	    tile,
	    Blocks.coreNucleus,
	    spectatorUnit,
	    (byte) rotation,
	    team,
	    null
	);

        // Spectator body больше не нужен.
        player.clearUnit();

        if (spectatorUnit != null && spectatorUnit.isAdded()) {
            Call.unitDespawn(spectatorUnit);
        }

        // Теперь core уже существует, поэтому vanilla
        // сможет нормально заспавнить игрока от него.
        TeamModule.restore(player, team);

        return true;
    }

    private FoundingModule() {
    }
}
