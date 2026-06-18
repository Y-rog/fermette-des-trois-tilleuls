package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * Contrôleur public pour les pages des gîtes.
 * Gère l'affichage de la liste, le détail,
 * le calendrier de disponibilités
 * et le formulaire de réservation.
 */
@Controller
@RequestMapping("/gites")
public class GiteController {

    private static final Logger log = LoggerFactory.getLogger(GiteController.class);

    private final GiteService giteService;
    private final BookingService bookingService;
    private final CalendarService calendarService;

    public GiteController(GiteService giteService,
                          BookingService bookingService,
                          CalendarService calendarService) {
        this.giteService = giteService;
        this.bookingService = bookingService;
        this.calendarService = calendarService;
    }

    /**
     * Affiche la liste de tous les gîtes.
     *
     * @param model modèle Thymeleaf
     * @return la vue de la liste des gîtes
     */
    @GetMapping
    public String getAllGites(Model model) {
        log.info("Accès à la page liste des gîtes");
        model.addAttribute("gites", giteService.findAll());
        return "public/gites";
    }

    /**
     * Affiche le formulaire de réservation pour un gîte
     * ainsi que son calendrier de disponibilités mensuel.
     * Par défaut affiche le mois courant.
     *
     * @param id    identifiant du gîte
     * @param year  année du calendrier (défaut : année courante)
     * @param month mois du calendrier (défaut : mois courant)
     * @param model modèle Thymeleaf
     * @return la vue du formulaire de réservation
     * @throws com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException
     *         si le gîte n'existe pas
     */
    @GetMapping("/{id}")
    public String getGiteBookingPage(
            @PathVariable Long id,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        int currentYear  = (year  != null) ? year  : LocalDate.now().getYear();
        int currentMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        model.addAttribute("gite", giteService.findById(id));
        model.addAttribute("calendar",
                calendarService.buildGiteCalendar(id, currentYear, currentMonth));
        return "public/gite-booking";
    }

    /**
     * Traite le formulaire de réservation d'un gîte.
     * Redirige vers la page de confirmation après succès.
     * En cas d'erreur de validation, réaffiche le formulaire
     * avec le calendrier du mois courant.
     *
     * @param form   formulaire de réservation rempli par le client
     * @param result résultat de la validation Bean Validation
     * @param model  modèle Thymeleaf
     * @return redirection vers la confirmation ou retour au formulaire
     */
    @PostMapping("/booking")
    public String submitBooking(@Valid GiteBookingForm form,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire de réservation invalide : {}", result.getAllErrors());
            model.addAttribute("gite", giteService.findById(form.getGiteId()));
            model.addAttribute("calendar",
                    calendarService.buildGiteCalendar(
                            form.getGiteId(),
                            LocalDate.now().getYear(),
                            LocalDate.now().getMonthValue()));
            return "public/gite-booking";
        }
        bookingService.saveGiteBooking(form);
        log.info("Réservation gîte soumise pour {}", form.getEmail());
        return "redirect:/confirmation";
    }
}
