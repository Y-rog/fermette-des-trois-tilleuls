package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.service.GiteService;
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

    private final GiteService giteService;

    public HomeController(GiteService giteService) {
        this.giteService = giteService;
    }

    /**
     * Affiche la page d'accueil avec la liste des gîtes.
     */
    @GetMapping
    public String getHomePage(org.springframework.ui.Model model) {
        log.info("Accès à la page d'accueil");
        model.addAttribute("gites", giteService.findAll());
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
