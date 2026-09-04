package closedpvp;

import mindustry.mod.Plugin;

public class ClosedPvpPlugin extends Plugin {
    @Override
    public void init() {
        ClosedPvpPatches.register();
    }
}
