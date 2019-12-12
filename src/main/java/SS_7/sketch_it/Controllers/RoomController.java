package SS_7.sketch_it.Controllers;

import SS_7.sketch_it.Util.CountdownTime;
import SS_7.sketch_it.Util.CountdownTimer;
import SS_7.sketch_it.Entities.*;
import SS_7.sketch_it.Repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles all room operations.
 */
@Controller
public class RoomController {

    @Autowired
    private MongoRepository mongoRepository;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    static class Room {
        private ChatEntity chatEntity;

        public Room() {
        }

        Room(ChatEntity chatEntity) {
            this.chatEntity = chatEntity;
        }

        public ChatEntity getChatEntity() {
            return chatEntity;
        }
    }

    static class User {
        private String username;
        private String gameType;

        public User() {

        }

        User(String username, String gameType) {
            this.username = username;
            this.gameType = gameType;
        }

        public String getGameType() {
            return gameType;
        }

        public String getUsername() {
            return username;
        }
    }

    static class Countdown {
        private String countdown;
        private int time;

        public Countdown() {

        }

        Countdown(String countdown, int time) {
            this.countdown = countdown;
            this.time = time;
        }

        public String getCountdown() {
            return countdown;
        }

        public int getTime() {
            return time;
        }
    }

    static class RoomCreationEntity {
        private String username;//user who created the room
        private String password;
        private String library;
        private int maxNumberOfPlayers;
        private String roomName;
        private String roomType;

        public RoomCreationEntity(String username, String password, String library, int maxNumberOfPlayers, String roomName, String roomType) {
            this.username = username;
            this.password = password;
            this.library = library;
            this.maxNumberOfPlayers = maxNumberOfPlayers;
            this.roomName = roomName;
            this.roomType = roomType;
        }

        public String getUsername() {
            return username;
        }
    }

    static class Canvas {
        private ArrayList<ArrayList<Object>> pointArray;
        private ArrayList<Object> mayhem;
        public Canvas(ArrayList<ArrayList<Object>> pointArray, ArrayList<Object> mayhem) {
            this.pointArray = pointArray;
            this.mayhem = mayhem;
        }

        public ArrayList<ArrayList<Object>> getPointArray() {
            return pointArray;
        }

        public ArrayList<Object> getMayhem() {
            return mayhem;
        }
    }

