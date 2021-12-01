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

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {

        Object clientInfoObj = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(clientInfoObj == null) {
            //User is not logged in. Redirect user to login page.
            System.out.println("User is not logged in.");
            return "redirect:/";
        }

        String userId = (String) clientInfoObj;

        String name = Users.getUserName(userId);

        if(Strings.isNullOrEmpty(name)) {
            //Unable to get user information
            //TODO: Handle the case
            return "redirect:/";
        }

        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        return "home";
    }
}
