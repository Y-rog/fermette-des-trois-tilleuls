package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.dto.ActivityBookingForm;
import com.yrog.fermettetroistilleuls.service.ActivityService;
import com.yrog.fermettetroistilleuls.service.BookingService;
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
 * Contrôleur public pour les pages des activités.
 */
@Controller
@RequestMapping("/activities")
public class ActivityController {
    private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
    private final ActivityService activityService;
    private final BookingService bookingService;

    public ActivityController (ActivityService activityService, BookingService bookingService) {
        this.activityService = activityService;
        this.bookingService = bookingService;

    }

    /**
     * Affiche la liste de toutes les activités.
     *
     * @param model modèle Thymeleaf
     * @return la vue de la liste des activités
     */
    @GetMapping
    public String getAllActivities(Model model) {
        log.info("Accès à la page liste des activités");
        model.addAttribute("activities", activityService.findAll());
        return "public/activities";
    }

    /**
     * Affiche le formulaire de réservation pour une activité.
     *
     * @param id    identifiant de l'activité
     * @param model modèle Thymeleaf
     * @return la vue du formulaire de réservation
     */
    @GetMapping("/{id}")
    public String getActivityBookingPage(@PathVariable Long id, Model model) {
        log.info("Accès à la page de réservation de l'activité id={}", id);
        model.addAttribute("activity", activityService.findById(id));
        return "public/activity-booking";
    }

    /**
     * Traite le formulaire de réservation d'une activité.
     * Redirige vers la page de confirmation après succès.
     *
     * @param form   formulaire de réservation rempli par le client
     * @param result résultat de la validation Bean Validation
     * @param model  modèle Thymeleaf
     * @return redirection vers la confirmation ou retour au formulaire
     */
    @PostMapping("/booking")
    public String submitBooking(@Valid ActivityBookingForm form,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire activité invalide : {}", result.getAllErrors());
            model.addAttribute("activity", activityService.findById(form.getActivityId()));
            return "public/activity-booking";
        }

        bookingService.saveActivityBooking(form);
        log.info("Réservation activité soumise pour {}", form.getEmail());

        return "redirect:/confirmation";
    }

}
