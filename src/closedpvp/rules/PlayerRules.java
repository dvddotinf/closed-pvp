package closedpvp.rules;

import mindustry.content.Blocks;
import mindustry.game.Team;
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
        if (player.team() == Team.derelict) {
	    rules.blockWhitelist = true;
	    rules.bannedBlocks.clear();
	    rules.bannedBlocks.add(Blocks.coreNucleus);
	    rules.infiniteResources = true;
	}
    }

    private PlayerRules() {
    }
}
