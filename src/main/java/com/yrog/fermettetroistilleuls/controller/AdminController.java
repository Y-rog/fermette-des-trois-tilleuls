package com.yrog.fermettetroistilleuls.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur de l'espace administrateur.
 * Toutes les routes sont protégées par Spring Security.
 * Seul un utilisateur avec le rôle ADMIN peut y accéder.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    /**
     * Affiche la page de connexion admin.
     */
    @GetMapping("/login")
    public String getLoginPage() {
        log.info("Accès à la page de login admin");
        return "admin/login";
    }

    /**
     * Affiche le tableau de bord admin.
     */
    @GetMapping("/dashboard")
    public String getDashboardPage() {
        log.info("Accès au dashboard admin");
        return "admin/dashboard";
    }
}
