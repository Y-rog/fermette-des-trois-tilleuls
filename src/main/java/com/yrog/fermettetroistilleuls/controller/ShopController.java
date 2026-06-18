package com.yrog.fermettetroistilleuls.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur public pour la page du magasin.
 * Vitrine uniquement — pas de vente en ligne.
 */
@Controller
@RequestMapping("/shop")
public class ShopController {

    private static final Logger log = LoggerFactory.getLogger(ShopController.class);

    /**
     * Affiche la page magasin.
     */
    @GetMapping
    public String getShopPage() {
        log.info("Accès à la page du magasin");
        return "public/shop";
    }

}
