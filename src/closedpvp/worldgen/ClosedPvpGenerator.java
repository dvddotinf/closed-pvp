package closedpvp.worldgen;

import arc.util.noise.Simplex;
import mindustry.maps.generators.BasicGenerator;
import mindustry.world.Block;
import mindustry.world.Tiles;
import mindustry.world.WorldParams;

public abstract class ClosedPvpGenerator extends BasicGenerator {
    private final int baseSeed;

    protected int seed;

    protected ClosedPvpGenerator(int seed) {
        this.baseSeed = seed;
    }

    @Override
    public final void generate(Tiles tiles, WorldParams params) {
        seed = baseSeed + params.seedOffset;

        rand.setSeed(seed);

        // World.loadGenerator() создаёт пустой Tiles,
        // поэтому перед BasicGenerator.pass()/terrain()/etc
        // его необходимо заполнить Tile-объектами.
        tiles.fill();

        super.generate(tiles, params);
    }

    @Override
    protected float noise(
        float x,
        float y,
        double octaves,
        double falloff,
        double scl,
        double mag
    ) {
        return Simplex.noise2d(
            seed,
            octaves,
            falloff,
            1f / scl,
            x,
            y
        ) * (float)mag;
    }

    /**
     * Square/Chebyshev distance from the center.
     *
     * 0 = center
     * 1 = map border
     */
    protected float centerDistance(int x, int y) {
        float cx = (width - 1) / 2f;
        float cy = (height - 1) / 2f;

        float rx = Math.max((width - 1) / 2f, 1f);
        float ry = Math.max((height - 1) / 2f, 1f);

        float dx = Math.abs(x - cx) / rx;
        float dy = Math.abs(y - cy) / ry;

        return Math.min(
            1f,
            Math.max(dx, dy)
        );
    }

    /**
     * Mindustry BasicGenerator.ore(), but threshold may depend
     * on the tile.
     *
     * threshold = 1 roughly corresponds to vanilla density.
     * Lower threshold -> more ore area.
     * Higher threshold -> less ore area.
     */
    protected void variableOre(
        Block dest,
        float index,
        OreThreshold threshold
    ) {
        pass((x, y) -> {
            if (!floor.asFloor().hasSurface()) {
                return;
            }

            float value = threshold.get(x, y);

            if (
                Math.abs(
                    0.5f - noise(
                        x,
                        y + index * 999f,
                        2,
                        0.7,
                        40 + index * 2
                    )
                ) > 0.26f * value
                &&
                Math.abs(
                    0.5f - noise(
                        x,
                        y - index * 999f,
                        1,
                        1,
                        30 + index * 4
                    )
                ) > 0.37f * value
            ) {
                ore = dest;
            }
        });
    }

    @FunctionalInterface
    protected interface OreThreshold {
        float get(int x, int y);
    }
}
