package closedpvp.modules.defeat;

import arc.Events;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.game.EventType.BlockDestroyEvent;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;

import closedpvp.match.MatchPlayers;
import closedpvp.modules.spectator.SpectatorModule;

public final class DefeatModule {
    private static final IntSet pending = new IntSet();
    private static final IntSet defeating = new IntSet();

    public static void register() {
        Log.info("[ClosedPVP] DefeatModule registered");

        Events.on(BlockDestroyEvent.class, event -> {
            if (!(event.tile.build instanceof CoreBuild core)) {
                return;
            }

            if (core.block != Blocks.coreNucleus) {
                return;
            }

            Team team = core.team;

            Log.info(
                "[ClosedPVP] Nucleus destroyed: team=@ cores=@",
                team.id,
                team.data().cores.size
            );

            scheduleCheck(team);
        });
    }

    private static void scheduleCheck(Team team) {
        if (team == null || team == Team.derelict) {
            return;
        }

        if (!pending.add(team.id)) {
            return;
        }

        // Не 0f: даём destruction pipeline закончить удаление core
        // и unregisterCore().
        Time.run(1f, () -> {
            pending.remove(team.id);

            if (!hasNucleus(team)) {
                defeat(team);
            }
        });
    }

    private static boolean hasNucleus(Team team) {
        for (CoreBuild core : team.data().cores) {
            if (core.block == Blocks.coreNucleus) {
                return true;
            }
        }

        return false;
    }

    public static void defeat(Team team) {
        if (team == null || team == Team.derelict) {
            return;
        }

        if (!defeating.add(team.id)) {
            return;
        }

        Log.info("[ClosedPVP] Team @ defeated", team.id);

        Seq<Player> players = new Seq<>();

        Groups.player.each(player -> {
            if (player.team() == team) {
                players.add(player);
            }
        });

        // Сначала убираем ownership игроков этой команды.
        MatchPlayers.clearTeam(team);

        // Остатки команды -> derelict / уничтожение.
        team.data().destroyToDerelict();

        for (Player player : players) {
            SpectatorModule.enter(player);
        }

        // Уже после выполнения defeat можно снова разрешить этот team id
        // для будущего переиспользования.
        Time.run(1f, () -> defeating.remove(team.id));
    }

    private DefeatModule() {
    }
}
