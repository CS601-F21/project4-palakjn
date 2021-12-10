package web.controller;

import configuration.Constants;
import controllers.dbManagers.Users;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import utilities.Strings;
import utilities.WebUtilities;

import javax.servlet.http.HttpServletRequest;

/**
 * A web controller containing routes for profile page
 *
 * @author Palak Jain
 */
@Controller
public class ProfileController {

    /**
     * Handles GET request for displaying profile of user
     */
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
            model.addAttribute("error", Constants.ERROR_MESSAGES.GENERIC);
        }

        Object error = request.getSession().getAttribute(Constants.ERROR_KEY);
        if(error != null) {
            model.addAttribute("error", error);

            request.getSession().removeAttribute(Constants.ERROR_KEY);
        }

        model.addAttribute("user", user);
        return "profile";
    }

    /**
     * Handles POST request for updating profile
     */
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
            if(Users.updateUser(user, true)) {
                System.out.printf("User %s information updated successfully.\n", user.getName());
            } else {
                System.out.printf("Not able to update user %s information.\n", user.getName());
                request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.GENERIC);
            }
        } else {
            if(Users.updateUser(user, false)) {
                System.out.printf("User %s information updated successfully.\n", user.getName());
            } else {
                System.out.printf("Not able to update user %s information.\n", user.getName());
                request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.GENERIC);
            }
        }

        System.out.printf("Update profile for user: %s", user.getName());

        return "redirect:/profile";
    }
}
