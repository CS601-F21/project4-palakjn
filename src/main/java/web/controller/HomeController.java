package web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        // retrieve the ID of this session
        String sessionId = request.getSession(true).getId();

        System.out.println(sessionId);

        model.addAttribute("slackAuthorizeUrl", "http://www.google.com");
        return "index";
    }


}
