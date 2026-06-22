package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.service.AvailabilityService;
import com.yrog.fermettetroistilleuls.service.CalendarService;
import com.yrog.fermettetroistilleuls.service.GiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Contrôleur admin pour la gestion des disponibilités des gîtes.
 * Séparé d'AdminGiteController pour alléger ce dernier.
 * Toutes les routes sont protégées par Spring Security.
 */
@Controller
@RequestMapping("/admin/gites")
public class AdminAvailabilityController {

    private static final Logger log = LoggerFactory.getLogger(AdminAvailabilityController.class);

    private final GiteService giteService;
    private final CalendarService calendarService;
    private final AvailabilityService availabilityService;

    public AdminAvailabilityController(GiteService giteService,
                                       CalendarService calendarService,
                                       AvailabilityService availabilityService) {
        this.giteService = giteService;
        this.calendarService = calendarService;
        this.availabilityService = availabilityService;
    }

    /**
     * Affiche le calendrier de disponibilités d'un gîte.
     *
     * @param id    identifiant du gîte
     * @param year  année du calendrier
     * @param month mois du calendrier
     * @param model modèle Thymeleaf
     * @return la vue du calendrier
     */
    @GetMapping("/{id}/availabilities")
    public String getGiteAvailabilitiesPage(
            @PathVariable Long id,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        int currentYear  = (year  != null) ? year  : LocalDate.now().getYear();
        int currentMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        log.info("Accès au calendrier admin du gîte id={} {}/{}", id, currentMonth, currentYear);

        model.addAttribute("gite", giteService.findById(id));
        model.addAttribute("calendar",
                calendarService.buildGiteCalendar(id, currentYear, currentMonth));
        return "admin/gite-availabilities";
    }

    /**
     * Bascule la disponibilité d'un gîte pour une date donnée.
     * Les dates RESERVED ne peuvent pas être modifiées.
     *
     * @param id    identifiant du gîte
     * @param year  année
     * @param month mois
     * @param day   jour
     * @return redirection vers le calendrier du même mois
     */
    @PostMapping("/{id}/availabilities/toggle")
    public String toggleAvailability(
            @PathVariable Long id,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {

        LocalDate date = LocalDate.of(year, month, day);
        log.info("Bascule de la disponibilité gîte id={} pour {}", id, date);
        availabilityService.toggleAvailability(id, date);
        return "redirect:/admin/gites/" + id + "/availabilities?year=" + year + "&month=" + month;
    }

    /**
     * Marque toutes les dates du mois comme disponibles.
     * Les dates RESERVED ne sont pas modifiées.
     *
     * @param id    identifiant du gîte
     * @param year  année
     * @param month mois
     * @return redirection vers le calendrier du même mois
     */
    @PostMapping("/{id}/availabilities/month-available")
    public String setMonthAvailable(@PathVariable Long id,
                                    @RequestParam int year,
                                    @RequestParam int month) {
        log.info("Mise en disponible du mois {}/{} pour gîte id={}", month, year, id);
        availabilityService.setMonthAvailable(id, year, month);
        return "redirect:/admin/gites/" + id + "/availabilities?year=" + year + "&month=" + month;
    }

    /**
     * Marque toutes les dates du mois comme indisponibles.
     * Les dates RESERVED ne sont pas modifiées.
     *
     * @param id    identifiant du gîte
     * @param year  année
     * @param month mois
     * @return redirection vers le calendrier du même mois
     */
    @PostMapping("/{id}/availabilities/month-unavailable")
    public String setMonthUnavailable(@PathVariable Long id,
                                      @RequestParam int year,
                                      @RequestParam int month) {
        log.info("Blocage du mois {}/{} pour gîte id={}", month, year, id);
        availabilityService.setMonthUnavailable(id, year, month);
        return "redirect:/admin/gites/" + id + "/availabilities?year=" + year + "&month=" + month;
    }
}
