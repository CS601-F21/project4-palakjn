package web.controller;

import configuration.Config;
import configuration.Constants;
import controllers.dbManagers.Events;
import controllers.dbManagers.Users;
import models.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utilities.FileStorage;
import utilities.LoginUtilities;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EventsController {

    @GetMapping("/events")
    public String events(Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /events route with session id: %s.\n", sessionId);

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo != null) {
            //User is already logged in. Redirecting the user to /:userId/events page
            return "redirect:/" + userInfo + "/events";
        }

        List<Event> allEvents = Events.getEvents();
        if(allEvents != null && allEvents.size() > 0) {
            model.addAttribute("events", allEvents);
        }

        String nonce = LoginUtilities.generateNonce(sessionId);

        // Generate url for request to Slack
        String url = LoginUtilities.generateSlackAuthorizeURL(Config.getClientId(),
                sessionId,
                nonce,
                Config.getRedirectUrl());

        model.addAttribute("slackAuthorizeUrl", url);
        return "eventsWithoutLogin";
    }

    @GetMapping("/{userId}/events")
    public String events(@PathVariable("userId") String userId, Model model, HttpServletRequest request) {
        System.out.printf("Request comes at /%s/events route with session id: %s.\n", userId, request.getSession(true).getId());

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        } else if(!userInfo.toString().equals(userId)) {
            //Active session is not for the userId. Redirecting the user to login first.
            System.out.printf("User %s is not logged in. User %s is the active session. \n", userId, userInfo);
            return "redirect:/";
        }

        List<Event> allEvents = Events.getEvents();
        if(allEvents != null && allEvents.size() > 0) {
            model.addAttribute("events", allEvents);
        }

        model.addAttribute("userId", userId);
        model.addAttribute("event", new Event());
        return "eventsWithLogin";
    }

    @PostMapping("/{userId}/events")
    public String addEvent(@PathVariable("userId") String userId, @RequestParam("image") MultipartFile file, @ModelAttribute Event event, HttpServletRequest request) {
        System.out.printf("Request comes at /%s/addEvent route with session id: %s.\n", userId, request.getSession(true).getId());

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        } else if(!userInfo.toString().equals(userId)) {
            //Active session is not for the userId. Redirecting the user to login first.
            System.out.printf("User %s is not logged in. User %s is the active session. \n", userId, userInfo);
            return "redirect:/";
        }

        event.setId(UUID.randomUUID().toString());

        if(!file.isEmpty()) {
            System.out.printf("File is not empty %s. \n", file.getOriginalFilename());

            if(FileStorage.exists(Constants.EVENTS_DIRECTORY)) {
                String fileName = getFileName(event.getId(), file.getOriginalFilename());
                boolean fileCreated = FileStorage.createFile(file, Constants.EVENTS_DIRECTORY, fileName);
                if(fileCreated) {
                    event.setImageUrl(fileName);
                }
            }
        }

        event.setAvailability(event.getTotal());
        event.setHostId(userId);
        event.setHost(Users.getUserName(userId));

        boolean isAdded = Events.insert(event);
        if(isAdded) {
            System.out.printf("Event %s added to the table", event.getName());
        } else {
            System.out.printf("Event %s NOT added to the table", event.getName());
        }

        return "redirect:/" + userId + "/events";
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
