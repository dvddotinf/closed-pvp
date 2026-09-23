package closedpvp.worldgen.maps.prototype;

import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.Tile;

import closedpvp.worldgen.ClosedPvpGenerator;
import closedpvp.worldgen.GeneratedMapDefinition;

public final class PrototypeMap extends ClosedPvpGenerator {
	private static final int SIZE = 512;

	private static final Block BASE_FLOOR = Blocks.metalFloor;
	private static final Block BASE_FLOOR_DAMAGED = Blocks.metalFloorDamaged;
	private static final Block HEX_TRACE_FLOOR = Blocks.darkPanel3;
	private static final Block HEX_WALL = Blocks.metalWall2;
	private static final Block SAND_FLOOR = Blocks.darksand;
	private static final Block BASALT_FLOOR = Blocks.basalt;

	private static final float SQRT3 = 1.73205080757f;

	private static final float HEX_RADIUS = 28f;
	private static final float HEX_WALL_WIDTH = 4f;
	private static final float HEX_WALL_THRESHOLD = 0.42f;
	private static final float HEX_TILE_SURVIVAL = 0.97f;
	private static final float HEX_SCRAP_THRESHOLD = 0.64f;
	private static final float HEX_LARGE_SCRAP_CHANCE = 0.40f;

	private static final float RESOURCE_BOULDER_CHANCE = 0.00018f;
	private static final int RESOURCE_BOULDER_MIN_RADIUS = 3;
	private static final int RESOURCE_BOULDER_MAX_RADIUS = 5;

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
		generateBaseFloor();
		generateHexRuins();

		generateOres();
		generateBeryllium();
		generateTungsten();
		generateArkycite();

		generateSand();
		generateBasaltOverSand();

