package web.controller;

import configuration.Constants;
import controllers.dbManagers.Events;
import controllers.dbManagers.Transactions;
import models.Event;
import models.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class TransactionController {
    /**
     * Handle GET request for displaying tickets for upcoming events.
     */
    @GetMapping("/transactions/upcomingEvents")
    public String upcomingEvents(Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /transactions/upcomingEvents route with session id: %s.\n", sessionId);

        return getEvents(model, request, true);
    }

    /**
     * Handle GET request for displaying tickets for past events.
     */
    @GetMapping("/transactions/pastEvents")
    public String pastEvents(Model model, HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /transactions/pastEvents route with session id: %s.\n", sessionId);

        return getEvents(model, request, false);
    }

    /**
     * Get events based on whether it is upcoming event or a past one.
     */
    private String getEvents(Model model, HttpServletRequest request, boolean isNew) {
        Object userInfo = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(userInfo == null) {
            //User is not being authorized
            return "redirect:/";
        }

        List<Transaction> transactions = Transactions.getTransactions(userInfo.toString(), isNew);
        if(transactions != null) {
            System.out.printf("Got %d transactions for user %s.\n", transactions.size(), userInfo);

            for(Transaction transaction : transactions) {
                Event event = Events.getEvent(transaction.getEventId());

                if(event != null) {
                    transaction.setEvent(event);

                    model.addAttribute("transactions", transactions);
                } else {
                    model.addAttribute("error", Constants.ERROR_MESSAGES.GENERIC);
                }
            }
        } else {
            model.addAttribute("error", Constants.ERROR_MESSAGES.GENERIC);
        }

        Object error = request.getSession().getAttribute(Constants.ERROR_KEY);
        if(error != null) {
            model.addAttribute("error", error);

            request.getSession().removeAttribute(Constants.ERROR_KEY);
        }

        model.addAttribute("isNew", isNew);
        return "transactions";
    }
}
