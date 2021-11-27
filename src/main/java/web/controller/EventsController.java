package web.controller;

import configuration.Constants;
import controllers.dbManagers.Events;
import models.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utilities.FileStorage;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class EventsController {

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

    @PostMapping("/{userId}/addEvent")
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
            String userDirectory = getUserDirectory(userId);
            String eventDirectory = getEventDirectory(userDirectory, event.getId());

            boolean isUserCreated = FileStorage.createDirectory(userDirectory);

            if(isUserCreated) {
                boolean isEventFolderCreated = FileStorage.createDirectory(eventDirectory);
                if(isEventFolderCreated) {
                    System.out.printf("Creating file inside %s.\n", eventDirectory);
                    boolean fileCreated = FileStorage.createFile(file, eventDirectory, file.getOriginalFilename());
                    if(fileCreated) {
                        event.setImageUrl(getImageDirectory(userId, event.getId(), file.getOriginalFilename()));
                    }
                }
            }
        }

        event.setAvailability(event.getTotal());

        boolean isAdded = Events.insert(event);
        if(isAdded) {
            System.out.printf("Event %s added to the table", event.getName());
        } else {
            System.out.printf("Event %s NOT added to the table", event.getName());
        }

        return "redirect:/" + userId + "/events";
    }

    private String getUserDirectory(String directoryName) {
        return Paths.get("src", "main", "resources", "static", "users", directoryName).toString();
    }

    private String getEventDirectory(String userDirectory, String directoryName) {
        return Paths.get(userDirectory, directoryName).toString();
    }

    private String getImageDirectory(String userId, String eventId, String fileName) {
        return Paths.get("/","users", userId, eventId, fileName).toString();
    }
}
