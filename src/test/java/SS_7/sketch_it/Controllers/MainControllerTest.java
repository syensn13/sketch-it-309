package SS_7.sketch_it.Controllers;

import SS_7.sketch_it.Entities.AccountEntity;
import SS_7.sketch_it.Entities.DictionaryEntity;
import SS_7.sketch_it.Repository.MongoRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;


@RunWith(MockitoJUnitRunner.class)
class zAccountControllerTest {

    private boolean init = false;

    private PageRedirectionController pageRedirectionController =  new PageRedirectionController();

    @InjectMocks
    private AccountController accountController = new AccountController();

    @InjectMocks
    private RoomController roomController = new RoomController();


    @Mock
    private MongoRepository mongoRepository;

    private ArrayList<AccountEntity> expected;
    private ArrayList<DictionaryEntity> categories;

    public void setup(){
        if(!init) {
            MockitoAnnotations.initMocks(this);
            expected = new ArrayList<>();
            categories = new ArrayList<>();
            AccountEntity accountEntity1 = new AccountEntity("test1", "test1", "test1", "test1");
            AccountEntity accountEntity2 = new AccountEntity("test2", "test2", "test2", "test2");
            AccountEntity accountEntity3 = new AccountEntity("test3", "test3", "test3", "test3");
            expected.add(accountEntity1);
            expected.add(accountEntity2);
            expected.add(accountEntity3);
            categories.add(new DictionaryEntity("cat","animal"));
            categories.add(new DictionaryEntity("computer","technology"));
            init = true;
        }
    }

    @Test
    void testGetDefaultPage() {
        Assert.assertEquals("login", pageRedirectionController.getDefaultPage());
    }

    @Test
    void testGetHomePage() {
        Assert.assertEquals("login", pageRedirectionController.getHomePage());
    }

    @Test
    void testGetRegistrationPage() {
        Assert.assertEquals("registration", pageRedirectionController.getRegistrationPage());
    }

    @Test
    void testGetGameRoomPage() {
        Assert.assertEquals("gameroom", pageRedirectionController.getGameRoomPage());
    }

    @Test
    void testGetAdminpage() {
        Assert.assertEquals("adminpage", pageRedirectionController.getAdminpage());
    }

    @Test
    void listUsers() {
        setup();
        Mockito.when(mongoRepository.findAccounts()).thenReturn(expected);
        ResponseEntity actual = accountController.listUsers();
        Assert.assertTrue(actual.getBody().equals(expected));
        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    void testGetMainMenu() {
        Assert.assertEquals("mainmenu", pageRedirectionController.getMainMenu());
    }

    @Test
    void testGetLeaderboards() {
        Assert.assertEquals("leaderboards", pageRedirectionController.getLeaderboards());
    }

    @Test
    void testVerifyLoginWithCorrectCreds() {
        setup();
        AccountEntity hashedExpected = new AccountEntity(expected.get(0).getUsername(),DigestUtils.sha1Hex(expected.get(0).getPassword()),expected.get(0).getFirstName(),expected.get(0).getLastName());
        Mockito.when(mongoRepository.findLoginCredsByUsername(Mockito.anyString())).thenReturn(hashedExpected);
        ResponseEntity actual = accountController.verifyLogin(expected.get(0));
        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    void testVerifyLoginWithIncorrectCreds() {
        setup();
        AccountEntity hashedExpected = new AccountEntity(expected.get(0).getUsername(),"bad",expected.get(0).getFirstName(),expected.get(0).getLastName());
        Mockito.when(mongoRepository.findLoginCredsByUsername(Mockito.anyString())).thenReturn(hashedExpected);
        ResponseEntity actual = accountController.verifyLogin(expected.get(0));
        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    void testRegisterAccountSuccess() {
        setup();
        Mockito.doNothing().when(mongoRepository).insertNewAccount(Mockito.any());
        ResponseEntity actual = accountController.registerAccount(expected.get(0));
        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.OK));
    }

    //Figure something out regarding the DuplicateKeyException constructor issues
//    @Test
//    void testRegisterAccountFailure() {
//        setup();
//        Mockito.doThrow(new DuplicateKeyException(new BsonDocument("message","error",))).when(mongoRepository).insertNewAccount(Mockito.any());
//        ResponseEntity actual = mainController.registerAccount(expected.get(0));
//        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.BAD_REQUEST));
//  Once again check for mongo exceptions and how to throw them well with Mockito
//    @Test
//    void testGetCategoriesFailure() {
//        setup();
//        Mockito.doThrow(new Exception()).when(mongoRepository).getAllCategories();
//        ResponseEntity actual = mainController.registerAccount(expected.get(0));
//        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
//    }

    @Test
    void getCategories() {
        setup();
        Mockito.when(mongoRepository.getAllCategories()).thenReturn(categories);
        ResponseEntity actual = roomController.getCategories();
        Assert.assertTrue(actual.getBody().equals(categories));
        Assert.assertTrue(actual.getStatusCode().equals(HttpStatus.OK));
    }
}