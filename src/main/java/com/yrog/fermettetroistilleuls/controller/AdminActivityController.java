package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.dto.ActivityDto;
import com.yrog.fermettetroistilleuls.dto.ActivityForm;
import com.yrog.fermettetroistilleuls.service.ActivityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur admin pour la gestion des activités :
 * CRUD complet (création, modification, suppression).
 * Toutes les routes sont protégées par Spring Security.
 */
@Controller
@RequestMapping("/admin/activities")
public class AdminActivityController {

    private static final Logger log = LoggerFactory.getLogger(AdminActivityController.class);

    private final ActivityService activityService;

    public AdminActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Affiche la liste des activités pour l'admin
     * avec boutons modifier/supprimer.
     */
    @GetMapping
    public String getActivitiesAdminPage(Model model) {
        log.info("Accès à la gestion des activités");
        model.addAttribute("activities", activityService.findAll());
        return "admin/activities-list";
    }

    /**
     * Affiche le formulaire de création d'une activité.
     */
    @GetMapping("/new")
    public String showCreateActivityForm(Model model) {
        log.info("Affichage du formulaire de création d'activité");
        model.addAttribute("activityForm", new ActivityForm());
        model.addAttribute("isEdit", false);
        return "admin/activity-form";
    }

    /**
     * Affiche le formulaire d'édition d'une activité existante.
     */
    @GetMapping("/{id}/edit")
    public String showEditActivityForm(@PathVariable Long id, Model model) {
        log.info("Affichage du formulaire d'édition de l'activité id={}", id);
        ActivityDto activity = activityService.findById(id);

        ActivityForm form = new ActivityForm();
        form.setName(activity.name());
        form.setDescription(activity.description());
        form.setDate(activity.date());
        form.setTime(activity.time());

        model.addAttribute("activityForm", form);
        model.addAttribute("activityId", id);
        model.addAttribute("isEdit", true);
        return "admin/activity-form";
    }

    /**
     * Traite le formulaire de création d'une activité.
     */
    @PostMapping("/new")
    public String createActivity(@Valid ActivityForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire de création d'activité invalide");
            model.addAttribute("isEdit", false);
            return "admin/activity-form";
        }
        activityService.create(form);
        log.info("Activité créée : {}", form.getName());
        return "redirect:/admin/activities";
    }

    /**
     * Traite le formulaire d'édition d'une activité.
     */
    @PostMapping("/{id}/edit")
    public String updateActivity(@PathVariable Long id, @Valid ActivityForm form,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire d'édition d'activité invalide");
            model.addAttribute("activityId", id);
            model.addAttribute("isEdit", true);
            return "admin/activity-form";
        }
        activityService.update(id, form);
        log.info("Activité {} mise à jour", id);
        return "redirect:/admin/activities";
    }

    /**
     * Supprime une activité.
     * Si des réservations sont en cours pour cette activité,
     * la suppression est refusée et un message d'erreur
     * est affiché sur la liste des activités.
     *
     * @param id                 identifiant de l'activité
     * @param redirectAttributes attributs pour le message flash
     * @return redirection vers la liste des activités
     */
    @PostMapping("/{id}/delete")
    public String deleteActivity(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            activityService.delete(id);
            log.info("Suppression de l'activité id={}", id);
        } catch (IllegalStateException e) {
            log.warn("Suppression de l'activité {} refusée : {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/activities";
    }

    /**
     * Affiche le formulaire de création pré-rempli
     * à partir d'une activité existante (duplication).
     */
    @GetMapping("/{id}/duplicate")
    public String duplicateActivityForm(@PathVariable Long id, Model model) {
        log.info("Duplication de l'activité id={}", id);
        ActivityDto activity = activityService.findById(id);

        ActivityForm form = new ActivityForm();
        form.setName(activity.name());
        form.setDescription(activity.description());
        // date et heure laissées vides pour que l'admin les choisisse

        model.addAttribute("activityForm", form);
        model.addAttribute("isEdit", false);
        return "admin/activity-form";
    }

    /**
     * Affiche la page de confirmation avant suppression d'une activité.
     */
    @GetMapping("/{id}/delete-confirm")
    public String showDeleteActivityConfirm(@PathVariable Long id, Model model) {
        model.addAttribute("activity", activityService.findById(id));
        return "admin/activity-delete-confirm";
    }
}