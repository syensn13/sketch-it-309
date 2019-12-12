package SS_7.sketch_it.Entities;

/**
 * Holds information for a chat message.
 */
public class ChatEntity {
    private String message;
    private String username;
    private String roomId;
    private String team;
    private int countdown;
    private String messageType;


    public ChatEntity() {
    }

    /**
     * Constructs a new chat message.
     * @param message Message.
     * @param username Username.
     * @param roomId Id of room.
     * @param messageType Type of message.
     */
    public ChatEntity(String message, String username, String roomId, String messageType) {
        this.message = message;
        this.username = username;
        this.roomId = roomId;
        this.messageType = messageType;
    }

    public ChatEntity(String message, String username, String roomId, String messageType, String team) {
        this.message = message;
        this.username = username;
        this.roomId = roomId;
        this.messageType = messageType;
        this.team = team;
    }

    public ChatEntity(String message, String username) {
        this.message = message;
        this.username = username;
    }

    /**
     * Returns team of message.
     * @return Team.
     */
    public String getTeam() {
        return team;
    }

    /**
     * Sets team of message.
     * @param team Team.
     */
    public void setTeam(String team) {
        this.team = team;
    }

    /**
     * Returns room of message.
     * @return Id of room.
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Sets room of message.
     * @param roomId Id of room.
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Returns message.
     * @return Message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     * @param message Message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns username of message.
     * @return Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username of message.
     * @param username Username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns type of message.
     * @return Message type.
     */
    public String getMessageType() { return messageType; }
}
