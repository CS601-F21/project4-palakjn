package web.controller;

import configuration.Config;
import configuration.Constants;
import models.ClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import utilities.LoginUtilities;
import utilities.ServiceHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@SessionAttributes("ClientInfo")
public class LoginController {

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        // retrieve the ID of this session
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at /login route with session id: %s\n", sessionId);

        Object clientInfoObj = request.getSession().getAttribute(Constants.CLIENT_INFO_KEY);
        if(clientInfoObj != null) {
            System.out.printf("Client with session ID %s already exists.\n", sessionId);
            return "redirect:/home";
        }

        // retrieve the code provided by Slack
        String code = request.getParameter(Constants.CODE_KEY);
        System.out.printf("Received code from request as %s\n", code);

        String url = LoginUtilities.generateSlackTokenURL(Config.getClientId(),
                                                          Config.getClientSecret(),
                                                          code,
                                                          Config.getRedirectUrl());
        System.out.printf("Token URL: %s.\n", url);

        // Make the request to the token API
        String responseString = ServiceHandler.send(url, Constants.METHOD.GET);
        Map<String, Object> response = LoginUtilities.jsonStrToMap(responseString);

        ClientInfo clientInfo = LoginUtilities.verifyTokenResponse(response, sessionId);

        if(clientInfo == null) {
            System.out.println("Oops! Login Unsuccessful");
            return "redirect:/";
        }
        else {
            request.getSession().setAttribute(Constants.CLIENT_INFO_KEY, clientInfo.getEmail());
            model.addAttribute("name", clientInfo.getName());
            return "home";
        }
    }
}
