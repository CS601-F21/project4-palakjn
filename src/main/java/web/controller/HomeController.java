package web.controller;

import configuration.Config;
import configuration.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import utilities.LoginUtilities;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        // retrieve the ID of this session
        String sessionId = request.getSession(true).getId();
        System.out.printf("Request comes at / route with session id: %s\n", sessionId);

        Object clientInfoObj = request.getSession().getAttribute(Constants.CLIENT_INFO_KEY);
        if(clientInfoObj != null) {
            System.out.printf("Client with session ID %s already exists.\n", sessionId);
            return "redirect:/home";
        }

        String nonce = LoginUtilities.generateNonce(sessionId);

        // Generate url for request to Slack
        String url = LoginUtilities.generateSlackAuthorizeURL(Config.getClientId(),
                sessionId,
                nonce,
                Config.getRedirectUrl());
        System.out.printf("Authorize URL: %s.\n", url);

        model.addAttribute("slackAuthorizeUrl", url);
        return "index";
    }
}
