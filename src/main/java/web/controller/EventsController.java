package web.controller;

import configuration.Config;
import configuration.Constants;
import controllers.dbManagers.Events;
import controllers.dbManagers.Tickets;
import controllers.dbManagers.Transactions;
import controllers.dbManagers.Users;
import models.Event;
import models.Ticket;
import models.Transaction;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utilities.FileStorage;
import utilities.Strings;
import utilities.WebUtilities;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

            WebUtilities.checksForAlerts(model, request, "error", "success");
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

        WebUtilities.checksForAlerts(model, request, "error1", "success");

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

        //Deleting the tickets first being bought
        boolean isDeleted = Tickets.deleteTicketByEventId(eventId);

        if(isDeleted) {
            //If deleted then, going to delete all the transactions information
            System.out.printf("Deleted all the tickets bought for the event: %s.\n", eventId);

            //If deleted then deleting the transaction
            isDeleted = Transactions.deleteTransaction(eventId);

            if(isDeleted) {
                //If deleted then, going to delete the event information
                System.out.printf("Deleted all the transactions made on the event: %s.\n", eventId);

                //Deleting the event from the database
                isDeleted = Events.deleteEvent(eventId);

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
            } else {
                request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.EVENT_NOT_DELETED);
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
    public String getEvents(@PathVariable("eventId") String eventId, @RequestParam("numOfTickets") int numOfTickets, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /events/%s/book route with session id: %s.\n", eventId, sessionId);

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }

        boolean isError = false;

        //Creating new ticket for the user
        isError = createTicket(eventId, userInfo.toString(), numOfTickets);

        //Creating new transaction for the user
        if(!isError) {
            //If error occur while creating a transaction then, keeping the behavior like no reporting error in UI and user will not see any transactions in UI.
            createTransaction(eventId, userInfo.toString(), numOfTickets);
        } else {
            //Redirecting user to event page displaying error message
            request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.EVENT_NOT_BOOKED);
            return "redirect:/events/" + eventId;
        }

        //Redirecting user to tickets page
        return "redirect:/tickets";
    }

    /**
     * Creating new ticket object for user bought it
     * @param eventId Event ID for which the user bought the ticket for
     * @param userId ID of a user who bought it
     * @param numOfTickets Number of tickets bought
     * @return true if successful in creating new ticket else false
     */
    private boolean createTicket(String eventId, String userId, int numOfTickets) {
        boolean isError = false;
        boolean deleteTicket = false;

        //Create ticket object holding tickets information
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setEventId(eventId);
        ticket.setUserId(userId);
        ticket.setNumOfTickets(numOfTickets);
        ticket.setHostId(userId);

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

        return isError;
    }

    /**
     * Creating new transaction object for user who bought the ticket
     * @param eventId Event ID for which the user bought the ticket for
     * @param userId ID of a user who bought it
     * @param numOfTickets Number of tickets bought
     */
    private void createTransaction(String eventId, String userId, int numOfTickets) {

        //Create transaction object holding tickets information
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setEventId(eventId);
        transaction.setUserId(userId);
        transaction.setNumOfTickets(numOfTickets);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        transaction.setStatus(String.format(Constants.BOUGHT_STRING, numOfTickets, LocalDate.now().format(formatter)));

        //Inserting transaction information to table
        boolean isSuccess = Transactions.insert(transaction);
        if(isSuccess) {
            System.out.printf("Added transaction %s information to table.\n", transaction.getId());
        } else {
            System.out.printf("Unable to insert transaction information for event %s.\n", eventId);
        }
    }
}
