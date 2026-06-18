package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Affiche la page de connexion admin.
     *
     * @return la vue de connexion
     */
    @GetMapping("/login")
    public String getLoginPage() {
        log.info("Accès à la page de login admin");
        return "admin/login";
    }

    /**
     * Affiche le tableau de bord admin avec toutes les réservations.
     *
     * @param model modèle Thymeleaf
     * @return la vue du dashboard
     */
    @GetMapping("/dashboard")
    public String getDashboardPage(Model model) {
        log.info("Accès au dashboard admin");
        model.addAttribute("giteBookings", adminService.findAllGiteBookings());
        model.addAttribute("activityBookings", adminService.findAllActivityBookings());
        return "admin/dashboard";
    }

    /**
     * Accepte une demande de réservation de gîte.
     *
     * @param id identifiant de la réservation
     * @return redirection vers le dashboard
     */
    @PostMapping("/gite-bookings/{id}/accept")
    public String acceptGiteBooking(@PathVariable Long id) {
        log.info("Acceptation de la réservation gîte id={}", id);
        adminService.acceptGiteBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Refuse une demande de réservation de gîte.
     *
     * @param id identifiant de la réservation
     * @return redirection vers le dashboard
     */
    @PostMapping("/gite-bookings/{id}/reject")
    public String rejectGiteBooking(@PathVariable Long id) {
        log.info("Refus de la réservation gîte id={}", id);
        adminService.rejectGiteBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Remet une réservation de gîte en attente.
     *
     * @param id identifiant de la réservation
     * @return redirection vers le dashboard
     */
    @PostMapping("/gite-bookings/{id}/pending")
    public String pendingGiteBooking(@PathVariable Long id) {
        log.info("Remise en attente de la réservation gîte id={}", id);
        adminService.pendingGiteBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Affiche la page de confirmation avant
     * de remettre une réservation gîte en attente.
     *
     * @param id    identifiant de la réservation
     * @param model modèle Thymeleaf
     * @return la vue de confirmation
     */
    @GetMapping("/gite-bookings/{id}/pending-confirm")
    public String showGitePendingConfirm(@PathVariable Long id, Model model) {
        log.info("Accès à la page de confirmation remise en attente gîte id={}", id);
        model.addAttribute("booking", adminService.findGiteBookingById(id));
        model.addAttribute("type", "gite");
        return "admin/pending-confirm";
    }

    /**
     * Accepte une demande de réservation d'activité.
     *
     * @param id identifiant de la réservation
     * @return redirection vers le dashboard
     */
    @PostMapping("/activity-bookings/{id}/accept")
    public String acceptActivityBooking(@PathVariable Long id) {
        log.info("Acceptation de la réservation activité id={}", id);
        adminService.acceptActivityBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Refuse une demande de réservation d'activité.
     *
     * @param id identifiant de la réservation
     * @return redirection vers le dashboard
     */
    @PostMapping("/activity-bookings/{id}/reject")
    public String rejectActivityBooking(@PathVariable Long id) {
        log.info("Refus de la réservation activité id={}", id);
        adminService.rejectActivityBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Remet une réservation d'activité en attente.
     *
     * @param id identifiant de la réservation
     * @return redirection vers le dashboard
     */
    @PostMapping("/activity-bookings/{id}/pending")
    public String pendingActivityBooking(@PathVariable Long id) {
        log.info("Remise en attente de la réservation activité id={}", id);
        adminService.pendingActivityBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Affiche la page de confirmation avant
     * de remettre une réservation activité en attente.
     *
     * @param id    identifiant de la réservation
     * @param model modèle Thymeleaf
     * @return la vue de confirmation
     */
    @GetMapping("/activity-bookings/{id}/pending-confirm")
    public String showActivityPendingConfirm(@PathVariable Long id, Model model) {
        log.info("Accès à la page de confirmation remise en attente activité id={}", id);
        model.addAttribute("booking", adminService.findActivityBookingById(id));
        model.addAttribute("type", "activity");
        return "admin/pending-confirm";
    }
}
