package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthenticationController {
    private final UsersRepository usersRepository;

    @Autowired
    public AuthenticationController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping("/login")
    public String showLoginForm(Model viewModel) {

        viewModel.addAttribute("user", new User());
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid User user, Errors validation, Model viewModel) {

        User auth = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

//        User existingUser = usersRepository.findByUsername(user.getUsername());

        if (auth == null || !auth.getPassword().equals(user.getPassword())) {
            validation.rejectValue(
                    "password",
                    "password",
                    "Username and password combination is incorrect"
            );
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/login";
        }
      return "redirect:/profile";
    }
}