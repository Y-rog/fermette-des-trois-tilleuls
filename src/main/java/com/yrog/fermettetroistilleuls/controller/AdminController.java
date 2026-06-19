package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.service.BookingManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur principal de l'espace administrateur.
 * Gère l'authentification et le tableau de bord.
 * Le reste de la gestion admin est réparti dans :
 * AdminBookingController, AdminGiteController, AdminInfoController.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final BookingManagementService bookingManagementService;

    public AdminController(BookingManagementService bookingManagementService) {
        this.bookingManagementService = bookingManagementService;
    }

    /**
     * Affiche la page de connexion admin.
     */
    @GetMapping("/login")
    public String getLoginPage() {
        log.info("Accès à la page de login admin");
        return "admin/login";
    }

    /**
     * Affiche le tableau de bord admin avec toutes les réservations.
     */
    @GetMapping("/dashboard")
    public String getDashboardPage(Model model) {
        log.info("Accès au dashboard admin");
        model.addAttribute("giteBookings", bookingManagementService.findAllGiteBookings());
        model.addAttribute("activityBookings", bookingManagementService.findAllActivityBookings());
        return "admin/dashboard";
    }
}