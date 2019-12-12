package SS_7.sketch_it.Repository;

import SS_7.sketch_it.Entities.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Repository for all MongoDB operations.
 */
@Repository
public class MongoRepository {

    private MongoTemplate mongoTemplate;

    /**
     * Constructs a new repository from a mongo template.
     *
     * @param mongoTemplate Mongo template.
     */
    public MongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /*public MongoRepository() {

    }*/

    /**
     * Inserts a new account.
     *
     * @param accountEntity Account to be created.
     */
    public void insertNewAccount(AccountEntity accountEntity) {
        if(accountEntity.getUsername().length()>=5) {
            String firstFiveChars = accountEntity.getUsername().substring(0, 5); //check the first five characters to see if they contain Guest
            if (firstFiveChars.equalsIgnoreCase("guest")) {
                throw new IllegalArgumentException("Username cannot contain Guest in the first five characters");
            }
        }
        mongoTemplate.insert(accountEntity);
    }

    /**
     * Returns an account based on username.
     *
     * @param username Username.
     * @return Account.
     */
    public AccountEntity findLoginCredsByUsername(String username) {
        Query query = new Query().addCriteria(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, AccountEntity.class);
    }

    /**
     * Returns a list of all accounts.
     *
     * @return List of accounts.
     */
    public List<AccountEntity> findAccounts() {
        return mongoTemplate.findAll(AccountEntity.class);
    }

    /**
     * Bans a specified account based on username.
     *
     * @param username Username.
     */
    public void banAccount(String username) {
        Query query = new Query().addCriteria(Criteria.where("username").is(username));
        Update update = new Update();
        update.set("isBanned", true);
        mongoTemplate.updateFirst(query, update, AccountEntity.class);

    }

    /**
     * Unbans a specified account based on username.
     *
     * @param username Username.
     */
    public void unbanAccount(String username) {
        Query query = new Query().addCriteria(Criteria.where("username").is(username));
        Update update = new Update();
        update.set("isBanned", false);
        mongoTemplate.updateFirst(query, update, AccountEntity.class);

    }

    //
    // Report Queries
    //

    /**
     * Inserts a new report.
     *
     * @param reportEntity Report to be created.
     * @return Report.
     */
    public ReportEntity insertNewReport(ReportEntity reportEntity) {
        return mongoTemplate.insert(reportEntity);
    }

    /**
     * Returns all reports.
     *
     * @return List of reports.
     */
    public List<ReportEntity> getReports() {
        return mongoTemplate.find(new Query(), ReportEntity.class);
    }

    /**
     * Updates number of reports for a user.
     *
     * @param username Username.
     * @param report   Number of reports.
     */
    public void updateReport(String username, int report) {
        Query query = new Query().addCriteria(Criteria.where("username").is(username));
        Update update = new Update();
        update.set("reportNumbers", report);
        mongoTemplate.updateFirst(query, update, AccountEntity.class);
    }

    //
    // Library/Dictionary Queries
    //

    /**
     * Returns all categories.
     *
     * @return List of categories.
     */
    public List<DictionaryEntity> getAllCategories() {
        return mongoTemplate.findDistinct(new Query(), "category", "dictionary", DictionaryEntity.class);
    }

    //
    // Start of Room Queries
    //

    /**
     * Returns specified room.
     *
     * @param roomId Id of room.
     * @return Room.
     */
    public RoomEntity getRoom(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        return mongoTemplate.findOne(query, RoomEntity.class);
    }

    /**
     * Inserts new room.
     *
     * @param roomEntity Room to be created.
     * @return Room.
     */
    public RoomEntity insertNewRoom(RoomEntity roomEntity) {
        return mongoTemplate.insert(roomEntity);
    }

    /**
     * Removes a specified room.
     *
     * @param roomId Id of room.
     */
    public void removeRoom(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        mongoTemplate.remove(query, RoomEntity.class);
    }

    /**
     * Returns the next id for a user.
     *
     * @param roomId Id of room.
     * @return Next id to be assigned to user.
     */
    public int getNewUserId(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        RoomEntity room = mongoTemplate.findOne(query, RoomEntity.class);
        return room.getUsers().size(); //need to fix for guests
    }

