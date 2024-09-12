package ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.models.User;
import ru.dab.PP_3_1_3_MVC_Boot_Security_Bootstrap.service.UserDetailsServiceImpl;


import java.io.IOException;
import java.security.Principal;

@RestController
public class UserAndLogoutController {

    private UserDetailsServiceImpl userDetailsServiceImpl; // чтобы достать из Principal данные юзера

    @Autowired
    public void setUserService(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping("/user") // JSON(REST) страница отображения данных юзера, который вошел в систему
    public String pageForAuthenticatedUsers(Principal principal) { //principal это не Юзер
        //Authentication a = SecurityContextHolder.getContext().getAuthentication(); // второй вариант вытащить principal не передавая в параметры
        User user = userDetailsServiceImpl.findByUsername(principal.getName());
        return "<div style='display: flex; justify-content: center; align-items: center; height: 100vh; flex-direction: column; text-align: center;'>" +
                "You entered to secured part of web service<br> your name is: " + user.getUsername() + "<br>" +
                "your email: " + user.getUserEmail() + "<br>" +
                "your age: " + user.getUserAge() + "<br>" +
                "<a href='/logout'>Logout</a>" + "</div>";
    }

//    @GetMapping("/logout")
//    public String logoutPage(Principal principal, HttpServletResponse response) throws IOException {
//        if (principal.getName().contains("admin")) {
//            return principal.getName() + ", you have been successfully logged out." + "<br>" +
//                    "<a href='/login'>login</a>" + " or back to " + "<a href='/hello'> hello page </a>";
//        }
//        if (principal.getName().contains("user")) {
//             response.sendRedirect("/login?logout");
//             return null;
//            //return "redirect:/login";
//        } else return "check logoutPage controller";
//    }
@GetMapping("/logout")
public String logoutPage(Principal principal, HttpServletResponse response) throws IOException {
    if (principal.getName().contains("admin")) {
        return "<div style='display: flex; justify-content: center; align-items: center; height: 100vh; flex-direction: column; text-align: center;'>" +
                principal.getName() + ", you have been successfully logged out." + "<br>" +
                "<a href='/login'>login</a>" + " or back to " + "<a href='/hello'> hello page </a>" +
                "</div>";
    }
    else if (principal.getName().contains("user")) {
        response.sendRedirect("/login?logout");
        return null;
    } else {
        return "<div style='display: flex; justify-content: center; align-items: center; height: 100vh; flex-direction: column; text-align: center;'>" +
                "Check logoutPage controller" +
                "</div>";
        //response.sendRedirect("/login?logout");
        //return null;
    }
}

}
