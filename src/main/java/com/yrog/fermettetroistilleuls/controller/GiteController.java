package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
import com.yrog.fermettetroistilleuls.service.BookingService;
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
 * Contrôleur public pour les pages des gîtes.
 */
@Controller
@RequestMapping("/gites")
public class GiteController {

    private static final Logger log = LoggerFactory.getLogger(GiteController.class);

    private final GiteService giteService;
    private final BookingService bookingService;

    public GiteController(GiteService giteService, BookingService bookingService) {
        this.giteService = giteService;
        this.bookingService = bookingService;

    }

    /**
     * Affiche la liste de tous les gîtes.
     */
    @GetMapping
    public String getAllGites(Model model) {
        log.info("Accès à la page liste des gîtes");
        model.addAttribute("gites", giteService.findAll());
        return "public/gites";
    }

    /**
     * Affiche le formulaire de réservation pour un gîte.
     * Accessible via /gites/{id}
     *
     * @param id    identifiant du gîte
     * @param model modèle Thymeleaf
     * @return la vue du formulaire de réservation
     */
    @GetMapping("/{id}")
    public String getGiteBookingPage(@PathVariable Long id, Model model) {
        log.info("Accès au détail du gîte id={}", id);
        model.addAttribute("gite", giteService.findById(id));
        return "public/gite-detail";
    }

    /**
     * Traite le formulaire de réservation d'un gîte.
     * Redirige vers la page de confirmation après succès.
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

        // Si erreurs de validation → retour au formulaire
        if (result.hasErrors()) {
            log.warn("Formulaire de réservation invalide : {}", result.getAllErrors());
            model.addAttribute("gite", giteService.findById(form.getGiteId()));
            return "public/gite-detail";
        }

        // Sauvegarde de la réservation
        bookingService.saveGiteBooking(form);
        log.info("Réservation gîte soumise pour {}", form.getEmail());

        // Redirection vers la page de confirmation (pattern POST/Redirect/GET)
        return "redirect:/confirmation";
    }

}
