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
import utilities.Strings;
import utilities.WebUtilities;

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
            String nonce = WebUtilities.generateNonce(sessionId);

            // Generate url for request to Slack
            String url = WebUtilities.generateSlackAuthorizeURL(Config.getClientId(),
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
        event.setImageUrl(WebUtilities.downloadImage(file, event.getId()));
        event.setAvailability(event.getTotal());
        event.setHostId(userId);

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
        System.out.printf("Request comes at Get /events/%s route with session id: %s.\n", eventId, request.getSession(true).getId());

        //Getting event information from database
        Event event = Events.getEvent(eventId);
        if(event != null) {
            model.addAttribute("event", event);

            //Getting event host information from database
            User user = Users.getUserInfo(event.getHostId());
            if(user != null) {
                model.addAttribute("user", user);

                String sessionId = request.getSession(true).getId();
                Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
                if(userInfo != null) {
                    //User is already logged in. Will display event information in user's page
                    model.addAttribute("userId", userInfo);
                } else {
                    //User is not logged in. Will display all events but will not allow book/edit/delete option
                    String nonce = WebUtilities.generateNonce(sessionId);

                    // Generate url for request to Slack
                    String url = WebUtilities.generateSlackAuthorizeURL(Config.getClientId(),
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

    @PostMapping("/events/{eventId}")
    public String updateEvent(@PathVariable("eventId") String eventId, @RequestParam("image") MultipartFile file, @ModelAttribute Event event, HttpServletRequest request) {
        System.out.printf("Request comes at POST /events/%s route with session id: %s.\n", eventId, request.getSession(true).getId());

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }
        String userId = (String) userInfo;

        event.setId(eventId);
        event.setHostId(userId);

        Event eventWithOldSeats = Events.getEventSeats(event.getId());
        if(eventWithOldSeats != null && (eventWithOldSeats.getAvailability() != 0 || eventWithOldSeats.getTotal() >= event.getTotal())) {
            event.setAvailability(eventWithOldSeats.getAvailability() + (event.getTotal() - eventWithOldSeats.getTotal()));
        }

        String imageUrl = WebUtilities.downloadImage(file, event.getId());

        if(!Strings.isNullOrEmpty(imageUrl)) {
            event.setImageUrl(imageUrl);
            Events.updateEvent(event, true);
        } else {
            Events.updateEvent(event, false);
        }

        System.out.printf("Update profile for event: %s", event.getName());

        return "redirect:/events/" + eventId;
    }

    @GetMapping("/events/delete/{eventId}")
    public String deleteEvent(@PathVariable("eventId") String eventId, Model model, HttpServletRequest request) {
        System.out.printf("Request comes at GET /events/delete/%s route with session id: %s.\n", eventId, request.getSession(true).getId());

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }

        //First getting the image url from storage
        String imageUrl = Events.getEventImage(eventId);

        //Deleting the event from the database
        boolean isDeleted = Events.deleteEvent(eventId);

        if(isDeleted) {
            //If deleted then, going to delete image file if locally stored.
            System.out.printf("Deleted event: %s.\n", eventId);

            if(!Strings.isNullOrEmpty(imageUrl)) {
                FileStorage.deleteFile(Constants.PHOTOS_DIRECTORY, imageUrl);
            }
        }

        return "redirect:/events";
    }
}
