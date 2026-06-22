package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.service.BookingManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur admin pour la gestion du cycle de vie
 * des réservations : acceptation, refus, remise en attente.
 * Toutes les routes sont protégées par Spring Security.
 */
@Controller
@RequestMapping("/admin")
public class AdminBookingController {

    private static final Logger log = LoggerFactory.getLogger(AdminBookingController.class);

    private final BookingManagementService bookingManagementService;

    public AdminBookingController(BookingManagementService bookingManagementService) {
        this.bookingManagementService = bookingManagementService;
    }

    /**
     * Accepte une demande de réservation de gîte.
     */
    @PostMapping("/gite-bookings/{id}/accept")
    public String acceptGiteBooking(@PathVariable Long id) {
        log.info("Acceptation de la réservation gîte id={}", id);
        bookingManagementService.acceptGiteBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Refuse une demande de réservation de gîte.
     */
    @PostMapping("/gite-bookings/{id}/reject")
    public String rejectGiteBooking(@PathVariable Long id) {
        log.info("Refus de la réservation gîte id={}", id);
        bookingManagementService.rejectGiteBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Remet une réservation de gîte en attente.
     */
    @PostMapping("/gite-bookings/{id}/pending")
    public String pendingGiteBooking(@PathVariable Long id) {
        log.info("Remise en attente de la réservation gîte id={}", id);
        bookingManagementService.pendingGiteBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Affiche la page de confirmation avant
     * de remettre une réservation gîte en attente.
     */
    @GetMapping("/gite-bookings/{id}/pending-confirm")
    public String showGitePendingConfirm(@PathVariable Long id, Model model) {
        log.info("Accès à la page de confirmation remise en attente gîte id={}", id);
        model.addAttribute("booking", bookingManagementService.findGiteBookingById(id));
        model.addAttribute("type", "gite");
        return "admin/pending-confirm";
    }

    /**
     * Accepte une demande de réservation d'activité.
     */
    @PostMapping("/activity-bookings/{id}/accept")
    public String acceptActivityBooking(@PathVariable Long id) {
        log.info("Acceptation de la réservation activité id={}", id);
        bookingManagementService.acceptActivityBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Refuse une demande de réservation d'activité.
     */
    @PostMapping("/activity-bookings/{id}/reject")
    public String rejectActivityBooking(@PathVariable Long id) {
        log.info("Refus de la réservation activité id={}", id);
        bookingManagementService.rejectActivityBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Remet une réservation d'activité en attente.
     */
    @PostMapping("/activity-bookings/{id}/pending")
    public String pendingActivityBooking(@PathVariable Long id) {
        log.info("Remise en attente de la réservation activité id={}", id);
        bookingManagementService.pendingActivityBooking(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Affiche la page de confirmation avant
     * de remettre une réservation activité en attente.
     */
    @GetMapping("/activity-bookings/{id}/pending-confirm")
    public String showActivityPendingConfirm(@PathVariable Long id, Model model) {
        log.info("Accès à la page de confirmation remise en attente activité id={}", id);
        model.addAttribute("booking", bookingManagementService.findActivityBookingById(id));
        model.addAttribute("type", "activity");
        return "admin/pending-confirm";
    }

    /**
     * Affiche la page de confirmation avant
     * d'accepter une réservation de gîte.
     */
    @GetMapping("/gite-bookings/{id}/accept-confirm")
    public String showGiteAcceptConfirm(@PathVariable Long id, Model model) {
        log.info("Confirmation acceptation réservation gîte id={}", id);
        model.addAttribute("booking", bookingManagementService.findGiteBookingById(id));
        model.addAttribute("type", "gite");
        model.addAttribute("action", "accept");
        return "admin/booking-confirm";
    }

    /**
     * Affiche la page de confirmation avant
     * de refuser une réservation de gîte.
     */
    @GetMapping("/gite-bookings/{id}/reject-confirm")
    public String showGiteRejectConfirm(@PathVariable Long id, Model model) {
        log.info("Confirmation refus réservation gîte id={}", id);
        model.addAttribute("booking", bookingManagementService.findGiteBookingById(id));
        model.addAttribute("type", "gite");
        model.addAttribute("action", "reject");
        return "admin/booking-confirm";
    }

    /**
     * Affiche la page de confirmation avant
     * d'accepter une réservation d'activité.
     */
    @GetMapping("/activity-bookings/{id}/accept-confirm")
    public String showActivityAcceptConfirm(@PathVariable Long id, Model model) {
        log.info("Confirmation acceptation réservation activité id={}", id);
        model.addAttribute("booking", bookingManagementService.findActivityBookingById(id));
        model.addAttribute("type", "activity");
        model.addAttribute("action", "accept");
        return "admin/booking-confirm";
    }

    /**
     * Affiche la page de confirmation avant
     * de refuser une réservation d'activité.
     */
    @GetMapping("/activity-bookings/{id}/reject-confirm")
    public String showActivityRejectConfirm(@PathVariable Long id, Model model) {
        log.info("Confirmation refus réservation activité id={}", id);
        model.addAttribute("booking", bookingManagementService.findActivityBookingById(id));
        model.addAttribute("type", "activity");
        model.addAttribute("action", "reject");
        return "admin/booking-confirm";
    }
}
