package com.yrog.fermettetroistilleuls.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    /**
     * Affiche la page d'accueil.
     */
    @GetMapping
    public String getHomePage() {
        log.info("Accès à la page d'accueil");
        return "public/home";
    }

    /**
     * Affiche la page de confirmation après une réservation.
     */
    @GetMapping("/confirmation")
    public String getConfirmationPage() {
        log.info("Accès à la page de confirmation");
        return "public/confirmation";
    }
}
