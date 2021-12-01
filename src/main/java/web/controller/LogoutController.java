package web.controller;

import configuration.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String dashboard(HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /logout route with session id: %s\n", sessionId);

        Object clientInfoObj = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(clientInfoObj == null) {
            //User is not logged in. Redirect user to login page.
            System.out.println("User is not logged in.");
            return "redirect:/";
        }

        request.getSession().invalidate();
        System.out.println("Logout successful");
        return "redirect:/";
    }
}
