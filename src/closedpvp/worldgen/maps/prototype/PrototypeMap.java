package closedpvp.worldgen.maps.prototype;

import mindustry.content.Blocks;
import mindustry.world.Block;

import closedpvp.worldgen.ClosedPvpGenerator;
import closedpvp.worldgen.GeneratedMapDefinition;

public final class PrototypeMap
    extends ClosedPvpGenerator {

    /*
     * Для разработки пока 512x512:
     * достаточно большая, чтобы видеть биомы и radial ore gradient,
     * но генерируется намного быстрее будущей ~1000x1000 карты.
     */
    private static final int SIZE = 512;

    private byte[] biomes;

    private static final Biome[] BIOMES =
        Biome.values();

    public PrototypeMap(int seed) {
        super(seed);
    }

    public static GeneratedMapDefinition definition() {
        return new GeneratedMapDefinition(
            "prototype",
            "Closed PVP Prototype",
            "Procedurally generated Closed PVP development map.",
            SIZE,
            SIZE,
            PrototypeMap::new
        );
    }

    @Override
    protected void generate() {
        generateBiomes();
        generateTerrain();

        /*
         * distort() двигает floor + block.
         * Поэтому после него экономическую biome map
         * синхронизируем с итоговыми floor.
         */
        rebuildBiomeMap();

        generateOres();

        // Уже существующий Mindustry decoration pass.
        decoration(0.0015f);
    }

    private void generateBiomes() {
        biomes = new byte[width * height];

        pass((x, y) -> {
            /*
             * Два больших независимых noise field.
             * Их комбинация даёт четыре крупных,
             * но нерегулярных региона.
             */
            float a = noise(
                x,
                y,
                2,
                0.55,
                260
            );

            float b = noise(
                x + 7319f,
                y - 2911f,
                2,
                0.55,
                300
            );

            int id =
                (a > 0.5f ? 1 : 0)
                |
                (b > 0.5f ? 2 : 0);

            Biome biome = BIOMES[id];

            biomes[index(x, y)] =
                (byte)biome.ordinal();

            floor = biome.floor;
            block = Blocks.air;
            ore = Blocks.air;
        });
    }

    private void generateTerrain() {
        /*
         * Сам terrain algorithm — штатный.
         *
         * Временно используем stoneWall как маску стен.
         * После cells/distort стены перекрашиваются
         * под конкретный biome.
         */
        terrain(
            Blocks.stoneWall,
            80f,
            1.2f,
            0f
        );

        cells(1);

        distort(
            45f,
            10f
        );

        /*
         * После distortion приводим внешний вид стен
         * к biome, которому принадлежит floor.
         */
        pass((x, y) -> {
            if (block == Blocks.air) {
                return;
            }

            block = biomeForFloor(floor).wall;
        });
    }

    private void rebuildBiomeMap() {
        each((x, y) -> {
            Block floor =
                tiles.getn(x, y).floor();

            biomes[index(x, y)] =
                (byte)biomeForFloor(floor).ordinal();
        });
    }

    private void generateOres() {
        /*
         * Пока это НЕ баланс.
         *
         * Задача prototype:
         * 1. увидеть vanilla-like жилы;
         * 2. увидеть разные resource profiles биомов;
         * 3. увидеть увеличение площади руды к краю.
         */

        variableOre(
            Blocks.oreCopper,
            0f,
            (x, y) -> oreThreshold(
                x,
                y,
                Blocks.oreCopper
            )
        );

        variableOre(
            Blocks.oreLead,
            1f,
            (x, y) -> oreThreshold(
                x,
                y,
                Blocks.oreLead
            )
        );

        variableOre(
            Blocks.oreCoal,
            2f,
            (x, y) -> oreThreshold(
                x,
                y,
                Blocks.oreCoal
            )
        );

        variableOre(
            Blocks.oreTitanium,
            3f,
            (x, y) -> oreThreshold(
                x,
                y,
                Blocks.oreTitanium
            )
        );
    }

    private float oreThreshold(
        int x,
        int y,
        Block ore
    ) {
        float distance =
            centerDistance(x, y);

        /*
         * Центр:
         * threshold ~1.18 -> относительно мало руды.
         *
         * Край:
         * threshold ~0.72 -> значительно больше площади.
         *
         * Переход непрерывный.
         */
        float richness =
            (float)Math.pow(distance, 1.35);

        float radialThreshold =
            1.18f
            + (0.72f - 1.18f) * richness;

        return radialThreshold
            * affinity(
                biomeAt(x, y),
                ore
            );
    }

    private float affinity(
        Biome biome,
        Block ore
    ) {
        return switch (biome) {
            case STONE -> {
                if (ore == Blocks.oreCopper) {
                    yield 0.80f;
                }

                if (ore == Blocks.oreLead) {
                    yield 0.95f;
                }

                if (ore == Blocks.oreCoal) {
                    yield 1.10f;
                }

                yield 1.15f;
            }

            case SAND -> {
                if (ore == Blocks.oreLead) {
                    yield 0.80f;
                }

                if (ore == Blocks.oreCoal) {
                    yield 0.95f;
                }

                if (ore == Blocks.oreCopper) {
                    yield 1.00f;
                }

                yield 1.15f;
            }

            case SHALE -> {
                if (ore == Blocks.oreCoal) {
                    yield 0.75f;
                }

                if (ore == Blocks.oreCopper) {
                    yield 1.00f;
                }

                if (ore == Blocks.oreLead) {
                    yield 1.05f;
                }

                yield 1.10f;
            }

            case BASALT -> {
                if (ore == Blocks.oreTitanium) {
                    yield 0.75f;
                }

                if (ore == Blocks.oreCopper) {
                    yield 1.00f;
                }

                if (ore == Blocks.oreLead) {
                    yield 1.05f;
                }

                yield 1.10f;
            }
        };
    }

    private Biome biomeAt(int x, int y) {
        return BIOMES[
            biomes[index(x, y)] & 0xff
        ];
    }

    private Biome biomeForFloor(Block floor) {
        for (Biome biome : BIOMES) {
            if (biome.floor == floor) {
                return biome;
            }
        }

        return Biome.STONE;
    }

    private int index(int x, int y) {
        return x + y * width;
    }

    private enum Biome {
        STONE(
            Blocks.stone,
            Blocks.stoneWall
        ),

        SAND(
            Blocks.sand,
            Blocks.sandWall
        ),

        SHALE(
            Blocks.shale,
            Blocks.shaleWall
        ),

        BASALT(
            Blocks.basalt,
            Blocks.duneWall
        );

        final Block floor;
        final Block wall;

        Biome(
            Block floor,
            Block wall
        ) {
            this.floor = floor;
            this.wall = wall;
        }
    }
}
