package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur public pour les pages des activités.
 */
@Controller
@RequestMapping("/activities")
public class ActivityController {
    private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
    private final ActivityService activityService;

    public ActivityController (ActivityService activityService) {
        this.activityService = activityService;

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


}
