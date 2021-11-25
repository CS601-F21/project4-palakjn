package web.controller;

import configuration.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import controllers.dbManagers.Users;
import utilities.Strings;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {

    @GetMapping("/dashboard/{userId}")
    public String dashboard(@PathVariable("userId") String userId, Model model, HttpServletRequest request) {

        Object clientInfoObj = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(clientInfoObj == null) {
            //User is not logged in. Redirect user to login page.
            System.out.printf("User %s is not logged in.\n", userId);
            return "redirect:/";
        }
        else if(!clientInfoObj.equals(userId)) {
            //Active session is not for the userId. Redirecting the user to login first.
            System.out.printf("User %s is not logged in. User %s is the active session. \n", userId, clientInfoObj);
            return "redirect:/";
        }

        String name = Users.getUserName(userId);

        if(Strings.isNullOrEmpty(name)) {
            //Unable to get user information
            //TODO: Handle the case
            return "redirect:/";
        }

        model.addAttribute("name", name);
        model.addAttribute("logout", "/" + userId + "/logout");
        model.addAttribute("eventUrl", "/" + userId + "/events");
        return "home";
    }
}
