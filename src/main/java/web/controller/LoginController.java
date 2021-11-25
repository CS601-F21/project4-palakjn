package web.controller;

import configuration.Config;
import configuration.Constants;
import models.ClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import utilities.DBManager;
import utilities.LoginUtilities;
import utilities.ServiceHandler;
import utilities.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        // retrieve the ID of this session
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /login route with session id: %s\n", sessionId);

        Object clientInfoObj = request.getSession().getAttribute(Constants.CLIENT_USER_ID);
        if(clientInfoObj != null) {
            System.out.printf("Client with session ID %s already exists. Redirecting to home page\n", sessionId);
            return "redirect:/dashboard/" + (int)clientInfoObj;
        }

        // retrieve the code provided by Slack
        String code = request.getParameter(Constants.CODE_KEY);

        String url = LoginUtilities.generateSlackTokenURL(Config.getClientId(),
                                                          Config.getClientSecret(),
                                                          code,
                                                          Config.getRedirectUrl());

        // Make the request to the token API
        String responseString = ServiceHandler.send(url, Constants.METHOD.GET);
        Map<String, Object> response = LoginUtilities.jsonStrToMap(responseString);

        ClientInfo clientInfo = LoginUtilities.verifyTokenResponse(response, sessionId);

        if(clientInfo == null) {
            System.out.println("Oops! Login Unsuccessful");
            //TODO: find a way to report an error to front page
            return "redirect:/";
        }
        else {
            String userId = DBManager.getUserId(clientInfo.getEmail());
            if(Strings.isNullOrEmpty(userId)) {
                //It is a new user. Generating new user-id for the user.
                userId = UUID.randomUUID().toString();
                boolean isInserted = DBManager.insertUser(userId, clientInfo.getEmail(), clientInfo.getName());
                if(!isInserted) {
                    System.out.println("Some error while inserting into users table.");
                    //TODO: find a way to report an error to front page
                    return "redirect:/";
                }
            }

            request.getSession().setAttribute(Constants.CLIENT_USER_ID, userId);
            return "redirect:/dashboard/" + userId;
        }
    }
}
