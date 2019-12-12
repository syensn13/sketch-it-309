package SS_7.sketch_it.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;

@Document(collection = "analysis")
public class DataAnalysisEntity {

    @Id
    private String date;

    private Integer loginClicks;
    private Integer guestLoginClicks;
    private HashMap<Object, Object> gameTypeSelectClicks;
    private HashMap<Object, Object> libraryTypeClicks;
    private HashMap<Object, Object> numberofPlayersClicks;
    private Integer passwordClicks;

    public DataAnalysisEntity(){

    }

    public DataAnalysisEntity(String date, Integer loginClicks, Integer guestLoginClicks, HashMap<Object, Object> gameTypeSelectClicks, HashMap<Object, Object> numberofPlayersClicks, HashMap<Object, Object> libraryTypeClicks,  Integer passwordClicks) {
        this.date = date;
        this.loginClicks = loginClicks;
        this.guestLoginClicks = guestLoginClicks;
        this.gameTypeSelectClicks = gameTypeSelectClicks;
        this.libraryTypeClicks = libraryTypeClicks;
        this.numberofPlayersClicks = numberofPlayersClicks;
        this.passwordClicks = passwordClicks;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getLoginClicks() {
        return loginClicks;
    }

    public void setLoginClicks(Integer loginClicks) {
        this.loginClicks = loginClicks;
    }

    public Integer getGuestLoginClicks() {
        return guestLoginClicks;
    }

    public void setGuestLoginClicks(Integer guestLoginClicks) {
        this.guestLoginClicks = guestLoginClicks;
    }

    public HashMap<Object, Object> getGameTypeSelectClicks() {
        return gameTypeSelectClicks;
    }

    public void setGameTypeSelectClicks(HashMap<Object, Object> gameTypeSelectClicks) {
        this.gameTypeSelectClicks = gameTypeSelectClicks;
    }

    public HashMap<Object, Object> getLibraryTypeClicks() {
        return libraryTypeClicks;
    }

    public void setLibraryTypeClicks(HashMap<Object, Object> libraryTypeClicks) {
        this.libraryTypeClicks = libraryTypeClicks;
    }

    public HashMap<Object, Object> getNumberofPlayersClicks() {
        return numberofPlayersClicks;
    }

    public void setNumberofPlayersClicks(HashMap<Object, Object> numberofPlayersClicks) {
        this.numberofPlayersClicks = numberofPlayersClicks;
    }

    public Integer getPasswordClicks() {
        return passwordClicks;
    }

    public void setPasswordClicks(Integer passwordClicks) {
        this.passwordClicks = passwordClicks;
    }

    public DataAnalysisEntity add(DataAnalysisEntity toAdd){
        DataAnalysisEntity toReturn = new DataAnalysisEntity();
        toReturn.setPasswordClicks(this.getPasswordClicks() + toAdd.getPasswordClicks());
        toReturn.setLoginClicks(this.getLoginClicks() + toAdd.getLoginClicks());
        toReturn.setGuestLoginClicks(this.getGuestLoginClicks() + toAdd.getGuestLoginClicks());
        toReturn.setNumberofPlayersClicks(updateHashMap(this.getNumberofPlayersClicks(),toAdd.getNumberofPlayersClicks()));
        toReturn.setLibraryTypeClicks(updateHashMap(this.getLibraryTypeClicks(),toAdd.getLibraryTypeClicks()));
        toReturn.setGameTypeSelectClicks(updateHashMap(this.getGameTypeSelectClicks(),toAdd.getGameTypeSelectClicks()));
        return toReturn;
    }

    private HashMap<Object, Object> updateHashMap(HashMap<Object, Object> curHash, HashMap<Object, Object> newHash){
        HashMap<Object, Object> returnHash = new HashMap<>();
        for(Object key : curHash.keySet()){
            returnHash.put(key, (Integer) curHash.get(key) + (Integer) newHash.get(key));
        }
        return returnHash;
    }
}
