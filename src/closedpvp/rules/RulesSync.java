package closedpvp.rules;

import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public final class RulesSync {
    public static void sync(Player player) {
        if (player == null || player.con == null) {
            return;
        }

        Call.setRules(
            player.con,
            PlayerRules.build(player)
        );
    }

    public static void syncAll() {
        Groups.player.each(RulesSync::sync);
    }

    private RulesSync() {
    }
}
