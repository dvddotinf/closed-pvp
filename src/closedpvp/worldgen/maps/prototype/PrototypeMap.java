package closedpvp.worldgen.maps.prototype;

import arc.struct.IntSeq;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.SteamVent;

import closedpvp.worldgen.ClosedPvpGenerator;
import closedpvp.worldgen.GeneratedMapDefinition;

public final class PrototypeMap extends ClosedPvpGenerator {
	private static final int SIZE = 800;

	private static final Block BASE_FLOOR = Blocks.metalFloor;
	private static final Block BASE_FLOOR_DAMAGED = Blocks.metalFloorDamaged;
	private static final Block HEX_TRACE_FLOOR = Blocks.darkPanel3;
	private static final Block HEX_WALL = Blocks.metalWall2;
	private static final Block SAND_FLOOR = Blocks.darksand;
	private static final Block BASALT_FLOOR = Blocks.basalt;

	private static final float SQRT3 = 1.73205080757f;

	private static final float HEX_RADIUS = 38f;
	private static final float HEX_WALL_WIDTH = 4f;
	private static final float HEX_WALL_THRESHOLD = 0.42f;
	private static final float HEX_TILE_SURVIVAL = 0.97f;
	private static final float HEX_SCRAP_THRESHOLD = 0.64f;
	private static final float HEX_LARGE_SCRAP_CHANCE = 0.40f;

	private static final float RESOURCE_BOULDER_CHANCE = 0.00015f;
	private static final int RESOURCE_BOULDER_MIN_RADIUS = 3;
	private static final int RESOURCE_BOULDER_MAX_RADIUS = 7;

	private static final int ARKYCITE_BORDER_RADIUS = 2;
	private static final float ARKYIC_SCATTER_CHANCE = 0.05f;

	// Пока пробное значение: 0.1% на каждый подходящий 4x4 участок.
	private static final float ARKYIC_VENT_CHANCE = 0.033f;

	private static final float AREA_PLUS_20_SCALE = 1.095445f;
	private static final float AREA_MINUS_15_SCALE = 0.921954f;

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
		generateArkyciteBorder();
		generateVents();
		generateArkyicScatter();

		generateSand();
		generateBasaltOverSand();

