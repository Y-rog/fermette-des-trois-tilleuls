package com.yrog.fermedufay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur de la page d'accueil.
 * Accessible publiquement sans authentification.
 */
@Controller
@RequestMapping("/")
public class HomeController {

    private static final String HOME_PAGE = "public/home";

    /**
     * Affiche la page d'accueil avec la présentation
     * de la fermette, des gîtes et des balades.
     */
    @GetMapping
    public String getHomePage() {
        return HOME_PAGE;
    }
}
