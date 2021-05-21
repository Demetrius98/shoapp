package com.javaMessenger.Messenger.controller;

import com.javaMessenger.Messenger.domain.User;
import com.javaMessenger.Messenger.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Controller for Authorization tasks
 *
 * @author dmitry
 */
@Controller
public class LoginController {

    @Autowired
    private CustomUserDetailsService userService;

    /**
     * GET Method @return login.html
     *  */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    /**
     * GET Method @return signup.html
     *  */
    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public ModelAndView signup() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("signup");
        return modelAndView;
    }

    /**
     * POST Method for transfering registration user data
     * @param user - user data: {email, password}
     * */
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the username provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("signup");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("login");

        }
        return modelAndView;
    }

    /**
     * GET Method @return dashboard.html
     * @param /dashboard - main page of application with chat
     *  */
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView dashboard(HttpServletRequest request, Model model, @RequestParam(required = false, name = "email") String emailTo) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        User userTo = userService.findUserByEmail(emailTo);

        if (!request.getSession().isNew()) {
            //String username = (String) request.getSession().getAttribute("username");
            model.addAttribute("username", user.getFullname());
        }

        List<User> allUsersList = userService.getAllUsers();

        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("fullName", user.getFullname());
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        //modelAndView.addObject("allUsers", user.getAllUsers());
        modelAndView.addObject("listOfUsers", allUsersList);
        modelAndView.addObject("emailTo", emailTo);
        modelAndView.addObject("usernameTo", userTo.getFullname());

        /*for (User s : allUsersList) {
            System.out.println(s.getFullname().toString());
        }*/

        //System.out.println (emailTo);
        modelAndView.setViewName("dashboard");
        return modelAndView;
    }

    /**
     * GET Method @return home.html
     *  */
    @RequestMapping(value = {"/","/home"}, method = RequestMethod.GET)
    public ModelAndView home(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");

        return modelAndView;
    }

    /**
     * POST Method for logout
     *
     * @param /logout close WebSocket and return home page
     *  */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ModelAndView logout(HttpServletRequest request) {

        //close WebSocket onLogout and return home page
        request.getSession(true).invalidate();
        return home(request);
    }

}
