package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.models.UserWithRoles;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;


@Controller
public class UsersController {
    private UsersRepository usersRepository;
    private ContactsRepository contactsRepository;
    private PasswordEncoder passwordEncoder;
    private PhoneService phoneService;


    @Autowired
    public UsersController(UsersRepository usersRepository, ContactsRepository contactsRepository,PasswordEncoder passwordEncoder, PhoneService phoneService) {
        this.usersRepository = usersRepository;
        this.contactsRepository = contactsRepository;
        this.passwordEncoder = passwordEncoder;
        this.phoneService = phoneService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model viewModel) {
        viewModel.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    // Errors validation has to be right after the object
    public String registerUser(@Valid User user, Errors validation, Model viewModel, @ModelAttribute User newUser) {

        User existingUser = usersRepository.findByUsername(user.getUsername());

        if (existingUser != null) {
            validation.rejectValue(
                    "username",
                    "username",
                    "This email is already taken."
            );
        }

        boolean validated = phoneService.validatePhoneNumber(user.getContact().getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "contact.phoneNumber",
                    "contact.phoneNumber",
                    "Invalid format: (xxx) xxx-xxxx"
            );
        }

        if (user.getPassword().equals("")) {
            validation.rejectValue(
                    "password",
                    "password",
                    "Password cannot be blank"
            );
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            validation.rejectValue(
                    "confirmPassword",
                    "confirmPassword",
                    "Password confirmation does not match"
            );
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/register";
        }


        user.getContact().setGoogleContact("" + (long) (Math.random() * (double) Long.MAX_VALUE));
        user.getContact().setOutlookContact("" + (long) (Math.random() * (double) Long.MAX_VALUE));

        contactsRepository.save(user.getContact());

//   place the hashing encoder to storing password in a variable
        String hashPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(hashPassword);
        usersRepository.save(newUser);
        authenticate(newUser);
        return "redirect:/profile";
    }

    private void authenticate(User newUser) {
        UserDetails userDetails = new UserWithRoles(newUser, Collections.emptyList());
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth);
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        user = usersRepository.findOne(user.getId());

        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model, HttpServletResponse response) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }
        user = usersRepository.findOne(user.getId());

        model.addAttribute("user", user);

        return "users/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile (@Valid User user, Errors validation, Model viewModel, HttpServletResponse response) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getId() == 0) {
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "redirect:/login";
        }

        boolean validated = phoneService.validatePhoneNumber(user.getContact().getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "contact.phoneNumber",
                    "contact.phoneNumber",
                    "Invalid format: (xxx) xxx-xxxx"
            );
        }


//        PASSWORD VALIDATION
//        If current password field is not empty
        if (!user.getPassword().equals("")) {
//            If current password field does not equal current user's password
            if (!currentUser.getPassword().equals(user.getPassword())){
                validation.rejectValue(
                        "password",
                        "password",
                        "Current password is incorrect"
                );
            } else {

                // If new password field does not equal the confirm password field
                if(user.getNewPassword().equals("")) {
                    validation.rejectValue(
                            "newPassword",
                            "newPassword",
                            "New password cannot be blank"
                    );
                }
                else if (!user.getNewPassword().equals(user.getConfirmNewPassword())) {
                    validation.rejectValue(
                            "confirmNewPassword",
                            "confirmNewPassword",
                            "Password confirmation does not match"
                    );
                // if user is changing password and everything is ok
                } else {
                    // set new password to user
                    user.setPassword(user.getNewPassword());
                }

            }
        // if user does not want to change password
        } else {
            // transfer current user's password to new user object
            user.setPassword(currentUser.getPassword());
        }


        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/edit-profile";
        }

        contactsRepository.save(user.getContact());
        usersRepository.save(user);

        return "redirect:/profile";
    }
}