    /**
     * Ends the game for a room.
     *
     * @param roomId Id of room.
     */
    public void endGame(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        Update update = new Update();
        update.set("gameEnded", true);
        update.set("gameStarted", false);
        mongoTemplate.updateFirst(query, update, RoomEntity.class);
    }

    //TODO IF WE DECIDE THAT BOTH TEAMS GET THE SAME WORD WE CAN REMOVE THIS CODE

    /**
     * Returns three words for a rooms category.
     *
     * @param roomId Id of room.
     * @return Three random words.
     */
    public List<String> getRoomWordChoices(String roomId) {
        ArrayList<String> wordChoices = new ArrayList<>();
        String library = getRoomLibrary(roomId);
        Query query = new Query().addCriteria(Criteria.where("category").is(library));
        List<DictionaryEntity> dictionary = new ArrayList<>();
        try {
            dictionary = (library.equalsIgnoreCase("all")) ? mongoTemplate.findAll(DictionaryEntity.class) : mongoTemplate.find(query, DictionaryEntity.class);
            Random rand = new Random();
            for (int i = 0; i < 3; i++) {//get 3 random words
                int index = rand.nextInt(dictionary.size());
                wordChoices.add(dictionary.get(index).getWord());
                dictionary.remove(index);
            }
            return wordChoices;
        } catch (Exception e) {
            ArrayList<String> errorList = new ArrayList<>();
            errorList.add("cat");
            errorList.add("dog");
            return errorList;
        }
    }

