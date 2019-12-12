package SS_7.sketch_it.Controllers;

import SS_7.sketch_it.Entities.AccountEntity;
import SS_7.sketch_it.Entities.DictionaryEntity;
import SS_7.sketch_it.Entities.RoomEntity;
import SS_7.sketch_it.Entities.UserListEntity;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.RoundingMode;
import java.util.ArrayList;


@RunWith(MockitoJUnitRunner.class)
class RoomControllerTest {
    private boolean init = false;

    @InjectMocks
    private RoomController roomController = new RoomController();

    @Mock
    private MongoRepository mongoRepository;

    private ArrayList<UserListEntity> userlist1;
    private ArrayList<UserListEntity> userlist2;
    private ArrayList<RoomEntity> rooms;

    public void setup(){
        if(!init) {
            MockitoAnnotations.initMocks(this);
            UserListEntity userListEntity1 = new UserListEntity("test1", 0);
            UserListEntity userListEntity2 = new UserListEntity("test2",0);
            UserListEntity userListEntity3 = new UserListEntity("test3",0);
            String uname = userListEntity1.getUserListUsername();
            userlist1 = new ArrayList<>();
            userlist2 = new ArrayList<>();
            userlist1.add(userListEntity1);
            userlist1.add(userListEntity2);
            userlist1.add(userListEntity3);
            userlist2.add(userListEntity1);
            userlist2.add(userListEntity3);
            RoomEntity roomEntity1 = new RoomEntity(userlist1,"all","room1","1234",5,"classic");
            RoomEntity roomEntity2 = new RoomEntity(userlist2,"all","room2","1234",4,"classic");
            RoomEntity roomEntity3 = new RoomEntity(userlist1,"all","room3","1234",6,"classic");
            rooms = new ArrayList<>();
            rooms.add(roomEntity1);
            rooms.add(roomEntity2);
            rooms.add(roomEntity3);
            roomEntity1.setRoomId("1234");
            init = true;
        }
    }

    @Test
    void testListOpenRooms()
    {
        setup();
        Mockito.when(mongoRepository.findOpenRooms()).thenReturn(rooms);
        ResponseEntity actual = roomController.listOpenRooms();
        Assert.assertTrue(actual.getBody().equals(rooms));
        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    void testListClosedRooms(){
        setup();
        Mockito.when(mongoRepository.findClosedRooms()).thenReturn(rooms);
        ResponseEntity tester = roomController.listClosedRooms();
        Assert.assertTrue(tester.getBody().equals(rooms));
        Assert.assertTrue(tester.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    void testIfUserWasAdded(){
        setup();
        /*Going to ask how to do this one*/
    }

    @Test
    void testIfUserWasRemoved(){
        setup();
        //Mockito.when(mongoRepository.removeUserFromRoom("user1","1"));
    }

    @Test
    void testIfRoomWasClosed(){
        setup();
        //Mockito.when(mongoRepository.removeRoom("rooms1")).thenReturn(rooms);
    }


    @Test
    void testGameRoomType(){
        setup();
        RoomEntity re = new RoomEntity(userlist1,"all","room1","1234",5,"classic");
        //rrayList<UserListEntity> users,String library, String roomName, String password, int maxNumberOfPlayers, String roomType
        //Mockito.when(mongoRepository.getRoomType("_id")).thenReturn();
    }

    @Test
    void testEndRoomGame(){
        setup();
        Mockito.doNothing().when(mongoRepository).endGame("1234");
        ResponseEntity roomActual = roomController.endRoomGame("1234");
        Assert.assertTrue(roomActual.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    void testListUsersInRoom()
    {
        setup();
        Mockito.when(mongoRepository.getUsers(rooms.get(0).getRoomId())).thenReturn(userlist1);
        ResponseEntity actual = roomController.listUsersInRoom(rooms.get(0).getRoomId());
        Assert.assertTrue(actual.getBody().equals(userlist1));
        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.OK));
    }

}