package SS_7.sketch_it.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Holds information for an account.
 */
@Document(collection = "loginInfo")
public class AccountEntity {

    @Id
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isAdmin;
    private int points;
    private int reportNumbers;
    private boolean isBanned;
    private int gamesPlayed;
    private int gamesWon;


    /**
     * Constructs a new account.
     * @param username Username.
     * @param password Password.
     * @param firstName First name.
     * @param lastName Last name.
     */
    public AccountEntity(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = false;
        this.reportNumbers = 0;
        this.points = 0;
        this.isBanned = false;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }

    /**
     * Returns first name.
     * @return First name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     * @param firstName First name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns last name.
     * @return Last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     * @param lastName Last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns username.
     * @return Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     * @param username Username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns password.
     * @return Password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     * @param password Password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns number of reports.
     * @return Number of reports.
     */
    public int getReportNumbers() {
        return reportNumbers;
    }

    /**
     * Adds report.
     */
    public void addReport() {
        this.reportNumbers++;
    }

    /**
     * Returns admin status.
     * @return True if admin, false if not.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Sets admin status.
     * @param admin True if admin, false if not.
     */
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Compares two accounts.
     * @param obj Second account to compare.
     * @return True or false.
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || this.getClass() != obj.getClass()){
            return false;
        }
        AccountEntity accountEntity = (AccountEntity) obj;
        return (accountEntity.getFirstName().equals(this.getFirstName()) && accountEntity.getLastName().equals(this.getLastName()) &&
                accountEntity.getUsername().equals(this.getUsername()) && accountEntity.getPassword().equals(this.getPassword()));
    }

    /**
     * Returns banned status.
     * @return True if banned, false if not.
     */
    public boolean isBanned() {
        return isBanned;
    }

    /**
     * Sets banned status.
     * @param banned True if banned, false if not.
     */
    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
}