		generateResourceBoulders();
		generateScatter();
	}

	/*
	 * ============================================================
	 * BASE
	 * ============================================================
	 */

	private void generateBaseFloor() {
		pass((x, y) -> {
			floor = scatterChance(x, y, 11, 0.01f)
				? BASE_FLOOR_DAMAGED
				: BASE_FLOOR;

			block = Blocks.air;
			ore = Blocks.air;
		});
	}

	/*
	 * ============================================================
	 * HEX RUINS
	 * ============================================================
	 */

	private void generateHexRuins() {
		pass((x, y) -> {
			float edgeDistance = hexEdgeDistance(x, y, HEX_RADIUS);

			if (edgeDistance > HEX_WALL_WIDTH / 2f) {
				return;
			}

			floor = HEX_TRACE_FLOOR;

			float survival = noise(x + 4381f, y - 7927f, 2, 0.62, 34f);

			if (survival <= HEX_WALL_THRESHOLD) {
				return;
			}

			if (!scatterChance(x, y, 31, HEX_TILE_SURVIVAL)) {
				return;
			}

			float material = noise(x - 2711f, y + 6143f, 2, 0.65, 22f);

			block = material > HEX_SCRAP_THRESHOLD
				? Blocks.scrapWall
				: HEX_WALL;
		});

		generateLargeScrapWalls();
	}

	private void generateLargeScrapWalls() {
		for (int y = 0; y < height - 1; y++) {
			for (int x = 0; x < width - 1; x++) {
				if (!canPlaceLargeScrapWall(x, y)) {
					continue;
				}

				if (!scatterChance(x, y, 32, HEX_LARGE_SCRAP_CHANCE)) {
					continue;
				}

				tiles.getn(x, y).setBlock(Blocks.scrapWallLarge);
			}
		}
	}

	private boolean canPlaceLargeScrapWall(int x, int y) {
		for (int dx = 0; dx < 2; dx++) {
			for (int dy = 0; dy < 2; dy++) {
				if (tiles.getn(x + dx, y + dy).block() != Blocks.scrapWall) {
					return false;
				}
			}
		}

		return true;
	}

	private float hexEdgeDistance(float x, float y, float radius) {
		long cell = hexCell(x, y, radius);

		int q = (int)(cell >> 32);
		int r = (int)cell;

		float centerX = SQRT3 * radius * (q + r / 2f);
		float centerY = 1.5f * radius * r;

		float dx = Math.abs(x - centerX);
		float dy = Math.abs(y - centerY);

		float verticalDistance = SQRT3 * 0.5f * radius - dx;
		float diagonalDistance = (radius - dy - dx / SQRT3) * SQRT3 * 0.5f;

		return Math.max(0f, Math.min(verticalDistance, diagonalDistance));
	}

	private long hexCell(float x, float y, float radius) {
		float q = (0.57735026919f * x - y / 3f) / radius;
		float r = (2f * y / 3f) / radius;

		float cubeX = q;
		float cubeZ = r;
		float cubeY = -cubeX - cubeZ;

		int rx = Math.round(cubeX);
		int ry = Math.round(cubeY);
		int rz = Math.round(cubeZ);

		float dx = Math.abs(rx - cubeX);
		float dy = Math.abs(ry - cubeY);
		float dz = Math.abs(rz - cubeZ);

		if (dx > dy && dx > dz) {
			rx = -ry - rz;
		} else if (dy > dz) {
			ry = -rx - rz;
		} else {
			rz = -rx - ry;
		}

		return ((long)rx << 32) ^ (rz & 0xffffffffL);
	}

	/*
	 * ============================================================
	 * SERPULO ORES
	 * ============================================================
	 */

	private void generateOres() {
		generateOre(Blocks.oreCopper, 0f, 1.00f, 0.86f, 1.10f);
		generateOre(Blocks.oreLead, 1f, 1.07f, 0.89f, 1.10f);
		generateOre(Blocks.oreCoal, 2f, 1.10f, 0.70f, 1.25f);
		generateOre(Blocks.oreTitanium, 3f, 1.18f, 0.86f, 1.20f);
		generateOre(Blocks.oreThorium, 4f, 1.30f, 0.92f, 1.30f);
	}

	private void generateOre(
		Block oreBlock,
		float index,
		float centerThreshold,
		float edgeThreshold,
		float curve
	) {
		variableOre(
			oreBlock,
			index,
			(x, y) -> lerp(
				centerThreshold,
				edgeThreshold,
				radialRichness(centerDistance(x, y), curve)
			)
		);
	}

	/*
	 * ============================================================
	 * BERYLLIUM
	 * ============================================================
	 */

	private void generateBeryllium() {
		generateScatteredOreLikeDeposit(
			Blocks.beryllicStone,
			Blocks.oreBeryllium,
			6f,
			1.10f,
			0.90f,
			1.25f,
			0.50f,
			101
		);
	}

	/*
	 * ============================================================
	 * TUNGSTEN
	 * ============================================================
	 */

	private void generateTungsten() {
		generateScatteredDeposit(
			Blocks.ferricStone,
			Blocks.oreTungsten,
			-6119f,
			2783f,
			62f,
			0.84f,
			0.76f,
			1.35f,
			0.25f,
			202
		);
	}

	/*
	 * ============================================================
	 * ARKYCITE
	 * ============================================================
	 */

	private void generateArkycite() {
		generateLiquidDeposit(
			Blocks.arkyicStone,
			Blocks.arkyciteFloor,
			7127f,
			1901f,
			96f,
			0.86f,
			0.78f,
			1.40f,
			-1931f,
			8317f,
			9f,
			0.66f
		);
	}

	/*
	 * ============================================================
	 * DARK SAND + BASALT
	 * ============================================================
	 */

	private void generateSand() {
		pass((x, y) -> {
			if (block != Blocks.air || !isReplaceableBaseFloor(floor)) {
				return;
			}

			float threshold = lerp(
				0.69f,
				0.57f,
				radialRichness(centerDistance(x, y), 1.25f)
			);

			float value = noise(x + 1831f, y - 7129f, 3, 0.62, 105f);

			if (value > threshold) {
				floor = SAND_FLOOR;
				ore = Blocks.air;
			}
		});
	}

	private void generateBasaltOverSand() {
		pass((x, y) -> {
			if (block != Blocks.air || floor != SAND_FLOOR) {
				return;
			}

			float value = noise(x - 5917f, y + 3281f, 2, 0.58, 185f);

			if (value > 0.72f) {
				floor = BASALT_FLOOR;
				ore = Blocks.air;
			}
		});
	}

	/*
	 * ============================================================
	 * RESOURCE BOULDERS
	 * ============================================================
	 *
	 * Сам boulder целиком состоит из обычных стен.
	 *
	 * Wall ore накладывается отдельно через scatter.
	 *
	 * Вероятность ресурса:
	 *
	 * center -> 0
	 * edge   -> maxOreChance конкретного ресурса
	 */

	private void generateResourceBoulders() {
		for (int y = RESOURCE_BOULDER_MAX_RADIUS; y < height - RESOURCE_BOULDER_MAX_RADIUS; y++) {
			for (int x = RESOURCE_BOULDER_MAX_RADIUS; x < width - RESOURCE_BOULDER_MAX_RADIUS; x++) {
				if (!scatterChance(x, y, 400, RESOURCE_BOULDER_CHANCE)) {
					continue;
				}

				Tile center = tiles.getn(x, y);

				if (center.block() != Blocks.air || center.floor() != BASE_FLOOR) {
					continue;
				}

				int radius = RESOURCE_BOULDER_MIN_RADIUS + (int)(
					scatterValue(x, y, 401)
					* (RESOURCE_BOULDER_MAX_RADIUS - RESOURCE_BOULDER_MIN_RADIUS + 1)
				);

				radius = Math.min(radius, RESOURCE_BOULDER_MAX_RADIUS);

				if (!canPlaceResourceBoulder(x, y, radius)) {
					continue;
				}

				int typeIndex = Math.min(
					(int)(scatterValue(x, y, 402) * ResourceBoulder.VALUES.length),
					ResourceBoulder.VALUES.length - 1
				);

				placeResourceBoulder(
					x,
					y,
					radius,
					ResourceBoulder.VALUES[typeIndex]
				);
			}
		}
	}

	private boolean canPlaceResourceBoulder(int centerX, int centerY, int radius) {
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				if (dx * dx + dy * dy > radius * radius) {
					continue;
				}

				Tile tile = tiles.get(centerX + dx, centerY + dy);

				if (
					tile == null
					|| tile.block() != Blocks.air
					|| tile.floor() != BASE_FLOOR
				) {
					return false;
				}
			}
		}

		return true;
	}

	private void placeResourceBoulder(
		int centerX,
		int centerY,
		int radius,
		ResourceBoulder type
	) {
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				int x = centerX + dx;
				int y = centerY + dy;

				float distance = (float)Math.sqrt(dx * dx + dy * dy);

				float shape = noise(
					x + type.ordinal() * 1337f,
					y - type.ordinal() * 791f,
					2,
					0.65,
					6f
				);

				/*
				 * Noise слегка деформирует окружность.
				 */
				float edge = radius - 0.7f + (shape - 0.5f) * 2f;

				if (distance > edge) {
					continue;
				}

				Tile tile = tiles.getn(x, y);
				tile.setBlock(type.wall);

				/*
				 * 0 в центре.
				 * 1 непосредственно на фактическом краю blob.
				 */
				float edgeFactor = Math.min(1f, distance / Math.max(edge, 0.001f));

				/*
				 * Максимальная насыщенность достигается
				 * только возле внешней поверхности валуна.
				 */
				float oreChance = type.maxOreChance * edgeFactor;

				if (scatterChance(x, y, 410 + type.ordinal(), oreChance)) {
					tile.setOverlay(type.wallOre);
				}
			}
		}
	}

	private enum ResourceBoulder {
		BERYLLIUM(
			Blocks.beryllicStoneWall,
			Blocks.wallOreBeryllium,
			1.00f
		),

		TUNGSTEN(
			Blocks.ferricStoneWall,
			Blocks.wallOreTungsten,
			0.50f
		),

		THORIUM(
			Blocks.crystallineStoneWall,
			Blocks.wallOreThorium,
			0.75f
		),

		GRAPHITE(
			Blocks.carbonWall,
			Blocks.wallOreGraphite,
			0.50f
		);

		static final ResourceBoulder[] VALUES = values();

		final Block wall;
		final Block wallOre;
		final float maxOreChance;

		ResourceBoulder(Block wall, Block wallOre, float maxOreChance) {
			this.wall = wall;
			this.wallOre = wallOre;
			this.maxOreChance = maxOreChance;
		}
	}

	/*
	 * ============================================================
	 * SCATTER
	 * ============================================================
	 */

	private void generateScatter() {
		pass((x, y) -> {
			if (floor != BASE_FLOOR || block != Blocks.air || ore != Blocks.air) {
				return;
			}

			float value = scatterValue(x, y, 500);

			if (value < 0.0010f) {
				block = Blocks.crystalCluster;
			} else if (value < 0.0015f) {
				block = Blocks.vibrantCrystalCluster;
			} else if (value < 0.0040f) {
				block = Blocks.sporeCluster;
			} else if (value < 0.0050f) {
				block = Blocks.whiteTree;
			}
		});
	}

	/*
	 * ============================================================
	 * FUTURE GENERATOR PRIMITIVES
	 * ============================================================
	 */

	private void generateScatteredOreLikeDeposit(
		Block depositFloor,
		Block oreBlock,
		float index,
		float centerThreshold,
		float edgeThreshold,
		float curve,
		float fillChance,
		int scatterSalt
	) {
		pass((x, y) -> {
			float threshold = lerp(
				centerThreshold,
				edgeThreshold,
				radialRichness(centerDistance(x, y), curve)
			);

			boolean inside =
				Math.abs(
					0.5f - noise(
						x,
						y + index * 999f,
						2,
						0.7,
						40 + index * 2
					)
				) > 0.26f * threshold
				&&
				Math.abs(
					0.5f - noise(
						x,
						y - index * 999f,
						1,
						1,
						30 + index * 4
					)
				) > 0.37f * threshold;

			if (!inside) {
				return;
			}

			if (block != Blocks.air || !isReplaceableBaseFloor(floor)) {
				return;
			}

			floor = depositFloor;
			ore = Blocks.air;

			if (scatterChance(x, y, scatterSalt, fillChance)) {
				ore = oreBlock;
			}
		});
	}

	private void generateScatteredDeposit(
		Block depositFloor,
		Block oreBlock,
		float offsetX,
		float offsetY,
		float scale,
		float centerThreshold,
		float edgeThreshold,
		float curve,
		float fillChance,
		int scatterSalt
	) {
		pass((x, y) -> {
			if (block != Blocks.air || !isReplaceableBaseFloor(floor)) {
				return;
			}

			float threshold = lerp(
				centerThreshold,
				edgeThreshold,
				radialRichness(centerDistance(x, y), curve)
			);

			float value = noise(
				x + offsetX,
				y + offsetY,
				3,
				0.68,
				scale
			);

			if (value <= threshold) {
				return;
			}

			floor = depositFloor;
			ore = Blocks.air;

			if (scatterChance(x, y, scatterSalt, fillChance)) {
				ore = oreBlock;
			}
		});
	}

	private void generateLiquidDeposit(
		Block markerFloor,
		Block liquidFloor,
		float macroOffsetX,
		float macroOffsetY,
		float macroScale,
		float centerThreshold,
		float edgeThreshold,
		float curve,
		float detailOffsetX,
		float detailOffsetY,
		float detailScale,
		float detailThreshold
	) {
		pass((x, y) -> {
			if (block != Blocks.air || !isReplaceableBaseFloor(floor)) {
				return;
			}

			float macroThreshold = lerp(
				centerThreshold,
				edgeThreshold,
				radialRichness(centerDistance(x, y), curve)
			);

			float macro = noise(
				x + macroOffsetX,
				y + macroOffsetY,
				3,
				0.68,
				macroScale
			);

			if (macro <= macroThreshold) {
				return;
			}

			floor = markerFloor;
			ore = Blocks.air;

			float detail = noise(
				x + detailOffsetX,
				y + detailOffsetY,
				2,
				0.62,
				detailScale
			);

			if (detail > detailThreshold) {
				floor = liquidFloor;
			}
		});
	}

	/*
	 * ============================================================
	 * UTILITY
	 * ============================================================
	 */

	private boolean isReplaceableBaseFloor(Block floor) {
		return floor == BASE_FLOOR || floor == BASE_FLOOR_DAMAGED;
	}

	private float radialRichness(float distance, float exponent) {
		return (float)Math.pow(distance, exponent);
	}

	private float lerp(float from, float to, float value) {
		return from + (to - from) * value;
	}

	private boolean scatterChance(int x, int y, int salt, float chance) {
		return scatterValue(x, y, salt) < chance;
	}

	private float scatterValue(int x, int y, int salt) {
		long value =
			((long)seed << 32)
			^ ((long)x * 0x9E3779B97F4A7C15L)
			^ ((long)y * 0xC2B2AE3D27D4EB4FL)
			^ ((long)salt * 0x165667B19E3779F9L);

		value ^= value >>> 33;
		value *= 0xff51afd7ed558ccdL;
		value ^= value >>> 33;
		value *= 0xc4ceb9fe1a85ec53L;
		value ^= value >>> 33;

		return ((value >>> 40) & 0xFFFFFFL) / 16777216f;
	}
}
