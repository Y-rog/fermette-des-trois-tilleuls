package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.dto.GiteDto;
import com.yrog.fermettetroistilleuls.dto.GiteForm;
import com.yrog.fermettetroistilleuls.entity.FermetteInfo;
import com.yrog.fermettetroistilleuls.service.BookingManagementService;
import com.yrog.fermettetroistilleuls.service.FermetteInfoService;
import com.yrog.fermettetroistilleuls.service.GiteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    private final BookingManagementService bookingManagementService;
    private final FermetteInfoService fermetteInfoService;
    private final GiteService giteService;

    public AdminController(BookingManagementService bookingManagementService,
                           FermetteInfoService fermetteInfoService, GiteService giteService) {
        this.bookingManagementService = bookingManagementService;
        this.fermetteInfoService = fermetteInfoService;
        this.giteService = giteService;
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
        model.addAttribute("giteBookings", bookingManagementService.findAllGiteBookings());
        model.addAttribute("activityBookings", bookingManagementService.findAllActivityBookings());
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
        bookingManagementService.acceptGiteBooking(id);
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
        bookingManagementService.rejectGiteBooking(id);
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
        bookingManagementService.pendingGiteBooking(id);
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
        model.addAttribute("booking", bookingManagementService.findGiteBookingById(id));
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
        bookingManagementService.acceptActivityBooking(id);
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
        bookingManagementService.rejectActivityBooking(id);
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
        bookingManagementService.pendingActivityBooking(id);
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
        model.addAttribute("booking", bookingManagementService.findActivityBookingById(id));
        model.addAttribute("type", "activity");
        return "admin/pending-confirm";
    }

    /**
     * Affiche la page de modification des infos de la fermette.
     */
    @GetMapping("/infos")
    public String getInfosPage(Model model) {
        log.info("Accès à la page des infos de la fermette");
        model.addAttribute("infos", fermetteInfoService.findInfo());
        return "admin/infos";
    }

    /**
     * Met à jour les infos de la fermette.
     */
    @PostMapping("/infos")
    public String updateInfos(FermetteInfo infos) {
        fermetteInfoService.updateInfo(infos);
        return "redirect:/admin/dashboard";
    }

    /**
     * Affiche la liste des gîtes pour l'admin
     * avec boutons modifier/supprimer.
     */
    @GetMapping("/gites")
    public String getGitesAdminPage(Model model) {
        log.info("Accès à la gestion des gîtes");
        model.addAttribute("gites", giteService.findAll());
        return "admin/gites-list";
    }

    /**
     * Affiche le formulaire de création d'un gîte.
     */
    @GetMapping("/gites/new")
    public String showCreateGiteForm(Model model) {
        model.addAttribute("giteForm", new GiteForm());
        model.addAttribute("isEdit", false);
        return "admin/gite-form";
    }

    /**
     * Affiche le formulaire d'édition d'un gîte.
     */
    @GetMapping("/gites/{id}/edit")
    public String showEditGiteForm(@PathVariable Long id, Model model) {
        GiteDto gite = giteService.findById(id);

        GiteForm form = new GiteForm();
        form.setName(gite.name());
        form.setLocation(gite.location());
        form.setDescription(gite.description());
        form.setCapacity(gite.capacity());
        form.setBedrooms(gite.bedrooms());
        form.setPhotoUrl(gite.photoUrl());

        model.addAttribute("giteForm", form);
        model.addAttribute("giteId", id);
        model.addAttribute("isEdit", true);
        return "admin/gite-form";
    }

    /**
     * Traite le formulaire de création d'un gîte.
     */
    @PostMapping("/gites/new")
    public String createGite(@Valid GiteForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/gite-form";
        }
        giteService.create(form);
        return "redirect:/admin/gites";
    }

    /**
     * Traite le formulaire d'édition d'un gîte.
     */
    @PostMapping("/gites/{id}/edit")
    public String updateGite(@PathVariable Long id, @Valid GiteForm form,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("giteId", id);
            model.addAttribute("isEdit", true);
            return "admin/gite-form";
        }
        giteService.update(id, form);
        return "redirect:/admin/gites";
    }

    /**
     * Supprime un gîte.
     */
    @PostMapping("/gites/{id}/delete")
    public String deleteGite(@PathVariable Long id) {
        giteService.delete(id);
        return "redirect:/admin/gites";
    }

}