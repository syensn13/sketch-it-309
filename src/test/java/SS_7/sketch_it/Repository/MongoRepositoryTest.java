package SS_7.sketch_it.Repository;

import SS_7.sketch_it.Entities.AccountEntity;
import SS_7.sketch_it.Entities.DictionaryEntity;
import SS_7.sketch_it.Entities.RoomEntity;
import SS_7.sketch_it.Entities.UserListEntity;
import SS_7.sketch_it.Entities.RoundEntity;
import SS_7.sketch_it.Repository.MongoRepository;
import com.mongodb.DuplicateKeyException;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.BsonDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
class MongoRepositoryTest {

    private boolean init = false;

    @InjectMocks
    private MongoRepository mongoRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private RoomEntity room;
    private ArrayList<UserListEntity> users;
    private ArrayList<RoundEntity> rounds;

    public void setup() {
        if (!init) {
            MockitoAnnotations.initMocks(this);
            users = new ArrayList<>();
            users.add(new UserListEntity("test", false, 0));
            users.add(new UserListEntity("test1", false, 2));
            rounds = new ArrayList<>();
            rounds.add(new RoundEntity("test", 10));
            rounds.add(new RoundEntity("test1", 20));
            room = new RoomEntity(users,"All", "Test", "1234", 2, "Classic");
            room.addNewRound(rounds);
            room.setRoomId("1234");
            room.setWordToGuess("Test");
            init = true;
        }
    }

    @Test
    void getRoom() {
        setup();
        Query query = new Query().addCriteria(Criteria.where("_id").is("1234"));
        Mockito.when(mongoTemplate.findOne(query, RoomEntity.class)).thenReturn(room);
        RoomEntity actual = mongoRepository.getRoom("1234");
        Assert.assertEquals(room, actual);
    }

    @Test
    void insertRoom() {
        setup();
        Mockito.when(mongoTemplate.insert(room)).thenReturn(room);
        RoomEntity actual = mongoRepository.insertNewRoom(room);
        Assert.assertEquals(room, actual);
    }

    @Test
    void getNewUserId() {
        setup();
        Query query = new Query().addCriteria(Criteria.where("_id").is("1234"));
        Mockito.when(mongoTemplate.findOne(query, RoomEntity.class)).thenReturn(room);
        int actual = mongoRepository.getRoom("1234").getUsers().size();
        Assert.assertEquals(2, actual);
    }

    @Test
    void getRoomType() {
        setup();
        Query query = new Query().addCriteria(Criteria.where("_id").is("1234"));
        Mockito.when(mongoTemplate.findOne(query, RoomEntity.class)).thenReturn(room);
        String actual = mongoRepository.getRoom("1234").getRoomType();
        Assert.assertEquals("Classic", actual);
    }

    @Test
    void getWordToGuess() {
        setup();
        Query query = new Query().addCriteria(Criteria.where("_id").is("1234"));
        Mockito.when(mongoTemplate.findOne(query, RoomEntity.class)).thenReturn(room);
        String actual = mongoRepository.getWordToGuess("1234");
        Assert.assertEquals("Test", actual);
    }

    @Test
    void getRoomLibrary() {
        setup();
        Query query = new Query().addCriteria(Criteria.where("_id").is("1234"));
        Mockito.when(mongoTemplate.findOne(query, RoomEntity.class)).thenReturn(room);
        String actual = mongoRepository.getRoomLibrary("1234");
        Assert.assertEquals("All", actual);
    }

    @Test
    void findOpenRooms() {
        setup();
        List<RoomEntity> rooms = new ArrayList<>();
        rooms.add(room);
        Query query = new Query().addCriteria(Criteria.where("gameEnded").is(false));
        Mockito.when(mongoTemplate.find(query, RoomEntity.class)).thenReturn(rooms);
        List<RoomEntity> actual = mongoRepository.findOpenRooms();
        Assert.assertEquals(rooms, actual);
    }

    @Test
    void findClosedRooms() {
        setup();
        List<RoomEntity> rooms = new ArrayList<>();
        rooms.add(room);
        Query query = new Query().addCriteria(Criteria.where("gameEnded").is(true));
        Mockito.when(mongoTemplate.find(query, RoomEntity.class)).thenReturn(rooms);
        List<RoomEntity> actual = mongoRepository.findClosedRooms();
        Assert.assertEquals(rooms, actual);
    }

    @Test
    void getUsers() {
        setup();
        Query query = new Query().addCriteria(Criteria.where("_id").is("1234"));
        Mockito.when(mongoTemplate.findOne(query, RoomEntity.class)).thenReturn(room);
        List<UserListEntity> actual = mongoRepository.getRoom("1234").getUsers();
        Assert.assertEquals(room.getUsers(), actual);
    }
}