    /**
     * Sets word to guess for room.
     *
     * @param roomId      Id of room.
     * @param wordToGuess Word.
     * @param team        Team number for word.
     */
    public void setWordToGuess(String roomId, String wordToGuess, int team) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        Update update = new Update();
        if (team == 1) {
            update.set("team1WordToGuess", wordToGuess);
        } else if (team == 2) {
            update.set("team2WordToGuess", wordToGuess);
        } else {
            update.set("wordToGuess", wordToGuess);
        }
        mongoTemplate.updateFirst(query, update, RoomEntity.class);
    }

    /**
     * Returns room type.
     *
     * @param roomId Id of room.
     * @return Room type.
     */
    public String getRoomType(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        return mongoTemplate.findOne(query, RoomEntity.class).getRoomType();
    }

    public void endGameAccountUpdate(AccountEntity account) {
        Query query = new Query().addCriteria(Criteria.where("username").is(account.getUsername()));
        Update update = new Update();
        update.set("gamesPlayed", account.getGamesPlayed());
        update.set("points", account.getPoints());
        mongoTemplate.updateFirst(query, update, AccountEntity.class);
    }

    public void updateWins(AccountEntity accountEntity) {
        Query query = new Query().addCriteria(Criteria.where("username").is(accountEntity.getUsername()));
        Update update = new Update();
        update.set("gamesWon", accountEntity.getGamesWon() + 1);
        mongoTemplate.updateFirst(query, update, AccountEntity.class);
    }



    /**
     * Returns word to guess.
     * @param roomId Id of room.
     * @return Word to guess.
     */
    public String getWordToGuess(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        try {
            return mongoTemplate.findOne(query, RoomEntity.class).getWordToGuess();
        } catch (NullPointerException nee) {
            return "";
        }
    }

    /**
     * Returns library of room.
     * @param roomId Id of room.
     * @return Library.
     */
    public String getRoomLibrary(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        return mongoTemplate.findOne(query, RoomEntity.class).getLibrary();
    }

    /**
     * Starts round for room.
     * @param roomId Id of room.
     * @param round Round number.
     * @return Updated room.
     */
    public RoomEntity startRound(String roomId, int round) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        Update update = new Update().set("roundStarted", true);
        update.set("round", round);
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), RoomEntity.class);
    }

    /**
     * Ends round for room.
     *
     * Resets user votes and guesses as well.
     * @param roomId Id of room.
     */
    public void endRound(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        query.addCriteria(Criteria.where("users.userType").is("drawer"));
        Update update = new Update();
        update.set("roundStarted", false);
        update.set("users.$.userType", "guesser");
        mongoTemplate.updateFirst(query, update, RoomEntity.class);
        Query query1 = new Query().addCriteria(Criteria.where("_id").is(roomId));
        Update update1 = new Update();
        update1.set("users.$[].guessed", false);
        mongoTemplate.updateFirst(query1, update1, RoomEntity.class);
    }

    /**
     * Starts a classic game for a room.
     * @param roomId Id of room.
     * @param drawer Id of the drawer.
     * @return Updated room.
     */
    public RoomEntity startGameClassic(String roomId, int drawer) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        query.addCriteria(Criteria.where("users._id").is(drawer));
        Update update = new Update();
        update.set("gameStarted", true);
        update.set("users.$.userType", "drawer");
        RoomEntity room = mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), RoomEntity.class);
        return room;
    }

    /**
     * Starts a team game for a room.
     * @param roomId Id of room.
     * @return Updated room.
     */
    public RoomEntity startGameTeams(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        RoomEntity room = mongoTemplate.findOne(query, RoomEntity.class);
        if(room.isGameStarted()){ //game is already started, thus we only need to change the guessers and drawers for each team.
            ArrayList<UserListEntity> userList = room.getUsers();
            Update update = new Update();
            boolean team1DrawerChosen = false;
            boolean team2DrawerChosen = false;
            Random rand = new Random(System.currentTimeMillis());
            //im lazy so for now just keep trying to choose the drawer for both team randomly in a single user list
            while(!(team1DrawerChosen && team2DrawerChosen)){
                int drawer = rand.nextInt(userList.size());
                if(userList.get(drawer).getTeam() == 1 && !team1DrawerChosen){
                    update.set("users." + drawer + ".userType", "drawer");
                    team1DrawerChosen = true;
                }
                if(userList.get(drawer).getTeam() == 2 && !team2DrawerChosen){
                    update.set("users." + drawer + ".userType", "drawer");
                    team2DrawerChosen = true;
                }
            }
            return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), RoomEntity.class);
        }
        else { //setup teams if the game hasnt started before and set first round of drawers and guessers
            ArrayList<UserListEntity> userList = room.getUsers();
            Update update = new Update();
            int team2Number = 0;
            int team1Number = 0;
            int maxTeamMembers = (userList.size() % 2 == 0) ? userList.size() / 2 : userList.size() / 2 + 1;
            Random rand = new Random(System.currentTimeMillis());
            for (int i = 0; i < userList.size(); i++) {
                String updateStr = "users." + i + ".team";
                int playerTeam = rand.nextInt(2) + 1;
                //here we want to set the first drawer to be the first person placed in each team
                if (playerTeam == 1) {
                    if (team1Number >= maxTeamMembers) {
                        if (team2Number == 0) {
                            //if we make it here and no player has been set to team2 yet, we know this is the first player in team2
                            update.set("users." + i + ".userType", "drawer");
                        }
                        update.set(updateStr, 2);
                        team2Number++;
                    } else {
                        if (team1Number == 0) {
                            //if we make it here and no player has been set to team1 yet, we know this is the first player in team1
                            update.set("users." + i + ".userType", "drawer");
                        }
                        update.set(updateStr, playerTeam);
                        team1Number++;
                    }
                } else {
                    if (team2Number >= maxTeamMembers) {
                        if (team1Number == 0) {
                            //if we make it here we know that no player has been set to team1 yet so they are the drawer
                            update.set("users." + i + ".userType", "drawer");
                        }
                        update.set(updateStr, 1);
                        team1Number++;

                    } else {
                        if (team2Number == 0) {
                            //if we make it here then we know no playesr have been set to team2 yet so this player is the drawer
                            update.set("users." + i + ".userType", "drawer");
                        }
                        update.set(updateStr, playerTeam);
                        team2Number++;
                    }
                }

            }
            update.set("gameStarted", true);
            return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), RoomEntity.class);
        }
    }

    /**
     * Adds a user to room.
     * @param user User.
     * @param roomId Id of room.
     */
    public void addUserToRoom(UserListEntity user, String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        query.addCriteria(Criteria.where("users.username").ne(user.getUserListUsername()));
        Update update = new Update();
        update.addToSet("users", user);
        mongoTemplate.updateFirst(query, update, RoomEntity.class);
    }

    /**
     * Removes a user from room.
     * @param username Username.
     * @param roomId Id of room.
     */
    public void removeUserFromRoom(String username, String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        Update update = new Update();
        update.pull("users", new Query().addCriteria(Criteria.where("username").is(username)));
        mongoTemplate.updateMulti(query, update, RoomEntity.class);
    }

    /**
     * Sets vote start for a user.
     * @param username Username.
     * @param roomId Id of room.
     * @return Updated room.
     */
    public RoomEntity userVoteToStart(String username, String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        query.addCriteria(Criteria.where("users.username").is(username));
        Update update = new Update();
        update.set("users.$.voteStart", true);
        return mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), RoomEntity.class);
    }

    /**
     * Returns list of open rooms.
     * @return List of open rooms.
     */
    public List<RoomEntity> findOpenRooms() {
        Query query = new Query().addCriteria(Criteria.where("gameEnded").is(false));
        return mongoTemplate.find(query, RoomEntity.class);
    }

    /**
     * Returns list of closed rooms.
     * @return List of closed rooms.
     */
    public List<RoomEntity> findClosedRooms() {
        Query query = new Query().addCriteria(Criteria.where("gameEnded").is(true));
        return mongoTemplate.find(query, RoomEntity.class);
    }

    /**
     * Updates points for a user both cumulative and for a round.
     * @param username Username.
     * @param roomId Id of room.
     * @param points Points to add.
     * @param round Round number.
     * @param team Team number.
     */
    public void addPoints(String username, String roomId, int points, int round,int team) {
        if(team != 0){
           addPointsToAllInTeam(roomId,points,round,team);
        }
        else {
            Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
            query.addCriteria(Criteria.where("gameStarted").is(true));
            query.addCriteria(Criteria.where("roundStarted").is(true));
            query.addCriteria(Criteria.where("users").elemMatch(Criteria.where("username").is(username).and("guessed").is(false).and("userType").is("guesser")));
            query.addCriteria(Criteria.where("rounds." + round + ".username").is(username));
            Update update = new Update();
            update.inc("users.$.points", points);
            update.set("users.$.guessed", true);
            update.set("rounds." + round + ".$.points", points);
            mongoTemplate.updateFirst(query, update, RoomEntity.class);
        }
    }

    /**
     * Updates points for all users on a team both cumulative and for a round.
     * @param roomId Id of room.
     * @param points Points to add.
     * @param round Round number.
     * @param team Team number.
     */
    public void addPointsToAllInTeam(String roomId, int points, int round, int team){
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        query.addCriteria(Criteria.where("gameStarted").is(true));
        query.addCriteria(Criteria.where("roundStarted").is(true));
        List<UserListEntity> users = mongoTemplate.findOne(query,RoomEntity.class).getUsers();
        Update update = new Update();
        for(int i =0; i<users.size(); i++){
            if(users.get(i).getTeam() == team) {
                update.inc("users." + i + ".points", points);
                update.set("users." + i + ".guessed", true);
                update.set("rounds." + round + "." + i + ".points", points);
            }
        }
        mongoTemplate.updateMulti(query,update,RoomEntity.class);
    }

    /**
     * Returns list of users.
     * @param roomId Id of room.
     * @return List of users.
     */
    public List<UserListEntity> getUsers(String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        return mongoTemplate.findOne(query, RoomEntity.class).getUsers();
    }

    /**
     * Add new round to a room.
     * @param round Round to be added.
     * @param roomId Id of room.
     */
    public void addNewRound(ArrayList<RoundEntity> round, String roomId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        Update update = new Update();
        update.push("rounds", round);
        mongoTemplate.updateFirst(query, update, RoomEntity.class);
    }

    /**
     * Add points to a round for a user.
     * @param roomId Id of room.
     * @param username Username.
     * @param points Points to add.
     */
    public void addPointsToRound(String roomId, String username, int points) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        //query.addCriteria(Criteria.where("rounds").);
        Update update = new Update();
        update.set("rounds.$.points", points);
        mongoTemplate.updateFirst(query, update, RoomEntity.class);
    }

    /**
     * Set countdown for a room.
     * @param roomId Id of room.
     * @param countdown Countdown number.
     */
    public void setCountdown(String roomId, int countdown) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(roomId));
        Update update = new Update();
        update.set("countdown", countdown);
        mongoTemplate.updateFirst(query, update, RoomEntity.class);
    }

    public HashMap<Object, Object> updateHashMap(HashMap<Object, Object> curHash, HashMap<Object, Object> newHash){
        HashMap<Object, Object> returnHash = new HashMap<>();
        for(Object key : curHash.keySet()){
            returnHash.put(key, (Integer) curHash.get(key) + (Integer) newHash.get(key));
        }
        return returnHash;
    }

    //0 is the normal data map
    //1 is the game type map
    //2 is the number of players map
    //3 is the library choice map
    public void sendDataAnalysisData(ArrayList<HashMap<Object, Object>> data){
        DataAnalysisEntity dataAnalysisEntity = new DataAnalysisEntity((String) data.get(0).get("date"), (Integer) data.get(0).get("loginClicks"), (Integer) data.get(0).get("guestLoginClicks"),
                                                                        data.get(1),data.get(2),data.get(3),(Integer)data.get(0).get("passwordClicks"));
        Query query = new Query().addCriteria(Criteria.where("date").is(data.get(0).get("date")));
        Update update = new Update();
        DataAnalysisEntity dae =  mongoTemplate.findOne(query,DataAnalysisEntity.class);
        if(dae == null){//if this is null we know its the first data object of the day so we just insert it
            mongoTemplate.insert(dataAnalysisEntity);
        }
        else {
            update.set("loginClicks", dataAnalysisEntity.getLoginClicks() + dae.getLoginClicks());
            update.set("guestLoginClicks", dae.getGuestLoginClicks() + dataAnalysisEntity.getGuestLoginClicks());
            update.set("passwordClicks", dae.getPasswordClicks() + dataAnalysisEntity.getPasswordClicks());
            update.set("gameTypeSelectClicks", updateHashMap(dae.getGameTypeSelectClicks(),dataAnalysisEntity.getGameTypeSelectClicks()));
            update.set("libraryTypeClicks", updateHashMap(dae.getLibraryTypeClicks(),dataAnalysisEntity.getLibraryTypeClicks()));
            update.set("numberofPlayersClicks", updateHashMap(dae.getNumberofPlayersClicks(),dataAnalysisEntity.getNumberofPlayersClicks()));
            mongoTemplate.updateFirst(query,update,DataAnalysisEntity.class);
        }
    }

    public DataAnalysisEntity aggregateData(String firstDate,String secondDate) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy");
        Date date1 = null;
        Date date2 = null;
        if(firstDate!=null) {
            date1 = simpleDateFormat.parse(firstDate);
        }
        if(secondDate!=null) {
            date2 = simpleDateFormat.parse(secondDate);
        }
        ArrayList<DataAnalysisEntity> daeList = (ArrayList) mongoTemplate.findAll(DataAnalysisEntity.class);
        DataAnalysisEntity aggregate = null;
        for(int i = 0; i<daeList.size();i++) {
            Date daeDate = simpleDateFormat.parse(daeList.get(i).getDate());
            if((firstDate == null && secondDate == null) || ((firstDate!=null && daeDate.after(date1)) && (secondDate==null)) || ((firstDate==null) && (daeDate.before(date2))) ||  ((firstDate!=null && daeDate.after(date1)) && (secondDate!=null && daeDate.before(date2)))) {
                aggregate = daeList.get(i);
                daeList.remove(i);
                break;
            }
        }
        if(aggregate == null){
           throw new IllegalArgumentException("Given dates returned no data");
        }
        for (DataAnalysisEntity dae : daeList){
            Date daeDate = simpleDateFormat.parse(dae.getDate());
            if((firstDate == null && secondDate == null) || ((firstDate!=null && daeDate.after(date1)) && (secondDate==null)) || ((firstDate==null) && (daeDate.before(date2))) ||  ((firstDate!=null && daeDate.after(date1)) && (secondDate!=null && daeDate.before(date2)))) {
                aggregate = aggregate.add(dae);
            }
        }
        return aggregate;
    }
}
