package web.controller;

import configuration.Constants;
import controllers.dbManagers.Events;
import models.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

        model.addAttribute("logout", "/" + userId + "/logout");
        model.addAttribute("eventUrl", "/" + userId + "/events");
        return "eventsWithLogin";
    }
}