    private void runClassicGame(String roomId) throws InterruptedException {
       /* CountdownTimer timerTask = new CountdownTimer();
        CountdownTime countdown7 = timerTask.start(10000, 1000);
        boolean running = true;
        while(running) {
            if(countdown7.get() <= 0) {
                timerTask.stop();
                running = false;
            } else {
                System.out.println(countdown7.get());
            }
        }*/
        RoomEntity room = mongoRepository.getRoom(roomId);
        for (int i = 1; i <= room.getUsers().size() + 1; i++) {
            RoomEntity roundRoom = mongoRepository.getRoom(roomId);
            mongoRepository.addNewRound(roundRoom.createNewRound(), roomId);
            if (i != 1 && !roundRoom.getRoundStarted()) {
                if (room.getRoomType().equals("Classic") || room.getRoomType().equals("Mayhem")) {
                    RoomEntity startedRoom = mongoRepository.startGameClassic(roomId, new Random().nextInt(room.getUsers().size()));
                    messagingTemplate.convertAndSend("/room/" + roomId + "/startGame", startedRoom);
                } else {
                    RoomEntity startedRoom = mongoRepository.startGameTeams(roomId);
                    messagingTemplate.convertAndSend("/room/" + roomId + "/startGame", startedRoom);
                }
            }
            boolean beforeRound = true;
            CountdownTimer beforeRoundTask = new CountdownTimer();
            CountdownTime beforeRoundCountdown = beforeRoundTask.start(10000, 1000);
            while(beforeRound) {
                if (beforeRoundCountdown.get() <= -1) {
                    beforeRoundTask.stop();
                    beforeRound = false;
                    if (mongoRepository.getRoom(roomId).getWordToGuess() == null || mongoRepository.getRoom(roomId).getWordToGuess().equals(roundRoom.getWordToGuess())) {
                        Random rand = new Random();
                        List<String> words = mongoRepository.getRoomWordChoices(roomId);
                        int randNum = rand.nextInt(words.size());
                        mongoRepository.setWordToGuess(roomId, words.get(randNum), 0);
                        messagingTemplate.convertAndSend("/room/" + roomId + "/wordChoice", words.get(randNum));
                    }
                    mongoRepository.startRound(roomId, i-1);
                    boolean round = true;
                    CountdownTimer roundTask = new CountdownTimer();
                    CountdownTime roundCountdown = roundTask.start(60000, 1000);
                    while(round) {
                        if (roundCountdown.get() <= -1) {
                            roundTask.stop();
                            round = false;
                            mongoRepository.endRound(roomId);
                            messagingTemplate.convertAndSend("/room/" + roomId + "/roundEnd", mongoRepository.getRoom(roomId));
                            boolean afterRound = true;
                            CountdownTimer afterRoundTask = new CountdownTimer();
                            CountdownTime afterRoundCountdown = roundTask.start(10000, 1000);
                            while(afterRound) {
                                if (afterRoundCountdown.get() <= 0) {
                                    afterRoundTask.stop();
                                    afterRound = false;
                                    System.out.println("Done");
                                }
                                TimeUnit.MILLISECONDS.sleep(500);
                            }
                        }
                        boolean allVoted = true;
                        for (UserListEntity u : mongoRepository.getRoom(roomId).getUsers()) {
                            if (u.getUserType().equals("guesser") && !u.isGuessed()) {
                                allVoted = false;
                                break;
                            }
                        }
                        if(allVoted) {
                            roundCountdown.set(1);
                        }
                        System.out.println("Output1 " + roundCountdown.get()/1000);
                        mongoRepository.setCountdown(roomId, (int) roundCountdown.get()/1000);
                        messagingTemplate.convertAndSend("/room/" + roomId + "/countdown", new Countdown("round", (int) roundCountdown.get()/1000));
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                }
                System.out.println("Output " + beforeRoundCountdown.get()/1000);
                messagingTemplate.convertAndSend("/room/" + roomId + "/countdown", new Countdown("beforeRound", (int) beforeRoundCountdown.get()/1000));
                TimeUnit.MILLISECONDS.sleep(500);
            }
        }
        mongoRepository.endGame(roomId);
        messagingTemplate.convertAndSend("/room/" + roomId + "/gameEnd", mongoRepository.getRoom(roomId));
    }

    /**
     * Receives chat messages and rebroadcasts them to the specified room.
     * @param chatEntity Chat message to be broadcast.
     * @param room Id of room.
     * @param headerAccessor Handling for sending username and roomId with message.
     * @return The chat message to be broadcast.
     * @throws Exception
     */
    @MessageMapping("/room/{room}/chat")
    @SendTo("/room/{room}/chat")
    public ChatEntity broadcastChat(@Payload ChatEntity chatEntity, @DestinationVariable String room, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        if (!room.equals("global")) {
            RoomEntity currentRoom = mongoRepository.getRoom(room);
            if (currentRoom.getRoundStarted() && chatEntity.getMessage().equalsIgnoreCase(mongoRepository.getWordToGuess(chatEntity.getRoomId()))) {
                chatEntity.setMessage("I have correctly guessed the word!");
                mongoRepository.addPoints(chatEntity.getUsername(), room, 3*currentRoom.getCountdown(), currentRoom.getRoundNum(),Integer.parseInt(chatEntity.getTeam()));
            }
        }
        headerAccessor.getSessionAttributes().put("roomId", room);
        headerAccessor.getSessionAttributes().put("username", chatEntity.getUsername());
        return chatEntity;
    }

    /**
     * Receives countdown and rebroadcasts to specified room.
     * @param countdown Countdown to be broadcast.
     * @param room Id of room.
     * @return The countdown to be broadcast.
     * @throws Exception
     */
    @MessageMapping("/room/{room}/countdown")
    @SendTo("/room/{room}/countdown")
    public Countdown sendCountdown(Countdown countdown, @DestinationVariable String room) throws Exception {
        return countdown;
    }

    /**
     * Returns the categories of words in the database.
     * @return List of categories.
     */
    @RequestMapping(value = "/categories", method= RequestMethod.GET)
    public ResponseEntity getCategories(){
        try {
            return new ResponseEntity(mongoRepository.getAllCategories(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param team
     * @param room
     * @return
     * @throws Exception
     */
    @MessageMapping("/room/{room}/teamChoose")
    @SendTo("/room/{room}/teamChoose")
    public String sendTeamChoose(String team, @DestinationVariable String room) throws Exception {
        return team;
    }

    /**
     * Receives a join, adds user to room, and broadcasts they have joined.
     * @param user User that joined.
     * @param room Id of room.
     * @throws Exception
     */
    @MessageMapping("/room/{room}/join")
    @SendTo("/room/{room}/join")
    public void joinedChat(User user, @DestinationVariable String room) throws Exception {
        if (!room.equals("global")) {
            mongoRepository.addUserToRoom(new UserListEntity(user.getUsername(), false, mongoRepository.getNewUserId(room)), room);
            messagingTemplate.convertAndSend("/room/menu/roomChange", true);
        }
    }

    /*@MessageMapping("/room/{room}/roundEnd")
    @SendTo("/room/{room}/roundEnd")
    public boolean roundEnd(@DestinationVariable String room) {
        mongoRepository.endRound(room);
        return true;
    }*/

    /**
     * Receives a vote to start, updates database, and starts game when all votes are received.
     * @param user User who voted.
     * @param room Id of room.
     * @throws Exception
     */
    @MessageMapping("/room/{room}/voteStart")
    public void voteStart(User user, @DestinationVariable String room) throws Exception {
        RoomEntity updatedRoom = mongoRepository.userVoteToStart(user.getUsername(), room);
        boolean gameStart = true;
        for (UserListEntity u : updatedRoom.getUsers()) {
            if (!u.getVoteStart()) {
                gameStart = false;
            }
        }
        if (gameStart) {
            //mongoRepository.addNewRound(updatedRoom.createNewRound(), room);
            if (user.gameType.equalsIgnoreCase("teams")) {
                RoomEntity returnRoom = mongoRepository.startGameTeams(room);
                messagingTemplate.convertAndSend("/room/" + room + "/startGame", returnRoom);
                runClassicGame(room);
            }
            RoomEntity returnRoom = mongoRepository.startGameClassic(room, new Random().nextInt(updatedRoom.getUsers().size()));
            messagingTemplate.convertAndSend("/room/" + room + "/startGame", returnRoom);
            messagingTemplate.convertAndSend("/room/menu/roomChange", true);
            runClassicGame(room);
        }
    }

    /**
     * Receives a drawing from canvas and rebroadcasts it to specified room.
     * @param canvas Point array of drawing.
     * @return Point Array
     * @throws Exception
     */
    @MessageMapping("/room/{room}/canvas")
    @SendTo("/room/{room}/canvas")
    public ResponseEntity broadcastCanvas(Canvas canvas) throws Exception {
        return new ResponseEntity(canvas, HttpStatus.OK);
    }

    @MessageMapping("/room/{room}/clearCanvas")
    @SendTo("/room/{room}/clearCanvas")
    public ResponseEntity clearCanvas() throws Exception {
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Creates a new room.
     * @param roomCreationEntity Room to be created.
     * @return New room.
     */
    @RequestMapping(value = "/room", method = RequestMethod.POST)
    public ResponseEntity createRoom(@RequestBody RoomCreationEntity roomCreationEntity) {
        ArrayList<UserListEntity> users = new ArrayList<UserListEntity>();
        users.add(new UserListEntity(roomCreationEntity.getUsername(), false, 0));
        RoomEntity newRoom = new RoomEntity(users, roomCreationEntity.library, roomCreationEntity.roomName, roomCreationEntity.password, roomCreationEntity.maxNumberOfPlayers, roomCreationEntity.roomType);
        messagingTemplate.convertAndSend("/room/menu/roomChange", true);
        return new ResponseEntity(mongoRepository.insertNewRoom(newRoom), HttpStatus.OK);
    }

    /**
     * Ends game on the specified room.
     * @param roomId Id of room.
     * @return OK.
     */
    @RequestMapping(value = "/room/game/end", method = RequestMethod.POST)
    public ResponseEntity endRoomGame(@RequestParam(name = "roomId") String roomId) {
        mongoRepository.endGame(roomId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Returns word choices based on the selected library.
     * @param roomId Id of room.
     * @return Three words based on category of room.
     */
    @RequestMapping(value = "/room/library/wordChoices", method = RequestMethod.POST)
    public ResponseEntity getRoomWordChoicesBasedOnLibrary(@RequestParam(name = "roomId") String roomId) {
        return new ResponseEntity(mongoRepository.getRoomWordChoices(roomId), HttpStatus.OK);
    }

    /**
     * Sets the word to guess for room.
     * @param roomId Id of room.
     * @param wordToGuess Word to guess.
     * @param team Team for word.
     * @return OK.
     * @throws InterruptedException
     */
    @RequestMapping(value = "/room/game/wordToGuess", method = RequestMethod.POST)
    public ResponseEntity setRoomWordToGuess(@RequestParam(name = "roomId") String roomId, @RequestParam(name = "wordToGuess") String wordToGuess, @RequestParam(name = "team", required = false) int team) throws InterruptedException {
        mongoRepository.setWordToGuess(roomId, wordToGuess, team);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Returns open rooms.
     * @return Open rooms.
     */
    @RequestMapping(value = "/rooms/open", method = RequestMethod.GET)
    public ResponseEntity listOpenRooms() {
        return new ResponseEntity(mongoRepository.findOpenRooms(), HttpStatus.OK);
    }

    /**
     * Returns closed rooms.
     * @return Closed rooms.
     */
    @RequestMapping(value = "/rooms/closed", method = RequestMethod.GET)
    public ResponseEntity listClosedRooms() {
        return new ResponseEntity(mongoRepository.findClosedRooms(), HttpStatus.OK);
    }

    /**
     * Returns users in a specified room.
     * @param roomId Id of room.
     * @return Users in room.
     */
    @RequestMapping(value = "/room/getUsersInRoom", method = RequestMethod.GET)
    public ResponseEntity listUsersInRoom(@RequestParam(name = "roomId") String roomId) {
        return new ResponseEntity(mongoRepository.getUsers(roomId), HttpStatus.OK);
    }

    /**
     * Sends a report on a specified user.
     * @param accountEntity User to report.
     * @return OK.
     */
    @RequestMapping(value = "/room/sendReport", method = RequestMethod.POST)
    public ResponseEntity reportUser(@RequestBody AccountEntity accountEntity){
        AccountEntity account = mongoRepository.findLoginCredsByUsername(accountEntity.getUsername());
        if(account != null) {
            account.addReport();
            mongoRepository.updateReport(account.getUsername(), account.getReportNumbers());
        }
        ReportEntity reportEntity = new ReportEntity();
        return new ResponseEntity(mongoRepository.insertNewReport(reportEntity), HttpStatus.OK);
    }

    /**
     * Returns type of room.
     * @param roomId Id of room.
     * @return Type of room.
     */
    @RequestMapping(value = "/room/roomType", method = RequestMethod.GET)
    public ResponseEntity sendReport(@RequestParam(name = "roomId") String roomId) {
        return new ResponseEntity(mongoRepository.getRoomType(roomId), HttpStatus.OK);
    }

    @RequestMapping(value = "/room/updateValues", method = RequestMethod.GET)
    public ResponseEntity updateValues(@RequestParam(name = "roomId") String roomId)
    {
        RoomEntity room = mongoRepository.getRoom(roomId);
        ArrayList<UserListEntity> users = room.getUsers();
        if(!mongoRepository.getRoomType(roomId).equals("Teams"))
        {
            UserListEntity winner = users.get(0);
            for(int j = 0; j < users.size(); j++){
                if(users.get(j).getPoints()> winner.getPoints())
                    winner = users.get(j);
            }
            mongoRepository.updateWins(mongoRepository.findLoginCredsByUsername(winner.getUserListUsername()));
        }
        for(int i = 0; i < users.size(); i++)
        {
            AccountEntity accountEntity = mongoRepository.findLoginCredsByUsername(users.get(i).getUserListUsername());
            int x = accountEntity.getGamesPlayed();
            accountEntity.setGamesPlayed(accountEntity.getGamesPlayed() + 1);
            accountEntity.setPoints(accountEntity.getPoints()+users.get(i).getPoints());
            mongoRepository.endGameAccountUpdate(accountEntity);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
