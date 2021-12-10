package web.controller;

import configuration.Config;
import configuration.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import utilities.WebUtilities;

import javax.servlet.http.HttpServletRequest;

/**
 * A web controller containing routes for home page
 *
 * @author Palak Jain
 */
@Controller
public class HomeController {

    /**
     * Handles Get request for rendering application home page.
     * This is the first page which user will see once using it.
     */
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        // retrieve the ID of this session
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at / route with session id: %s\n", sessionId);

        Object clientInfoObj = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        Object errorMessage = request.getSession().getAttribute(Constants.ERROR_KEY);

        if(clientInfoObj !=  null && errorMessage == null) {
            System.out.printf("Client with session ID %s already exists.\n", sessionId);
            return "redirect:/dashboard";
        } else if(errorMessage != null) {

            //If got an error from another page. Will display it as an error in UI
            model.addAttribute("error", errorMessage);
            request.getSession().removeAttribute(Constants.ERROR_KEY);

            if(clientInfoObj != null) {
                //Logout the user as some error occurred
                request.getSession().invalidate();
            }
        }

        String nonce = WebUtilities.generateNonce(sessionId);

        // Generate url for request to Slack
        String url = WebUtilities.generateSlackAuthorizeURL(Config.getClientId(),
                sessionId,
                nonce,
                Config.getRedirectUrl());

        model.addAttribute("slackAuthorizeUrl", url);
        return "index";
    }
}
