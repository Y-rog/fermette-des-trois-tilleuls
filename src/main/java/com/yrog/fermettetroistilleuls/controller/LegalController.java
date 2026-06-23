package com.yrog.fermettetroistilleuls.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour les pages légales du site.
 */
@Controller
public class LegalController {

    /**
     * Affiche la page des mentions légales.
     */
    @GetMapping("/mentions-legales")
    public String getMentionsLegales() {
        return "public/mentions-legales";
    }

    /**
     * Affiche la politique de confidentialité.
     */
    @GetMapping("/confidentialite")
    public String getConfidentialite() {
        return "public/confidentialite";
    }

    /**
     * Affiche les conditions générales de vente.
     */
    @GetMapping("/cgv")
    public String getCgv() {
        return "public/cgv";
    }

}
