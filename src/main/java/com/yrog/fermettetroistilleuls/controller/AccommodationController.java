package com.yrog.fermettetroistilleuls.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur public pour la page de présentation
 * des hébergements (gîtes et écolodges).
 */
@Controller
@RequestMapping("/accommodations")
public class AccommodationController {

    private static final Logger log = LoggerFactory.getLogger(AccommodationController.class);

    /**
     * Affiche la page de présentation des hébergements.
     *
     * @return la vue des hébergements
     */
    @GetMapping
    public String getAccommodationsPage() {
        log.info("Accès à la page hébergements");
        return "public/accommodations";
    }
}