package com.yrog.fermedufay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    private static final String HOME_PAGE = "public/home";

    @GetMapping
    public String getHomePage() {
        return HOME_PAGE;

    }
}
