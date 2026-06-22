package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
import com.yrog.fermettetroistilleuls.dto.GiteDto;
import com.yrog.fermettetroistilleuls.dto.GiteForm;
import com.yrog.fermettetroistilleuls.service.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * Contrôleur admin pour la gestion des gîtes :
 * CRUD complet et gestion des disponibilités.
 * Toutes les routes sont protégées par Spring Security.
 */
@Controller
@RequestMapping("/admin/gites")
public class AdminGiteController {

    private static final Logger log = LoggerFactory.getLogger(AdminGiteController.class);

    private final GiteService giteService;
    private final CalendarService calendarService;
    private final AvailabilityService availabilityService;
    private final GitePhotoService gitePhotoService;
    private final BookingService bookingService;
    private final FileUploadService fileUploadService;

    public AdminGiteController(GiteService giteService,
                               CalendarService calendarService,
                               AvailabilityService availabilityService, GitePhotoService gitePhotoService, BookingService bookingService, FileUploadService fileUploadService) {
        this.giteService = giteService;
        this.calendarService = calendarService;
        this.availabilityService = availabilityService;
        this.gitePhotoService = gitePhotoService;
        this.bookingService = bookingService;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Affiche la liste des gîtes pour l'admin
     * avec boutons modifier/supprimer.
     */
    @GetMapping
    public String getGitesAdminPage(Model model) {
        log.info("Accès à la gestion des gîtes");
        model.addAttribute("gites", giteService.findAll());
        return "admin/gites-list";
    }

    /**
     * Affiche le formulaire de création d'un gîte.
     */
    @GetMapping("/new")
    public String showCreateGiteForm(Model model) {
        log.info("Affichage du formulaire de création de gîte");
        model.addAttribute("giteForm", new GiteForm());
        model.addAttribute("isEdit", false);
        return "admin/gite-form";
    }

    /**
     * Affiche le formulaire d'édition d'un gîte existant.
     */
    @GetMapping("/{id}/edit")
    public String showEditGiteForm(@PathVariable Long id, Model model) {
        log.info("Affichage du formulaire d'édition du gîte id={}", id);
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
        model.addAttribute("photos", gitePhotoService.findPhotos(id));
        return "admin/gite-form";
    }

    /**
     * Traite le formulaire de création d'un gîte.
     */
    @PostMapping("/new")
    public String createGite(@Valid GiteForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire de création de gîte invalide");
            model.addAttribute("isEdit", false);
            return "admin/gite-form";
        }
        giteService.create(form);
        log.info("Gîte créé : {}", form.getName());
        return "redirect:/admin/gites";
    }

    /**
     * Traite le formulaire d'édition d'un gîte.
     */
    @PostMapping("/{id}/edit")
    public String updateGite(@PathVariable Long id, @Valid GiteForm form,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire d'édition de gîte invalide");
            model.addAttribute("giteId", id);
            model.addAttribute("isEdit", true);
            return "admin/gite-form";
        }
        giteService.update(id, form);
        log.info("Gîte {} mis à jour", id);
        return "redirect:/admin/gites";
    }

    /**
     * Supprime un gîte.
     * Si des réservations sont en cours pour ce gîte,
     * la suppression est refusée et un message d'erreur
     * est affiché sur la liste des gîtes.
     *
     * @param id                 identifiant du gîte
     * @param redirectAttributes attributs pour le message flash
     * @return redirection vers la liste des gîtes
     */
    @PostMapping("/{id}/delete")
    public String deleteGite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            giteService.delete(id);
            log.info("Suppression du gîte id={}", id);
        } catch (IllegalStateException e) {
            log.warn("Suppression refusée : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/gites";
    }

    /**
     * Affiche le calendrier de disponibilités d'un gîte
     * pour que l'admin puisse le gérer.
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
     * Affiche la page de confirmation avant suppression d'un gîte.
     */
    @GetMapping("/{id}/delete-confirm")
    public String showDeleteGiteConfirm(@PathVariable Long id, Model model) {
        model.addAttribute("gite", giteService.findById(id));
        return "admin/gite-delete-confirm";
    }

    /**
     * Ajoute une photo à un gîte depuis le formulaire d'édition.
     *
     * @param id        identifiant du gîte
     * @param photoUrl  URL de la photo à ajouter
     * @return redirection vers le formulaire d'édition du gîte
     */
    @PostMapping("/{id}/photos/add")
    public String addPhoto(@PathVariable Long id, @RequestParam String photoUrl) {
        log.info("Ajout d'une photo pour le gîte id={}", id);
        gitePhotoService.addPhoto(id, photoUrl);
        return "redirect:/admin/gites/" + id + "/edit";
    }

    /**
     * Supprime une photo d'un gîte.
     *
     * @param giteId  identifiant du gîte (pour la redirection)
     * @param photoId identifiant de la photo à supprimer
     * @return redirection vers le formulaire d'édition du gîte
     */
    @PostMapping("/{giteId}/photos/{photoId}/delete")
    public String deletePhoto(@PathVariable Long giteId, @PathVariable Long photoId) {
        log.info("Suppression de la photo id={} du gîte id={}", photoId, giteId);
        gitePhotoService.deletePhoto(photoId);
        return "redirect:/admin/gites/" + giteId + "/edit";
    }

    /**
     * Affiche le formulaire de création d'une réservation
     * téléphonique pour un gîte.
     */
    @GetMapping("/{id}/bookings/new")
    public String showCreateBookingForm(@PathVariable Long id, Model model) {
        model.addAttribute("gite", giteService.findById(id));
        model.addAttribute("bookingForm", new GiteBookingForm());
        return "admin/gite-booking-form";
    }

    /**
     * Traite le formulaire de création d'une réservation
     * téléphonique. Statut PENDING — à valider après l'appel.
     */
    @PostMapping("/{id}/bookings/new")
    public String createBooking(@PathVariable Long id,
                                @Valid GiteBookingForm form,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("gite", giteService.findById(id));
            return "admin/gite-booking-form";
        }
        form.setGiteId(id);
        bookingService.saveGiteBooking(form);
        return "redirect:/admin/dashboard";
    }

    /**
     * Upload une photo depuis le formulaire d'édition
     * et la sauvegarde sur le serveur.
     *
     * @param id   identifiant du gîte
     * @param file fichier image uploadé
     * @param redirectAttributes attributs pour le message flash
     * @return redirection vers le formulaire d'édition
     */
    @PostMapping("/{id}/photos/upload")
    public String uploadPhoto(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        log.info("Upload photo pour le gîte id={}", id);
        try {
            String url = fileUploadService.saveGitePhoto(file);
            gitePhotoService.addPhoto(id, url);
            redirectAttributes.addFlashAttribute("success", "Photo ajoutée avec succès !");
        } catch (IllegalArgumentException e) {
            log.warn("Upload refusé : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur upload photo", e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'upload.");
        }
        return "redirect:/admin/gites/" + id + "/edit";
    }

}
