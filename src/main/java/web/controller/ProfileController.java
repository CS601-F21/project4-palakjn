package web.controller;

import configuration.Constants;
import controllers.dbManagers.Users;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utilities.FileStorage;

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
        model.addAttribute("userWithUpdatedProfile", new User());
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("profilePic") MultipartFile file, @ModelAttribute User userWithUpdatedProfile, HttpServletRequest request) {
        System.out.printf("Request comes at POST /profile route with session id: %s.\n", request.getSession(true).getId());

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not logged in. Redirect user to login page.
            System.out.println("User is not logged in.");
            return "redirect:/";
        }

        String userId = (String) userInfo;
        userWithUpdatedProfile.setId(userId);
        boolean updateProfileWithImage = false;

        if(!file.isEmpty()) {
            System.out.printf("File is not empty %s. \n", file.getOriginalFilename());

            if(FileStorage.exists(Constants.PHOTOS_DIRECTORY)) {
                String fileName = getFileName(userId, file.getOriginalFilename());
                boolean fileCreated = FileStorage.createFile(file, Constants.PHOTOS_DIRECTORY, fileName);
                if(fileCreated) {
                    userWithUpdatedProfile.setImage(fileName);
                    updateProfileWithImage = true;
                }
            }
        }

        Users.updateUser(userWithUpdatedProfile, updateProfileWithImage);

        return "redirect:/profile";
    }

    private String getFileName(String eventId, String fileName) {
        String extension = getExtension(fileName).isPresent() ? getExtension(fileName).get() : ".png";
        return String.format("%s.%s", eventId, extension);
    }

    //https://www.baeldung.com/java-file-extension
    private Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
