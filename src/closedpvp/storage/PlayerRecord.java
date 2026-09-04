package closedpvp.storage;

public record PlayerRecord(
    long id,
    String uuid,
    String usid,
    String lastName,
    long createdAt,
    long lastSeenAt
) {
}
