package web.controller;

import configuration.Config;
import configuration.Constants;
import models.ClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import controllers.dbManagers.Users;
import utilities.WebUtilities;
import controllers.ServiceHandler;
import utilities.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

/**
 * A web controller containing routes for login page
 *
 * @author Palak Jain
 */
@Controller
public class LoginController {

    /**
     * Handles GET request to verify if the request received from Slack authorizes the user.
     */
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        // retrieve the ID of this session
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /login route with session id: %s\n", sessionId);

        Object clientInfoObj = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(clientInfoObj != null) {
            System.out.printf("Client with session ID %s already exists. Redirecting to home page\n", sessionId);
            return "redirect:/dashboard";
        }

        // retrieve the code provided by Slack
        String code = request.getParameter(Constants.CODE_KEY);

        String url = WebUtilities.generateSlackTokenURL(Config.getClientId(),
                                                          Config.getClientSecret(),
                                                          code,
                                                          Config.getRedirectUrl());

        // Make the request to the token API
        String responseString = ServiceHandler.send(url, Constants.METHOD.GET);
        Map<String, Object> response = WebUtilities.jsonStrToMap(responseString);

        ClientInfo clientInfo = WebUtilities.verifyTokenResponse(response, sessionId);

        if(clientInfo == null) {
            System.out.println("Oops! Login Unsuccessful");
            request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.GENERIC);
            return "redirect:/";
        }
        else {
            String userId = Users.getUserId(clientInfo.getEmail());
            if(Strings.isNullOrEmpty(userId)) {
                //It is a new user. Generating new user-id for the user.
                userId = UUID.randomUUID().toString();
                boolean isInserted = Users.insertUser(userId, clientInfo.getEmail(), clientInfo.getName());
                if(!isInserted) {
                    System.out.println("Some error while inserting into users table.");
                    request.getSession().setAttribute(Constants.ERROR_KEY, Constants.ERROR_MESSAGES.GENERIC);
                    return "redirect:/";
                }
            }

            request.getSession().setAttribute(Constants.CLIENT_USER_ID, userId);
            return "redirect:/dashboard";
        }
    }
}
