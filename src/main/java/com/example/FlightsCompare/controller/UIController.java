package com.example.FlightsCompare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/login-oauth2")
    public String oauth2LoginPage() {
        return "auth/login-oauth2";
    }

    @GetMapping("/refresh")
    public String refreshPage() {
        return "auth/refresh";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "auth/logout";
    }

    @GetMapping("/validate-token")
    public String validatePage() {
        return "auth/validate";
    }
}
