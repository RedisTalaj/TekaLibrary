package com.sda.tekalibrary.controllers;

import com.sda.tekalibrary.entities.User;
import com.sda.tekalibrary.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllUsers(Model model){
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "User/users";
    }

    @GetMapping("/signup")
    public String goToAddUsers(Model model){
        model.addAttribute("user", new User());
        return "signup/signup";
    }

    @PostMapping("/signup")
    public String addUser(@ModelAttribute("user")User user){
        userService.addUser(user);
        return "redirect:/users";
    }


    @GetMapping("/login")
    public String goToLogin(Model model){
        model.addAttribute("user", new User());
        return "Login/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user")User user) {
        User logedInUser = userService.getUserByEmail(user.getEmail());
        if (logedInUser.getPassword().equals(user.getPassword())) {
            user.setRole(logedInUser.getRole());
            if (logedInUser.getRole().equals("Admin")) {
                return "redirect:/users";
            } else {
                //do dergohet te dashboard, por per placeholder vendosim login page
                return "redirect:/login";
            }
        }
        return "";
    }

}
