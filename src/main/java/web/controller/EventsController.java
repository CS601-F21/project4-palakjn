package web.controller;

import configuration.Config;
import configuration.Constants;
import controllers.dbManagers.Events;
import controllers.dbManagers.Tickets;
import controllers.dbManagers.Users;
import models.Event;
import models.Ticket;
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
import java.util.UUID;

/**
 * A web controller containing routes for view/update/delete event/events
 *
 * @author Palak Jain
 */
@Controller
public class EventsController {

    /**
     * Get request handler for displaying all the events
     */
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

            //Checks if there is an error being propagated by other routes which are being redirected here.
            Object error = request.getSession().getAttribute(Constants.ERROR_KEY);
            if(error != null) {
                model.addAttribute("error", error);
                //Removing it in order to not do it again.
                request.getSession().removeAttribute(Constants.ERROR_KEY);
            }

            //Checks if there is a success being propagated by other routes which are being redirected here.
            Object success = request.getSession().getAttribute(Constants.SUCCESS__KEY);
            if(success != null) {
                model.addAttribute("success", success);
                //Removing it in order to not do it again.
                request.getSession().removeAttribute(Constants.SUCCESS__KEY);
            }
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

    /**
     * Post request handler for creating new events
     */
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
            request.getSession().setAttribute(Constants.SUCCESS__KEY, Constants.SUCCESS_MESSAGES.EVENT_CREATED);
            System.out.printf("Event %s added to the table", event.getName());
        } else {
            request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.EVENT_NOT_CREATED);
            System.out.printf("Event %s NOT added to the table", event.getName());
        }

        return "redirect:/events";
    }

    /**
     * Get request for displaying particular event information
     */
    @GetMapping("/events/{eventId}")
    public String getEvent(@PathVariable("eventId") String eventId, Model model, HttpServletRequest request) {
        System.out.printf("Request comes at Get /events/%s route with session id: %s.\n", eventId, request.getSession(true).getId());

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

        //Getting event information from database
        Event event = Events.getEvent(eventId);
        if(event != null) {
            model.addAttribute("event", event);

            //Getting event host information from database
            User user = Users.getUserInfo(event.getHostId());
            if(user != null) {
                model.addAttribute("user", user);
            } else {
                //No user information found
                model.addAttribute("error", Constants.ERROR_MESSAGES.GENERIC);
            }
        }
        else {
            //No event found
            model.addAttribute("error", Constants.ERROR_MESSAGES.GENERIC);
        }

        Object success = request.getSession().getAttribute(Constants.SUCCESS__KEY);
        if(success != null) {
            model.addAttribute("success", success);
            request.getSession().removeAttribute(Constants.SUCCESS__KEY);
        }
        Object error = request.getSession().getAttribute(Constants.ERROR_KEY);
        if(error != null) {
            model.addAttribute("error1", error);
            request.getSession().removeAttribute(Constants.ERROR_KEY);
        }
        return "event";
    }

    /**
     * POST request for updating particular event information
     */
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
        if(eventWithOldSeats != null && (eventWithOldSeats.getAvailability() != 0 || event.getTotal() >= eventWithOldSeats.getTotal())) {
            event.setAvailability(eventWithOldSeats.getAvailability() + (event.getTotal() - eventWithOldSeats.getTotal()));
        }

        String imageUrl = WebUtilities.downloadImage(file, event.getId());

        if(!Strings.isNullOrEmpty(imageUrl)) {
            event.setImageUrl(imageUrl);
            if(Events.updateEvent(event, true)) {
                System.out.printf("Update profile for event: %s", event.getName());
                request.getSession().setAttribute(Constants.SUCCESS__KEY, Constants.SUCCESS_MESSAGES.EVENT_UPDATED);
            } else {
                System.out.printf("Not Updated profile for event: %s", event.getName());
                request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.EVENT_NOT_UPDATED);
            }
        } else {
            if(Events.updateEvent(event, false)) {
                System.out.printf("Update profile for event: %s", event.getName());
                request.getSession().setAttribute(Constants.SUCCESS__KEY, Constants.SUCCESS_MESSAGES.EVENT_UPDATED);
            } else {
                System.out.printf("Not Updated profile for event: %s", event.getName());
                request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.EVENT_NOT_UPDATED);
            }
        }

        return "redirect:/events/" + eventId;
    }

    /**
     * GET request for deleting the particular event.
     */
    @GetMapping("/events/{eventId}/delete")
    public String deleteEvent(@PathVariable("eventId") String eventId, HttpServletRequest request) {
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
                if(FileStorage.deleteFile(Constants.PHOTOS_DIRECTORY, imageUrl)) {
                    System.out.printf("%s/%s deleted successfully.\n", Constants.PHOTOS_DIRECTORY, imageUrl);
                } else {
                    System.out.printf("%s/%s Unable to delete file.\n", Constants.PHOTOS_DIRECTORY, imageUrl);
                }

                request.getSession().setAttribute(Constants.SUCCESS__KEY, Constants.SUCCESS_MESSAGES.EVENT_DELETED);
            }
        } else {
            request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.EVENT_NOT_DELETED);
        }

        return "redirect:/events";
    }

    /**
     * POST request for booking the event
     */
    @PostMapping("/events/{eventId}/book")
    public String getEvents(@PathVariable("eventId") String eventId, @RequestParam("numOfTickets") int numOfTickets, Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /events/%s/book route with session id: %s.\n", eventId, sessionId);

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }

        //Create ticket object holding tickets information
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setEventId(eventId);
        ticket.setUserId(userInfo.toString());
        ticket.setNumOfTickets(numOfTickets);

        boolean isError = false;
        boolean deleteTicket = false;

        //Inserting ticket information to table
        boolean isSuccess = Tickets.insert(ticket);
        if(isSuccess) {
            System.out.printf("Added ticket %s information to table.\n", ticket.getId());

            //get event current availability
            Event event = Events.getEventSeats(eventId);
            if(event != null) {
                event.setAvailability((event.getAvailability() - numOfTickets));

                isSuccess = Events.updateEventSeats(event);
                if(isSuccess) {
                    System.out.println("Updated event availability information in DB");
                } else {
                    isError = true;
                    deleteTicket = true;
                }
            } else {
                isError = true;
                deleteTicket = true;
            }
        } else {
            System.out.printf("Unable to insert ticket information for event %s.\n", eventId);
            isError = true;
        }

        if(deleteTicket) {
            //Making an attempt to delete ticket
            if(Tickets.deleteTicket(ticket.getId())) {
                System.out.printf("Successfully deleted ticket %s.\n", ticket.getId());
            } else {
                System.out.printf("Unable to delete ticket %s.\n", ticket.getId());
            }
        }

        if(isError) {
            //Redirecting user to event page displaying error message
            request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.EVENT_NOT_BOOKED);
            return "redirect:/events/" + eventId;
        }

        //Redirecting user to tickets page
        return "redirect:/tickets/upcomingEvents";
    }
}
