package closedpvp.match;

import arc.struct.ObjectMap;
import mindustry.game.Team;
import mindustry.gen.Player;

public final class MatchPlayers {
    private static final ObjectMap<String, MatchPlayerState> players =
        new ObjectMap<>();

    public static MatchPlayerState getOrCreate(String uuid) {
        return players.get(
            uuid,
            () -> new MatchPlayerState(uuid)
        );
    }

    public static MatchPlayerState getOrCreate(Player player) {
        return getOrCreate(player.uuid());
    }

    public static MatchPlayerState get(String uuid) {
        return players.get(uuid);
    }

    public static MatchPlayerState get(Player player) {
        return get(player.uuid());
    }

    public static boolean contains(String uuid) {
        return players.containsKey(uuid);
    }

    public static void assignTeam(String uuid, Team team) {
        getOrCreate(uuid).assignTeam(team);
    }

    public static void assignTeam(Player player, Team team) {
        assignTeam(player.uuid(), team);
    }

    public static void clearTeam(String uuid) {
        MatchPlayerState state = players.get(uuid);

        if (state != null) {
            state.clearTeam();
        }
    }

    public static void clearTeam(Player player) {
        clearTeam(player.uuid());
    }

    public static void reset() {
        players.clear();
    }

    private MatchPlayers() {
    }
}
