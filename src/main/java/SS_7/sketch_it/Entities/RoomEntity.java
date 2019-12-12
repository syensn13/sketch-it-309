package SS_7.sketch_it.Entities;

import SS_7.sketch_it.Entities.UserListEntity;
import org.apache.catalina.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Holds information for a room.
 */
@Document(collection = "rooms")
public class RoomEntity {

    @Id
    private String roomId;
    private String library;
    private String roomName;
    private String roomType;
    private String password;
    private int maxNumberOfPlayers;
    private String wordToGuess;
    private ArrayList<UserListEntity> users;
    private boolean gameEnded;
    private boolean gameStarted;
    private boolean roundStarted;
    private ArrayList<ArrayList<RoundEntity>> rounds;
    private int round;
    private int countdown;

    public RoomEntity() {

    }

    /**
     * Constructs a room from users, a library, a room name, a password, a max number of players, and a type.
     * @param users List of users.
     * @param library Library.
     * @param roomName Name of room.
     * @param password Password.
     * @param maxNumberOfPlayers Maximum number of players.
     * @param roomType Type of room.
     */
    public RoomEntity(ArrayList<UserListEntity> users,String library, String roomName, String password, int maxNumberOfPlayers, String roomType) {
        this.roomId = UUID.randomUUID().toString();
        this.library = library;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.roomName = roomName;
        this.roomType = roomType;
        this.password = password;
        this.users = users;
        wordToGuess = null;
        this.gameEnded = false;
        this.gameStarted = false;
        this.roundStarted = false;
        this.rounds = new ArrayList<ArrayList<RoundEntity>>();
        this.round = 0;
        this.countdown = 0;
    }

    /**
     * Returns room type.
     * @return Type of room.
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * Sets room type.
     * @param roomType Type of room.
     */
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    /**
     * Returns word to guess.
     * @return Word to guess.
     */
    public String getWordToGuess() {
        return wordToGuess;
    }

    /**
     * Sets word to guess.
     * @param wordToGuess Word to guess.
     */
    public void setWordToGuess(String wordToGuess) {
        this.wordToGuess = wordToGuess;
    }

    /**
     * Returns maximum number of players.
     * @return Maximum number of players.
     */
    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    /**
     * Sets maximum number of players.
     * @param maxNumberofPlayers Maximum number of players.
     */
    public void setMaxNumberOfPlayers(int maxNumberofPlayers) {
        this.maxNumberOfPlayers = maxNumberofPlayers;
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
     * @param password Password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns name of room.
     * @return Room name.
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Sets name of room.
     * @param roomName Room name.
     */
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    /**
     * Returns library.
     * @return Library.
     */
    public String getLibrary() {
        return library;
    }

    /**
     * Sets library of room.
     * @param library Library.
     */
    public void setLibrary(String library) {
        this.library = library;
    }

    /**
     * Returns id of room.
     * @return Id of room.
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Sets id of room.
     * @param roomId Id of room.
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Returns list of users.
     * @return List of users.
     */
    public ArrayList<UserListEntity> getUsers() {
        return users;
    }

    /**
     * Sets list of users.
     * @param users List of users.
     */
    public void setUsers(ArrayList<UserListEntity> users) {
        this.users = users;
    }

    /**
     * Returns status of game ended.
     * @return Game ended.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Sets status of game ended.
     * @param gameEnded Game ended.
     */
    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    /**
     * Returns status of game started.
     * @return Game started.
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Sets status of game started.
     * @param gameStarted Game started.
     */
    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    /**
     * Returns list of rounds.
     * @return List of rounds.
     */
    public ArrayList<ArrayList<RoundEntity>> getRounds() {
        return rounds;
    }

    /**
     * Adds a new round.
     * @param round Round to add.
     */
    public void addNewRound(ArrayList<RoundEntity> round) {
        rounds.add(round);
    }

    /**
     * Creates a new round of users and points.
     * @return New round.
     */
    public ArrayList<RoundEntity> createNewRound() {
        ArrayList<RoundEntity> newRound = new ArrayList<RoundEntity>();
        for (UserListEntity u : users) {
            newRound.add(new RoundEntity(u.getUserListUsername(), 0));
        }
        return newRound;
    }

    /**
     * Returns round number.
     * @return Round number.
     */
    public int getRoundNum() {
        return round;
    }

    /**
     * Returns status of round started.
     * @return Round started.
     */
    public boolean getRoundStarted() { return roundStarted; }

    /**
     * Returns countdown.
     * @return Countdown.
     */
    public int getCountdown() { return countdown; }
}
