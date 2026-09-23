package closedpvp.worldgen;

public record GeneratedMapDefinition(
    String id,
    String name,
    String description,
    int width,
    int height,
    GeneratorFactory generator
) {
    @FunctionalInterface
    public interface GeneratorFactory {
        ClosedPvpGenerator create(int seed);
    }
}
