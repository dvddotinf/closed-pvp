package closedpvp.rules;

import mindustry.Vars;
import mindustry.game.Rules;
import mindustry.gen.Player;

public final class PlayerRules {
    public static Rules build(Player player) {
        Rules rules = Vars.state.rules.copy();

        applyOverrides(player, rules);

        return rules;
    }

    private static void applyOverrides(Player player, Rules rules) {
        // Client-only overrides идут сюда.
        //
        // Например:
        //
        // if (player.team() == Team.derelict) {
        //     rules.unitCap = 999;
        // }
    }

    private PlayerRules() {
    }
}
