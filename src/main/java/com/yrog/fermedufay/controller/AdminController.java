package com.yrog.fermedufay.controller;

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

    private static final String ADMIN_LOGIN_PAGE = "admin/login";
    private static final String ADMIN_DASHBOARD = "admin/dashboard";

    /**
     * Affiche la page de connexion de l'espace admin.
     * Si l'utilisateur est déjà connecté, Spring Security
     * le redirige automatiquement vers le dashboard.
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return ADMIN_LOGIN_PAGE;
    }

    /**
     * Affiche le tableau de bord principal.
     * Accessible uniquement après authentification.
     */
    @GetMapping("/dashboard")
    public String getDashboardPage() {
        return ADMIN_DASHBOARD;
    }
}
