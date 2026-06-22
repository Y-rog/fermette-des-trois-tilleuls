package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
import com.yrog.fermettetroistilleuls.service.ArchiveService;
import com.yrog.fermettetroistilleuls.service.BookingManagementService;
import com.yrog.fermettetroistilleuls.service.BookingService;
import com.yrog.fermettetroistilleuls.service.CalendarService;
import com.yrog.fermettetroistilleuls.service.GiteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Contrôleur principal de l'espace administrateur.
 * Gère l'authentification et le tableau de bord.
 * Le reste de la gestion admin est réparti dans :
 * AdminBookingController, AdminGiteController,
 * AdminAvailabilityController, AdminInfoController.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final BookingManagementService bookingManagementService;
    private final CalendarService calendarService;
    private final ArchiveService archiveService;
    private final BookingService bookingService;
    private final GiteService giteService;

    public AdminController(BookingManagementService bookingManagementService,
                           CalendarService calendarService,
                           ArchiveService archiveService,
                           BookingService bookingService,
                           GiteService giteService) {
        this.bookingManagementService = bookingManagementService;
        this.calendarService = calendarService;
        this.archiveService = archiveService;
        this.bookingService = bookingService;
        this.giteService = giteService;
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
     * Affiche le tableau de bord admin avec toutes les réservations,
     * le calendrier multi-gîtes et le formulaire de réservation téléphonique.
     *
     * @param year  année du calendrier (optionnel, mois courant par défaut)
     * @param month mois du calendrier (optionnel, mois courant par défaut)
     * @param model modèle Thymeleaf
     * @return la vue du dashboard
     */
    @GetMapping("/dashboard")
    public String getDashboardPage(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        int currentYear  = (year  != null) ? year  : LocalDate.now().getYear();
        int currentMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        log.info("Accès au dashboard admin");
        model.addAttribute("giteBookings", bookingManagementService.findAllGiteBookings());
        model.addAttribute("activityBookings", bookingManagementService.findAllActivityBookings());
        model.addAttribute("multiCalendar", calendarService.buildMultiGiteCalendar(currentYear, currentMonth));
        model.addAttribute("gites", giteService.findAll());
        return "admin/dashboard";
    }

    /**
     * Déclenche l'archivage des réservations terminées
     * (gîtes et activités) et redirige vers le dashboard
     * avec un message de confirmation.
     *
     * @param redirectAttributes attributs pour le message flash
     * @return redirection vers le dashboard
     */
    @PostMapping("/archive")
    public String archiveOldBookings(RedirectAttributes redirectAttributes) {
        log.info("Déclenchement de l'archivage des réservations terminées");
        int giteCount = archiveService.archiveOldGiteBookings();
        int activityCount = archiveService.archiveOldActivityBookings();

        redirectAttributes.addFlashAttribute("success",
                giteCount + " réservation(s) gîte et " + activityCount
                        + " réservation(s) activité archivée(s).");
        return "redirect:/admin/dashboard";
    }

    /**
     * Affiche l'historique complet des réservations archivées
     * (gîtes et activités).
     *
     * @param model modèle Thymeleaf
     * @return la vue de l'historique
     */
    @GetMapping("/history")
    public String getHistoryPage(Model model) {
        log.info("Accès à l'historique des réservations");
        model.addAttribute("giteHistory", archiveService.findGiteBookingsHistory());
        model.addAttribute("activityHistory", archiveService.findActivityBookingsHistory());
        return "admin/history";
    }

    /**
     * Traite le formulaire de réservation téléphonique
     * créée directement depuis le dashboard admin.
     * Le statut est PENDING — à valider après l'appel.
     *
     * @param form               formulaire de réservation
     * @param result             résultat de la validation
     * @param redirectAttributes attributs pour le message flash
     * @return redirection vers le dashboard
     */
    @PostMapping("/bookings/new")
    public String createPhoneBooking(@Valid GiteBookingForm form,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error",
                    "Formulaire invalide — vérifiez les champs obligatoires.");
            return "redirect:/admin/dashboard";
        }
        try {
            bookingService.saveGiteBooking(form);
            redirectAttributes.addFlashAttribute("success",
                    "Réservation téléphonique créée avec succès !");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}