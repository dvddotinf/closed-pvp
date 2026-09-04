package closedpvp.match;

import mindustry.game.Team;

public final class MatchPlayerState {
    private final String uuid;
    private Team team;

    public MatchPlayerState(String uuid) {
        this.uuid = uuid;
    }

    public String uuid() {
        return uuid;
    }

    public Team team() {
        return team;
    }

    public boolean hasTeam() {
        return team != null;
    }

    public void assignTeam(Team team) {
        this.team = team;
    }

    public void clearTeam() {
        this.team = null;
    }
}
