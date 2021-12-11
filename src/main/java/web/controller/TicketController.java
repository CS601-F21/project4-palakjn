package web.controller;

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
import utilities.WebUtilities;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * A web controller containing routes for tickets page
 *
 * @author Palak Jain
 */
@Controller
public class TicketController {

    /**
     * Handle GET request for displaying tickets
     */
    @GetMapping("/tickets")
    public String displayTickets(Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /tickets/upcomingEvents route with session id: %s.\n", sessionId);

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }

        //Getting the tickets owned by the user.
        List<Ticket> tickets = Tickets.getTickets(userInfo.toString());
        if(tickets != null) {
            System.out.printf("Got %d tickets for user %s.\n", tickets.size(), userInfo);

            for(Ticket ticket : tickets) {
                Event event = Events.getEvent(ticket.getEventId());

                if(event != null) {
                    ticket.setEvent(event);

                    List<User> users = Users.getUsers();
                    if(users != null) {
                        User currentUser = getUser(users, userInfo.toString());
                        User sharedByUser = getUser(users, ticket.getHostId());
                        if(currentUser != null) {
                            if(sharedByUser != null && !ticket.getHostId().equals(userInfo.toString())) {
                                //If the ticket is being shared by another user then, displaying the name Who shared the ticket in UI
                                ticket.setSharedMsg(String.format(Constants.SHARED_BY_STRING, sharedByUser.getName()));
                            }

                            //Removing current user so that it will not get displayed in UI
                            users.remove(currentUser);
                        }
                        model.addAttribute("users", users);
                        model.addAttribute("tickets", tickets);
                    } else {
                        model.addAttribute("error", Constants.ERROR_MESSAGES.GENERIC);
                    }
                } else {
                    model.addAttribute("error", Constants.ERROR_MESSAGES.GENERIC);
                }
            }
        } else {
            model.addAttribute("error", Constants.ERROR_MESSAGES.GENERIC);
        }

        WebUtilities.checksForAlerts(model, request, "error", "success");

        return "tickets";
    }

    /**
     * Handled POST request for sharing the ticket to another user
     */
    @PostMapping("/tickets/{ticketId}/share")
    public String shareTicket(@PathVariable("ticketId") String ticketId, @RequestParam("selectedUser") String userId, @RequestParam("numOfTickets") int numOfTickets, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /tickets/%s/share route with session id: %s.\n", ticketId, sessionId);

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }

        Ticket newTicket = new Ticket();
        newTicket.setId(UUID.randomUUID().toString());
        newTicket.setNumOfTickets(numOfTickets);
        newTicket.setUserId(userId);
        newTicket.setHostId(userInfo.toString());

        Ticket currentTicket = Tickets.getTicket(ticketId);

        boolean isError = false;

        if(currentTicket != null) {
            newTicket.setEventId(currentTicket.getEventId());

            if(numOfTickets == currentTicket.getNumOfTickets()) {
                //Deleting the ticket from the user's ticket collection who originally bought it
                isError = !Tickets.deleteTicket(currentTicket.getId()); //If got an error while deleting the ticket then, will not let ticket to be shared to another user
            } else {
                //Updating the current ticket with left number of tickets
                boolean isUpdated = Tickets.updateTicket(currentTicket.getId(), currentTicket.getNumOfTickets() - numOfTickets);
                if(isUpdated) {
                    System.out.printf("Updated existing ticket %s with the number of tickets from %s to %s.\n", currentTicket.getId(), currentTicket.getNumOfTickets(), currentTicket.getNumOfTickets() - numOfTickets);
                } else {
                    //If got an error while updating the ticket then, will not let ticket to be shared to another user
                    isError = true;
                }
            }
        } else {
            //If got an error while getting the current ticket then, will not let ticket to be shared to another user
            isError = true;
        }

        if(!isError) {
            //Inserting ticket information to table
            boolean isSuccess = Tickets.insert(newTicket);
            if(isSuccess) {
                System.out.printf("Added ticket %s information to table.\n", newTicket.getId());
                request.getSession().setAttribute(Constants.SUCCESS__KEY, Constants.SUCCESS_MESSAGES.TICKET_SENT);

                //Adding new transaction to the current user stating that user has shared this number of tickets to another user
                Transaction transaction = new Transaction();
                transaction.setId(UUID.randomUUID().toString());
                transaction.setNumOfTickets(numOfTickets);
                transaction.setUserId(userInfo.toString());
                transaction.setEventId(currentTicket.getEventId());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                transaction.setStatus(String.format(Constants.SHARED_TO_STRING, numOfTickets, numOfTickets == 1 ? "ticket" : "tickets", Users.getUserName(userId), LocalDate.now().format(formatter)));

                if(Transactions.insert(transaction)) { //If got an error while adding new transaction then, I will simply ignore it. Impact will be that user will not see the new transaction
                    System.out.printf("Added new transaction by user %s.\n", userInfo.toString());
                }
            } else {
                System.out.printf("Unable to add ticket %s information to table.\n", newTicket.getId());
                request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.GENERIC);
            }
        } else {
            request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.GENERIC);
        }

        return "redirect:/tickets";
    }

    /**
     * Get the user object from the collection
     * @param users List of all users
     * @param userId ID of a user which we want
     * @return User object if found else null
     */
    private User getUser(List<User> users, String userId) {
        User currentUser = null;

        for (User user : users) {
            if(user.getId().equals(userId)) {
                currentUser = user;
                break;
            }
        }

        return currentUser;
    }
}
