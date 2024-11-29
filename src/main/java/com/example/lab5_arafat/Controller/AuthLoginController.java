package com.example.lab5_arafat.Controller;

import com.example.lab5_arafat.Entity.User;
import com.example.lab5_arafat.Service.Implementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthLoginController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/register")
    public String registerForm(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("user", new User());
        if (error != null) {
            model.addAttribute("errorMessage",
                    "Email is already in use. Try another one.");
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        if (userService.findByEmail(user.getEmail()) != null) {
            return "redirect:/register?error=email_exists";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
