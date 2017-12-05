package com.sweetjandy.remindr.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String landing() {

        return "/index";
    }

    @GetMapping("/about")
    public  String getAbout() {

        return "/about";
    }

    @GetMapping("/howitworks")
    public String getHowItWorks() {

        return "/howitworks";
    }

}
