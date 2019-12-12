package SS_7.sketch_it.Entities;

import java.util.ArrayList;

/**
 * Holds information for a round entry.
 */
public class RoundEntity {
    private String username;
    private int points;

    public RoundEntity() {
    }

    /**
     * Constructs a round entry from a username and points.
     * @param username Username.
     * @param points Points.
     */
    public RoundEntity(String username, int points) {
        this.username = username;
        this.points = points;
    }

    /**
     * Returns points.
     * @return Points.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns username.
     * @return Username.
     */
    public String getRoundUsername() {
        return username;
    }
}
