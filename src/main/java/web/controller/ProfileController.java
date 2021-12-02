package web.controller;

import configuration.Constants;
import controllers.dbManagers.Users;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import utilities.Strings;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String dashboard(Model model, HttpServletRequest request) {

        Object clientInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(clientInfo == null) {
            //User is not logged in. Redirect user to login page.
            System.out.println("User is not logged in.");
            return "redirect:/";
        }

        String userId = (String) clientInfo;

        User user = Users.getUserProfile(userId);

        if(user == null) {
            //Unable to get user information
            //TODO: Handle the case
            return "redirect:/";
        }

        model.addAttribute("user", user);
        return "profile";
    }
}
