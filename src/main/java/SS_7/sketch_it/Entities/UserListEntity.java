package SS_7.sketch_it.Entities;

/**
 * Holds information of a user in room.
 */
public class UserListEntity {
    private int id;
    private String username;
    private boolean voteStart;
    private String userType;
    private int team;
    private int points;
    private boolean guessed;

    public UserListEntity() {
    }

    /**
     * Constructs a user from username and points.
     * @param username Username.
     * @param points Points.
     */
    public UserListEntity(String username, int points) {
        this.username = username;
        this.points = points;
    }

    /**
     * Constructs a user from a username, vote, and id.
     * @param username Username.
     * @param voteStart Vote.
     * @param id Id.
     */
    public UserListEntity(String username, boolean voteStart, int id) {
        this.id = id;
        this.username = username;
        this.voteStart = voteStart;
        this.userType = "guesser";
        this.team = 0;//user is set initially to no team
        this.points = 0;
        this.guessed = false;
    }

    /**
     * Returns team.
     * @return Team.
     */
    public int getTeam() {
        return team;
    }

    /**
     * Sets team for user.
     * @param team Team.
     */
    public void setTeam(int team) {
        this.team = team;
    }

    /**
     * Returns id of user.
     * @return Id.
     */
    public int getId() { return id; }

    /**
     * Returns username of user.
     * @return Username.
     */
    public String getUserListUsername() {
        return username;
    }

    /**
     * Returns vote for user.
     * @return Vote.
     */
    public Boolean getVoteStart() {
        return voteStart;
    }

    /**
     * Returns type of user.
     * @return Type of user.
     */
    public String getUserType() { return userType; }

    /**
     * Returns points for user.
     * @return Points.
     */
    public int getPoints() { return points; }

    /**
     * Returns status of guess from user.
     * @return Guessed.
     */
    public boolean isGuessed() { return guessed; }
}
