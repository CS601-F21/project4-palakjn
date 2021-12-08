package web.controller;

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

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Controller
public class TicketController {

    @GetMapping("/tickets/upcomingEvents")
    public String upcomingEvents(Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /tickets/upcomingEvents route with session id: %s.\n", sessionId);

        return getEvents(model, request, true);
    }

    @GetMapping("/tickets/pastEvents")
    public String pastEvents(Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /tickets/pastEvents route with session id: %s.\n", sessionId);

        return getEvents(model, request, false);
    }

    private String getEvents(Model model, HttpServletRequest request, boolean isNew) {
        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }

        List<Ticket> tickets = Tickets.getTickets(userInfo.toString(), isNew);
        if(tickets != null) {
            System.out.printf("Got %d tickets for user %s.\n", tickets.size(), userInfo);

            for(Ticket ticket : tickets) {
                Event event = Events.getEvent(ticket.getEventId());

                if(event != null) {
                    ticket.setEvent(event);

                    List<User> users = Users.getUsersExcept(userInfo.toString());
                    if(users != null) {
                        model.addAttribute("users", users);
                        model.addAttribute("tickets", tickets);
                        model.addAttribute("error", null);
                    } else {
                        model.addAttribute("error", "Internal Server Error. Try Again Later. Contact admin if the problem persists.");
                    }
                } else {
                    model.addAttribute("error", "Internal Server Error. Try Again Later. Contact admin if the problem persists.");
                }
            }
        } else {
            model.addAttribute("error", "Internal Server Error. Try Again Later. Contact admin if the problem persists.");
        }

        model.addAttribute("isNew", isNew);
        return "tickets";


    }

    @PostMapping("/tickets/{ticketId}/share")
    public String upcomingEvents(@PathVariable("ticketId") String ticketId, @RequestParam("selectedUser") String userId, Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /tickets/%s/share route with session id: %s.\n", ticketId, sessionId);

        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }

        Ticket ticket = Tickets.getTicket(ticketId);
        if(ticket != null) {
            ticket.setId(UUID.randomUUID().toString());
            ticket.setUserId(userId);

            //Inserting ticket information to table
            boolean isSuccess = Tickets.insert(ticket);
            if(isSuccess) {
                System.out.printf("Added ticket %s information to table.\n", ticket.getId());
            } else {
                //TODO: Handle the error
            }
        }

        return "redirect:/tickets/upcomingEvents";
    }
}
