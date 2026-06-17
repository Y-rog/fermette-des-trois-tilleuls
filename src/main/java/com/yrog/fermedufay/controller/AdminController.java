package com.yrog.fermedufay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String ADMIN_LOGIN = "admin/login";
    private static final String ADMIN_DASHBOARD = "admin/dashboard";

    @GetMapping("/login")
    public String getLoginPage() {
        return ADMIN_LOGIN;
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return ADMIN_DASHBOARD;
    }

}
