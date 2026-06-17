package com.yrog.fermedufay.controller;

import com.yrog.fermedufay.service.GiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Contrôleur public pour les pages des gîtes.
 */
@Controller
@RequestMapping("/gites")
public class GiteController {

    private static final Logger log = LoggerFactory.getLogger(GiteController.class);

    private final GiteService giteService;

    public GiteController(GiteService giteService) {
        this.giteService = giteService;
    }

    /**
     * Affiche la liste de tous les gîtes.
     */
    @GetMapping
    public String getAllGites(Model model) {
        log.info("Accès à la page liste des gîtes");
        model.addAttribute("gites", giteService.findAll());
        return "public/gites";
    }
}