		generateResourceBoulders();
		generateScatter();
	}

	/*
	 * BASE
	 */

	private void generateBaseFloor() {
		pass((x, y) -> {
			floor = scatterChance(x, y, 11, 0.01f) ? BASE_FLOOR_DAMAGED : BASE_FLOOR;
			block = Blocks.air;
			ore = Blocks.air;
		});
	}

	/*
	 * HEX RUINS
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
			block = material > HEX_SCRAP_THRESHOLD ? Blocks.scrapWall : HEX_WALL;
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
	 * SERPULO ORES
	 */

	private void generateOres() {
		generateOre(Blocks.oreCopper, 0f, 1.00f, 0.86f, 1.10f, AREA_PLUS_20_SCALE);
		generateOre(Blocks.oreLead, 1f, 1.07f, 0.89f, 1.10f, AREA_PLUS_20_SCALE);
		generateOre(Blocks.oreCoal, 2f, 1.10f, 0.70f, 1.25f, 1f);
		generateOre(Blocks.oreTitanium, 3f, 1.18f, 0.86f, 1.20f, 1f);
		generateOre(Blocks.oreThorium, 4f, 1.30f, 0.92f, 1.30f, AREA_MINUS_15_SCALE);
	}

	private void generateOre(
		Block oreBlock,
		float index,
		float centerThreshold,
		float edgeThreshold,
		float curve,
		float scaleMultiplier
	) {
		pass((x, y) -> {
			if (!floor.asFloor().hasSurface()) {
				return;
			}

			float threshold = lerp(
				centerThreshold,
				edgeThreshold,
				radialRichness(centerDistance(x, y), curve)
			);

			float scale1 = (40 + index * 2) * scaleMultiplier;
			float scale2 = (30 + index * 4) * scaleMultiplier;

			if (
				Math.abs(0.5f - noise(x, y + index * 999f, 2, 0.7, scale1)) > 0.26f * threshold
				&& Math.abs(0.5f - noise(x, y - index * 999f, 1, 1, scale2)) > 0.37f * threshold
			) {
				ore = oreBlock;
			}
		});
	}

	/*
	 * BERYLLIUM
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
			0.70f,
			0.80f,
			101
		);
	}

	/*
	 * TUNGSTEN
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
	 * ARKYCITE
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
	 * Гарантирует минимум два tile arkyicStone вокруг liquid arkycite,
	 * не перезаписывая Dark Panel, ресурсы и стены.
	 */
	private void generateArkyciteBorder() {
		boolean[] liquid = new boolean[width * height];

		for (Tile tile : tiles) {
			liquid[tile.x + tile.y * width] = tile.floor() == Blocks.arkyciteFloor;
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!liquid[x + y * width]) {
					continue;
				}

				for (int dx = -ARKYCITE_BORDER_RADIUS; dx <= ARKYCITE_BORDER_RADIUS; dx++) {
					for (int dy = -ARKYCITE_BORDER_RADIUS; dy <= ARKYCITE_BORDER_RADIUS; dy++) {
						if (dx * dx + dy * dy > ARKYCITE_BORDER_RADIUS * ARKYCITE_BORDER_RADIUS) {
							continue;
						}

						Tile tile = tiles.get(x + dx, y + dy);

						if (
							tile != null
							&& tile.block() == Blocks.air
							&& isReplaceableBaseFloor(tile.floor())
						) {
							tile.setFloor(Blocks.arkyicStone.asFloor());
							tile.setOverlay(Blocks.air);
						}
					}
				}
			}
		}
	}

	/*
	 * VENTS
	 */

	private void generateVents() {
		generateVent(Blocks.arkyicStone, Blocks.arkyicVent, ARKYIC_VENT_CHANCE, 600);
	}

	private void generateVent(Block targetFloor, Block ventFloor, float chance, int salt) {
		for (int y = 1; y < height - 2; y++) {
			for (int x = 1; x < width - 2; x++) {
				if (!scatterChance(x, y, salt, chance) || !canPlaceVent(x, y, targetFloor)) {
					continue;
				}

				/*
				 * В v159.7 SteamVent штатно состоит из 3x3 floor tiles.
				 */
				for (var pos : SteamVent.offsets) {
					Tile tile = tiles.getn(x + pos.x + 1, y + pos.y + 1);
					tile.setFloor(ventFloor.asFloor());
					tile.setOverlay(Blocks.air);
				}
			}
		}
	}

	/*
	 * Проверяем 4x4 чистого targetFloor.
	 * Сам штатный vent занимает центральную область 3x3.
	 */
	private boolean canPlaceVent(int centerX, int centerY, Block targetFloor) {
		for (int dx = -1; dx <= 2; dx++) {
			for (int dy = -1; dy <= 2; dy++) {
				Tile tile = tiles.get(centerX + dx, centerY + dy);

				if (
					tile == null
					|| tile.floor() != targetFloor
					|| tile.block() != Blocks.air
					|| tile.overlay() != Blocks.air
				) {
					return false;
				}
			}
		}

		return true;
	}

	/*
	 * 5% суммарно:
	 * 2.5% Crystal Orbs
	 * 2.5% Arkyic Boulder
	 */
	private void generateArkyicScatter() {
		pass((x, y) -> {
			if (floor != Blocks.arkyicStone || block != Blocks.air || ore != Blocks.air) {
				return;
			}

			float value = scatterValue(x, y, 610);

			if (value < ARKYIC_SCATTER_CHANCE / 2f) {
				block = Blocks.crystalOrbs;
			} else if (value < ARKYIC_SCATTER_CHANCE) {
				block = Blocks.arkyicBoulder;
			}
		});
	}

	/*
	 * DARK SAND + BASALT
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
	 * RESOURCE BOULDERS
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

				placeResourceBoulder(x, y, radius, ResourceBoulder.VALUES[typeIndex]);
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

				if (tile == null || tile.block() != Blocks.air || tile.floor() != BASE_FLOOR) {
					return false;
				}
			}
		}

		return true;
	}

	private void placeResourceBoulder(int centerX, int centerY, int radius, ResourceBoulder type) {
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				int x = centerX + dx;
				int y = centerY + dy;

				float distance = (float)Math.sqrt(dx * dx + dy * dy);
				float shape = noise(x + type.ordinal() * 1337f, y - type.ordinal() * 791f, 2, 0.65, 6f);
				float edge = radius - 0.7f + (shape - 0.5f) * 2f;

				if (distance > edge) {
					continue;
				}

				Tile tile = tiles.getn(x, y);
				tile.setBlock(type.wall);

				float edgeFactor = Math.min(1f, distance / Math.max(edge, 0.001f));
				float oreChance = type.maxOreChance * edgeFactor;

				if (scatterChance(x, y, 410 + type.ordinal(), oreChance)) {
					tile.setOverlay(type.wallOre);
				}
			}
		}
	}

	private enum ResourceBoulder {
		BERYLLIUM(Blocks.beryllicStoneWall, Blocks.wallOreBeryllium, 1.00f),
		TUNGSTEN(Blocks.ferricStoneWall, Blocks.wallOreTungsten, 0.50f),
		THORIUM(Blocks.crystallineStoneWall, Blocks.wallOreThorium, 0.75f),
		GRAPHITE(Blocks.carbonWall, Blocks.wallOreGraphite, 0.50f);

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
	 * BASE METAL FLOOR SCATTER
	 */

	private void generateScatter() {
		pass((x, y) -> {
			if (floor != BASE_FLOOR || block != Blocks.air || ore != Blocks.air) {
				return;
			}

			float value = scatterValue(x, y, 500);

			if (value < 0.0005f) {
				block = Blocks.crystalCluster;
			} else if (value < 0.001f) {
				block = Blocks.vibrantCrystalCluster;
			} else if (value < 0.0020f) {
				block = Blocks.sporeCluster;
			} else if (value < 0.0025f) {
				block = Blocks.whiteTree;
			}
		});
	}

	/*
	 * DEPOSIT PRIMITIVES
	 */

	private void generateScatteredOreLikeDeposit(
		Block depositFloor,
		Block oreBlock,
		float index,
		float centerThreshold,
		float edgeThreshold,
		float curve,
		float fillChance,
		float areaKeep,
		float componentKeepChance,
		int scatterSalt
	) {
		boolean[] mask = new boolean[width * height];

		for (Tile tile : tiles) {
			int x = tile.x;
			int y = tile.y;

			if (tile.block() != Blocks.air || !isReplaceableBaseFloor(tile.floor())) {
				continue;
			}

			float threshold = lerp(
				centerThreshold,
				edgeThreshold,
				radialRichness(centerDistance(x, y), curve)
			);

			boolean inside =
				Math.abs(0.5f - noise(x, y + index * 999f, 2, 0.7, 40 + index * 2)) > 0.26f * threshold
				&& Math.abs(0.5f - noise(x, y - index * 999f, 1, 1, 30 + index * 4)) > 0.37f * threshold;

			mask[x + y * width] = inside;
		}

		tuneDepositMask(mask, areaKeep, componentKeepChance, scatterSalt + 1000);

		for (Tile tile : tiles) {
			int x = tile.x;
			int y = tile.y;

			if (!mask[x + y * width]) {
				continue;
			}

			tile.setFloor(depositFloor.asFloor());
			tile.setOverlay(
				scatterChance(x, y, scatterSalt, fillChance)
					? oreBlock
					: Blocks.air
			);
		}
	}

	/*
	 * componentKeepChance отвечает только за частоту месторождений.
	 * areaKeep — за площадь каждого оставшегося component.
	 *
	 * Уменьшение площади идёт с краёв внутрь, поэтому мы не получаем
	 * случайные дырки посреди бериллиевой жилы.
	 */
	private void tuneDepositMask(
		boolean[] mask,
		float areaKeep,
		float componentKeepChance,
		int salt
	) {
		boolean[] visited = new boolean[mask.length];

		IntSeq stack = new IntSeq();
		IntSeq component = new IntSeq();
		IntSeq edge = new IntSeq();

		for (int start = 0; start < mask.length; start++) {
			if (!mask[start] || visited[start]) {
				continue;
			}

			stack.clear();
			component.clear();

			stack.add(start);
			visited[start] = true;

			while (!stack.isEmpty()) {
				int pos = stack.pop();
				component.add(pos);

				int x = pos % width;
				int y = pos / width;

				addMaskNeighbor(mask, visited, stack, x - 1, y);
				addMaskNeighbor(mask, visited, stack, x + 1, y);
				addMaskNeighbor(mask, visited, stack, x, y - 1);
				addMaskNeighbor(mask, visited, stack, x, y + 1);
			}

			int sample = component.get(0);
			int sampleX = sample % width;
			int sampleY = sample / width;

			if (!scatterChance(sampleX, sampleY, salt, componentKeepChance)) {
				for (int i = 0; i < component.size; i++) {
					mask[component.get(i)] = false;
				}

				continue;
			}

			int targetRemove = Math.round(component.size * (1f - areaKeep));
			int layer = 0;

			while (targetRemove > 0) {
				edge.clear();

				for (int i = 0; i < component.size; i++) {
					int pos = component.get(i);

					if (!mask[pos]) {
						continue;
					}

					int x = pos % width;
					int y = pos / width;

					if (isMaskEdge(mask, x, y)) {
						edge.add(pos);
					}
				}

				if (edge.isEmpty()) {
					break;
				}

				int removeThisLayer = Math.min(targetRemove, edge.size);
				float removeChance = (float)removeThisLayer / edge.size;
				int removed = 0;

				for (int i = 0; i < edge.size && removed < removeThisLayer; i++) {
					int pos = edge.get(i);
					int x = pos % width;
					int y = pos / width;

					if (scatterChance(x, y, salt + 100 + layer, removeChance)) {
						mask[pos] = false;
						removed++;
						targetRemove--;
					}
				}

				if (removed < removeThisLayer) {
					int offset = (int)(
						scatterValue(sampleX, sampleY, salt + 500 + layer) * edge.size
					);

					for (int i = 0; i < edge.size && removed < removeThisLayer; i++) {
						int pos = edge.get((offset + i) % edge.size);

						if (!mask[pos]) {
							continue;
						}

						mask[pos] = false;
						removed++;
						targetRemove--;
					}
				}

				layer++;
			}
		}
	}

	private void addMaskNeighbor(boolean[] mask, boolean[] visited, IntSeq stack, int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}

		int pos = x + y * width;

		if (mask[pos] && !visited[pos]) {
			visited[pos] = true;
			stack.add(pos);
		}
	}

	private boolean isMaskEdge(boolean[] mask, int x, int y) {
		return !maskAt(mask, x - 1, y)
			|| !maskAt(mask, x + 1, y)
			|| !maskAt(mask, x, y - 1)
			|| !maskAt(mask, x, y + 1);
	}

	private boolean maskAt(boolean[] mask, int x, int y) {
		return x >= 0 && y >= 0 && x < width && y < height && mask[x + y * width];
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

			float value = noise(x + offsetX, y + offsetY, 3, 0.68, scale);

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
	 * UTILITY
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
