package SS_7.sketch_it.Controllers;

import SS_7.sketch_it.Entities.AccountEntity;
import SS_7.sketch_it.Repository.MongoRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all main operations.
 */
@Controller
public class AccountController {

    @Autowired
    private MongoRepository mongoRepository;

    /**
     * Returns a list of all users registered.
     *
     * @return List of users.
     */
    @RequestMapping(value = "/retrieveusers", method = RequestMethod.GET)
    public ResponseEntity listUsers() {
        ResponseEntity entity = new ResponseEntity(mongoRepository.findAccounts(), HttpStatus.OK);
        return entity;
    }

    @RequestMapping(value = "/isAdmin", method = RequestMethod.GET)
    public ResponseEntity isAdmin(@RequestParam(name = "username") String username) {
        return new ResponseEntity(mongoRepository.findLoginCredsByUsername(username).isAdmin(),HttpStatus.OK);
    }

    /**
     * Returns a single user.
     *
     * @param searchedUser The username of user to be returned.
     * @return A single user.
     */
    @RequestMapping(value = "/retrievesingleuser", method = RequestMethod.POST)
    public ResponseEntity getSingleUser(@RequestBody AccountEntity searchedUser) {
        AccountEntity dbAccount = mongoRepository.findLoginCredsByUsername(searchedUser.getUsername());
        return (dbAccount != null) ? new ResponseEntity(dbAccount, HttpStatus.OK) : new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Bans a specified user.
     *
     * @param searchedUser Username of user to ban.
     * @return OK if user is found or BAD if user does not exist.
     */
    @RequestMapping(value = "/ban", method = RequestMethod.POST)
    public ResponseEntity ban(@RequestBody AccountEntity searchedUser) {
        AccountEntity dbAccount = mongoRepository.findLoginCredsByUsername(searchedUser.getUsername());
        if(dbAccount != null) {
            dbAccount.setBanned(true);
            mongoRepository.banAccount(dbAccount.getUsername());
            return (dbAccount != null) ? new ResponseEntity(dbAccount, HttpStatus.OK) : new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Unbans a specified user.
     *
     * @param searchedUser The username of a user to be unbanned.
     * @return OK if user is found or BAD if user does not exist.
     */
    @RequestMapping(value = "/unban", method = RequestMethod.POST)
    public ResponseEntity unban(@RequestBody AccountEntity searchedUser) {
        AccountEntity dbAccount = mongoRepository.findLoginCredsByUsername(searchedUser.getUsername());
        if(dbAccount!=null) {
            dbAccount.setBanned(false);
            mongoRepository.unbanAccount(dbAccount.getUsername());
            return (dbAccount != null) ? new ResponseEntity(dbAccount, HttpStatus.OK) : new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    /**
     * Verifies the given credentials as valid.
     *
     * @param accountEntity The credentials to be verified.
     * @return OK if valid and BAD if not valid.
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity verifyLogin(@RequestBody AccountEntity accountEntity) {
        String hashedPassword = DigestUtils.sha1Hex(accountEntity.getPassword());
        AccountEntity databaseAccountEntity = mongoRepository.findLoginCredsByUsername(accountEntity.getUsername());
        if (databaseAccountEntity != null && databaseAccountEntity.isBanned())
            return new ResponseEntity(databaseAccountEntity, HttpStatus.OK);
        return (databaseAccountEntity != null && databaseAccountEntity.getPassword().equals(hashedPassword)) ? new ResponseEntity(databaseAccountEntity, HttpStatus.OK) : new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    /**
     * Registers the given account.
     *
     * @param accountEntity Account information to be registered.
     * @return OK if new account or BAD if already exists.
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity registerAccount(@RequestBody AccountEntity accountEntity) {
        accountEntity.setPassword(DigestUtils.sha1Hex(accountEntity.getPassword()));
        try {
            mongoRepository.insertNewAccount(accountEntity);
            return new ResponseEntity(HttpStatus.OK);
        } catch (DuplicateKeyException dke) {
            return new ResponseEntity("There already exists and account with this username please use another one", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException iae) {
            return new ResponseEntity(iae.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }


    /**
     * Returns all reports.
     *
     * @return The reports.
     */
    @RequestMapping(value = "/retrievereports", method = RequestMethod.GET)
    public ResponseEntity getReports() {
        return new ResponseEntity(mongoRepository.getReports(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getUser", method = RequestMethod.GET)
    public ResponseEntity getUser(@RequestParam(name = "username") String username){
    AccountEntity accountEntity= mongoRepository.findLoginCredsByUsername(username);
    return new ResponseEntity(accountEntity,HttpStatus.OK);
    }

    @RequestMapping(value = "/getleaders", method = RequestMethod.GET)
    public ResponseEntity getLeaders() {
        java.util.List<AccountEntity> accounts = mongoRepository.findAccounts();
        mergeSort(accounts,0,accounts.size()-1); //not bubbleSort anymore
        return new ResponseEntity(accounts, HttpStatus.OK);
    }

    private void merge(List<AccountEntity> accounts, int l, int m, int r) {
        int i, j, k;
        int n1 = m - l + 1;
        int n2 = r - m;
        List<AccountEntity> leftTemp = new ArrayList<>();
        List<AccountEntity> rightTemp = new ArrayList<>();
        for (i = 0; i < n1; i++)
            leftTemp.add(i,accounts.get(l + i));
        for (j = 0; j < n2; j++)
            rightTemp.add(j,accounts.get(m+1+j));
        i = 0;
        j = 0;
        k = l;
        while (i < n1 && j < n2) {
            if (leftTemp.get(i).getPoints() >= rightTemp.get(j).getPoints()) {
                accounts.set(k,leftTemp.get(i));
                i++;
            } else {
                accounts.set(k,rightTemp.get(j));
                j++;
            }
            k++;
        }
        while (i < n1) {
            accounts.set(k,leftTemp.get(i));
            i++;
            k++;
        }
        while (j < n2) {
            accounts.set(k,rightTemp.get(j));
            j++;
            k++;
        }
    }

   private void mergeSort(List<AccountEntity> accounts, int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(accounts, l, m);
            mergeSort(accounts, m + 1, r);
            merge(accounts, l, m, r);
        }
    }
}