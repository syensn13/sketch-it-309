package SS_7.sketch_it.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PageRedirectionController {

    /**
     *  Returns the default page as determined by the team.
     * @return The default page.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getDefaultPage() {
        return "login";
    } //change this to whatever we want to default to

    /**
     * Returns the login page.
     * @return The login page.
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getHomePage() {
        return "login";
    }

    /**
     * Returns the registration page.
     * @return The registration page.
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String getRegistrationPage() {
        return "registration";
    }

    /**
     * Returns the game room page.
     * @return The game room page.
     */
    @RequestMapping(value = "/gameroom", method = RequestMethod.GET)
    public String getGameRoomPage() { return "gameroom"; }

    /**
     * Returns the admin page.
     * @return The admin page.
     */
    @RequestMapping(value = "/adminpage", method = RequestMethod.GET)
    public String getAdminpage() { return "adminpage"; }

    /**
     * Returns the main menu page.
     * @return The main menu page.
     */
    @RequestMapping(value = "/mainmenu", method = RequestMethod.GET)
    public String getMainMenu() {
        return "mainmenu";
    }

    /**
     * Returns the leaderboards page.
     * @return The leaderboards page.
     */
    @RequestMapping(value = "/leaderboards", method = RequestMethod.GET)
    public String getLeaderboards() {
        return "leaderboards";
    }

    /**
     * Returns the game room development page.
     * @return The game room development page.
     */
    @RequestMapping(value = "/gameroomdev", method = RequestMethod.GET)
    public String getGameRoomDev() {
        return "gameroomBackup";
    }

}
