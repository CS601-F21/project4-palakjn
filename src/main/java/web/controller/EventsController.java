package web.controller;

import configuration.Config;
import configuration.Constants;
import controllers.dbManagers.Events;
import controllers.dbManagers.Users;
import models.Event;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utilities.FileStorage;
import utilities.LoginUtilities;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EventsController {

    @GetMapping("/events")
    public String getEvents(Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /events route with session id: %s.\n", sessionId);

        System.out.println("Getting all events from database");
        List<Event> allEvents = Events.getEvents();
        if(allEvents != null && allEvents.size() > 0) {
            System.out.printf("Got %s events from database. \n", allEvents.size());
            model.addAttribute("events", allEvents);
        }

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo != null) {
            //User is already logged in.
            model.addAttribute("userId", userInfo);
            model.addAttribute("event", new Event());
        }
        else {
            //User is not logged in. Will display all events but will not allow book/edit/delete option
            String nonce = LoginUtilities.generateNonce(sessionId);

            // Generate url for request to Slack
            String url = LoginUtilities.generateSlackAuthorizeURL(Config.getClientId(),
                    sessionId,
                    nonce,
                    Config.getRedirectUrl());

            model.addAttribute("slackAuthorizeUrl", url);
        }

        return "events";
    }

    @PostMapping("/events")
    public String addEvent(@RequestParam("image") MultipartFile file, @ModelAttribute Event event, HttpServletRequest request) {
        System.out.printf("Request comes at POST /events route with session id: %s.\n", request.getSession(true).getId());

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }
        String userId = (String) userInfo;

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

        return "redirect:/events";
    }

    @GetMapping("/events/{eventId}")
    public String getEvent(@PathVariable("eventId") String eventId, Model model, HttpServletRequest request) {

        //Getting event information from database
        Event event = Events.getEvent(eventId);
        if(event != null) {
            model.addAttribute("event", event);

            //Getting event host information from database
            User user = Users.getUserIdAndName(event.getHostId());
            if(user != null) {
                model.addAttribute("user", user);

                String sessionId = request.getSession(true).getId();
                Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
                if(userInfo != null) {
                    //User is already logged in. Will display event information in user's page
                    model.addAttribute("userId", userInfo);
                } else {
                    //User is not logged in. Will display all events but will not allow book/edit/delete option
                    String nonce = LoginUtilities.generateNonce(sessionId);

                    // Generate url for request to Slack
                    String url = LoginUtilities.generateSlackAuthorizeURL(Config.getClientId(),
                            sessionId,
                            nonce,
                            Config.getRedirectUrl());

                    model.addAttribute("slackAuthorizeUrl", url);
                }
            } else {
                model.addAttribute("message", Constants.ERROR_MESSAGE);
            }
        }
        else {
            //No event found
            model.addAttribute("message", Constants.ERROR_MESSAGE);
        }

        return "event";
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
