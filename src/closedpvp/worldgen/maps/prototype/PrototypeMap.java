package closedpvp.worldgen.maps.prototype;

import mindustry.content.Blocks;

import closedpvp.worldgen.ClosedPvpGenerator;
import closedpvp.worldgen.GeneratedMapDefinition;

public final class PrototypeMap
    extends ClosedPvpGenerator {

    /*
     * Economic baseline map.
     *
     * Здесь намеренно нет биомов.
     *
     * Цель:
     * - проверить mix-tech resource economy;
     * - подобрать абсолютную плотность ресурсов;
     * - подобрать radial richness gradient;
     * - понять, насколько далеко игроку обычно приходится
     *   расширяться за конкретным ресурсом.
     *
     * Настоящие карты позже будут строиться относительно
     * этого baseline.
     */
    private static final int SIZE = 512;

    public PrototypeMap(int seed) {
        super(seed);
    }

    public static GeneratedMapDefinition definition() {
        return new GeneratedMapDefinition(
            "prototype",
            "Closed PVP Prototype",
            "Closed PVP economic balance prototype.",
            SIZE,
            SIZE,
            PrototypeMap::new
        );
    }

    @Override
    protected void generate() {
        generateBase();
        generateTerrain();

        /*
         * Сначала resource floors.
         *
         * Они формируют добываемые площади:
         * sand, water и arkycite.
         */
        generateSand();
        generateWater();
        generateArkycite();

        /*
         * Затем overlays.
         */
        generateOres();
        generateBeryllium();

        decoration(0.0015f);
    }

    /**
     * Один нейтральный biome.
     *
     * Визуальный дизайн сейчас не имеет значения:
     * stone используется просто как substrate.
     */
    private void generateBase() {
        pass((x, y) -> {
            floor = Blocks.stone;
            block = Blocks.air;
            ore = Blocks.air;
        });
    }

    /**
     * Базовая геометрия карты.
     *
     * Оставляем достаточно стен:
     * кроме обычного terrain gameplay они нужны
     * для wall-ore beryllium.
     */
    private void generateTerrain() {
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
    }

    /**
     * Sand — один из самых area-sensitive ресурсов.
     *
     * Поэтому к краю карты увеличивается именно
     * ширина/площадь sand regions.
     *
     * Используем band around 0.5 вместо простого
     * noise > threshold:
     *
     * это создаёт длинные связанные области,
     * а не россыпь случайных островков.
     */
    private void generateSand() {
        pass((x, y) -> {
            if (block != Blocks.air) {
                return;
            }

            if (floor != Blocks.stone) {
                return;
            }

            float distance =
                centerDistance(x, y);

            float richness =
                radialRichness(
                    distance,
                    1.25f
                );

            float width =
                lerp(
                    0.045f,
                    0.105f,
                    richness
                );

            float value = noise(
                x + 1831f,
                y - 7129f,
                3,
                0.65,
                58
            );

            if (
                Math.abs(
                    value - 0.5f
                ) < width
            ) {
                floor = Blocks.sand;
            }
        });
    }

    /**
     * Water — необходима для:
     *
     * - cryofluid;
     * - hydrogen;
     * - turret boosting;
     * - части производственных цепочек;
     * - naval gameplay.
     *
     * В baseline вода присутствует по всей карте,
     * но её площадь немного растёт к краю.
     */
    private void generateWater() {
        pass((x, y) -> {
            if (block != Blocks.air) {
                return;
            }

            /*
             * Sand имеет приоритет:
             * не хотим, чтобы water pass съедал
             * уже сформированные sand fields.
             */
            if (floor != Blocks.stone) {
                return;
            }

            float distance =
                centerDistance(x, y);

            float threshold =
                lerp(
                    0.805f,
                    0.745f,
                    radialRichness(
                        distance,
                        1.15f
                    )
                );

            float value = noise(
                x - 9317f,
                y + 4189f,
                4,
                0.72,
                82
            );

            if (value > threshold) {
                floor = Blocks.water;
                ore = Blocks.air;
            }
        });
    }

    /**
     * Arkycite — strategic Erekir resource.
     *
     * В центре встречается редко,
     * но к опасному краю карты его площадь
     * увеличивается довольно сильно.
     */
    private void generateArkycite() {
        pass((x, y) -> {
            if (block != Blocks.air) {
                return;
            }

            if (floor != Blocks.stone) {
                return;
            }

            float distance =
                centerDistance(x, y);

            float threshold =
                lerp(
                    0.865f,
                    0.765f,
                    radialRichness(
                        distance,
                        1.40f
                    )
                );

            float value = noise(
                x + 6121f,
                y + 2711f,
                4,
                0.70,
                72
            );

            if (value > threshold) {
                floor = Blocks.arkyciteFloor;
                ore = Blocks.air;
            }
        });
    }

    /**
     * Основные floor ores.
     *
     * У каждого ресурса:
     *
     * centerThreshold — плотность около центра;
     * edgeThreshold   — плотность около края;
     * curve           — насколько поздно начинает
     *                   ощущаться богатство края.
     *
     * Чем МЕНЬШЕ threshold, тем БОЛЬШЕ площадь жил.
     */
    private void generateOres() {
        /*
         * Copper.
         *
         * Нужна всем и особенно важна в early game.
         * Радиальный bonus небольшой:
         * огромные залежи copper в late game
         * не дают большой стратегической ценности.
         */
        generateOre(
            Blocks.oreCopper,
            0f,
            1.00f,
            0.86f,
            1.10f
        );

        /*
         * Lead.
         *
         * Немного реже copper,
         * radial bonus тоже умеренный.
         */
        generateOre(
            Blocks.oreLead,
            1f,
            1.07f,
            0.89f,
            1.10f
        );

        /*
         * Coal.
         *
         * Один из основных throughput bottleneck:
         *
         * - silicon;
         * - graphite;
         * - early power.
         *
         * Поэтому к краю количество растёт сильно.
         */
        generateOre(
            Blocks.oreCoal,
            2f,
            1.10f,
            0.70f,
            1.25f
        );

        /*
         * Titanium.
         *
         * Важен для перехода в midgame,
         * но после нормальной unit mining экономики
         * перестаёт быть таким острым bottleneck.
         */
        generateOre(
            Blocks.oreTitanium,
            3f,
            1.18f,
            0.86f,
            1.20f
        );

        /*
         * Thorium.
         *
         * Поздний ресурс.
         *
         * В центре он есть,
         * но хорошие залежи должны уже мотивировать
         * расширяться наружу.
         */
        generateOre(
            Blocks.oreThorium,
            4f,
            1.30f,
            0.92f,
            1.30f
        );

        /*
         * Tungsten.
         *
         * Один из ключевых strategic resources mix-tech.
         *
         * В центре deliberately бедный,
         * возле края становится значительно выгоднее.
         *
         * В отличие от beryllium vanilla Erekir
         * использует tungsten как обычный floor ore.
         */
        generateOre(
            Blocks.oreTungsten,
            5f,
            1.44f,
            0.90f,
            1.45f
        );
    }

    private void generateOre(
        mindustry.world.Block ore,
        float index,
        float centerThreshold,
        float edgeThreshold,
        float curve
    ) {
        variableOre(
            ore,
            index,
            (x, y) -> {
                float richness =
                    radialRichness(
                        centerDistance(x, y),
                        curve
                    );

                return lerp(
                    centerThreshold,
                    edgeThreshold,
                    richness
                );
            }
        );
    }

    /**
     * Beryllium сохраняем wall resource.
     *
     * Это принципиально отличается от Serpulo ores:
     * здесь ценна не просто площадь пола,
     * а доступная поверхность стен.
     *
     * Проверяем только стены,
     * непосредственно граничащие с открытым пространством.
     */
    private void generateBeryllium() {
        pass((x, y) -> {
            if (block == Blocks.air) {
                return;
            }

            if (!nearAir(x, y)) {
                return;
            }

            float distance =
                centerDistance(x, y);

            float threshold =
                lerp(
                    0.755f,
                    0.605f,
                    radialRichness(
                        distance,
                        1.35f
                    )
                );

            float value = noise(
                x + 782f,
                y - 3449f,
                4,
                0.76,
                38
            );

            if (value > threshold) {
                ore =
                    Blocks.wallOreBeryllium;
            }
        });
    }

    /**
     * Нормализованный radial wealth.
     *
     * distance:
     * 0 = центр
     * 1 = край
     *
     * exponent > 1 означает:
     * большая часть bonus сосредоточена
     * ближе к внешней части карты.
     */
    private float radialRichness(
        float distance,
        float exponent
    ) {
        return (float)Math.pow(
            distance,
            exponent
        );
    }

    private float lerp(
        float from,
        float to,
        float value
    ) {
        return from
            + (to - from) * value;
    }
}
