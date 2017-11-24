package com.sweetjandy.remindr.controllers;

import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.User;
import com.sweetjandy.remindr.repositories.ContactsRepository;
import com.sweetjandy.remindr.repositories.UsersRepository;
import com.sweetjandy.remindr.services.PhoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;


@Controller
public class UsersController {
    private UsersRepository usersRepository;
    private ContactsRepository contactsRepository;
//    private PasswordEncoder passwordEncoder;


    @Autowired
    public UsersController(UsersRepository usersRepository, ContactsRepository contactsRepository
// ,PasswordEncoder passwordEncoder
                           ) {
        this.usersRepository = usersRepository;
        this.contactsRepository = contactsRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model viewModel) {
        viewModel.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    // Errors validation has to be right after the object
    public String registerUser(@Valid User user, Errors validation, Model viewModel) {

//    user.setPassword(passwordEncoder.encode(user.getPassword()));
//   place the hashing encoder to storing password in a variable

        User existingUser = usersRepository.findByUsername(user.getUsername());

        Contact existingPhoneNumber = contactsRepository.findByPhoneNumber(user.getContact().getPhoneNumber());

        if (existingPhoneNumber != null) {
            validation.rejectValue(
                    "phoneNumber",
                    "user.contact.phoneNumber",
                    "Phone number is already taken"
            );
        }

//        User existingEmail = usersRepository.findByEmail(user.getEmail());

        if (existingUser != null) {
            validation.rejectValue(
                    "username",
                    "user.username",
                    "This email is already taken!"
            );
        }
        if (existingUser != null) {
            validation.rejectValue(
                    "phoneNumber",
                    "user.phoneNumber",
                    "This phone number is already taken!"
            );
        }
//        if (existingEmail != null) {
//            validation.rejectValue(
//                    "email",
//                    "user.email",
//                    "Email already taken!"
//            );
//        }

        boolean validated = PhoneService.validatePhoneNumber(user.getContact().getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "contact.phoneNumber",
                    "contact.phoneNumber",
                    "Invalid format: (xxx)xxx-xxxx"
            );
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/register";
        }


//        String hashPassword = passwordEncoder.encode(user.getPassword());

        //Brandon's previous code
//        user.setContact(contact);

        user.getContact().setGoogleContact((long) (Math.random() * (double) Long.MAX_VALUE));
        user.getContact().setOutlookContact((long) (Math.random() * (double) Long.MAX_VALUE));
        contactsRepository.save(user.getContact());
        user.setPassword(user.getPassword());
        usersRepository.save(user);

        return "redirect:profile";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@Valid User user, Errors validation, Model viewModel) {

//        User existingUser = usersRepository.findByUsername(user.getUsername());
//
//        if (validation.hasErrors()) {
//            viewModel.addAttribute("errors", validation);
//            viewModel.addAttribute("user", user);
//            return "users/login";
//        }

        return "redirect:/profile";

    }

    @GetMapping("/logout")
    public String logout() {

        return "redirect:/login";

    }

    @GetMapping("/profile")
    public String profile(Model model) {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping("/profile/update")
    public String showUpdateProfile(Model model) {
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//        model.addAttribute("user", user);
        return "users/update-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile (@Valid User user, Errors validation, Model viewModel) {

        boolean validated = PhoneService.validatePhoneNumber(user.getContact().getPhoneNumber());
        if (!validated) {
            validation.rejectValue(
                    "contact.phoneNumber",
                    "contact.phoneNumber",
                    "Invalid format: (xxx)xxx-xxxx"
            );
        }

        if (validation.hasErrors()) {
            viewModel.addAttribute("errors", validation);
            viewModel.addAttribute("user", user);
            return "users/register";
        }
//        user.setPassword(user.getPassword());
        contactsRepository.save(user.getContact());
        usersRepository.save(user);

        return "redirect:users/update-profile";
    }
}

