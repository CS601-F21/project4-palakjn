package web.controller;

import configuration.Constants;
import controllers.dbManagers.Users;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utilities.FileStorage;
import utilities.Strings;
import utilities.WebUtilities;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String getProfile(Model model, HttpServletRequest request) {
        System.out.printf("Request comes at GET /profile route with session id: %s.\n", request.getSession(true).getId());

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
        //model.addAttribute("userWithUpdatedProfile", new User());
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("profilePic") MultipartFile file, @ModelAttribute User user, HttpServletRequest request) {
        System.out.printf("Request comes at POST /profile route with session id: %s.\n", request.getSession(true).getId());

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not logged in. Redirect user to login page.
            System.out.println("User is not logged in.");
            return "redirect:/";
        }

        String userId = (String) userInfo;
        user.setId(userId);

        String imageUrl = WebUtilities.downloadImage(file, userId);
        if(!Strings.isNullOrEmpty(imageUrl)) {
            user.setImage(imageUrl);
            Users.updateUser(user, true);
        } else {
            Users.updateUser(user, false);
        }

        System.out.printf("Update profile for user: %s", user.getName());

        return "redirect:/profile";
    }
}
