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
 * CRUD complet, gestion des disponibilités et des photos.
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
                               AvailabilityService availabilityService,
                               GitePhotoService gitePhotoService,
                               BookingService bookingService,
                               FileUploadService fileUploadService) {
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
     *
     * @param model modèle Thymeleaf
     * @return la vue de liste des gîtes
     */
    @GetMapping
    public String getGitesAdminPage(Model model) {
        log.info("Accès à la gestion des gîtes");
        model.addAttribute("gites", giteService.findAll());
        return "admin/gites-list";
    }

    /**
     * Affiche le formulaire de création d'un gîte.
     *
     * @param model modèle Thymeleaf
     * @return la vue du formulaire de création
     */
    @GetMapping("/new")
    public String showCreateGiteForm(Model model) {
        log.info("Affichage du formulaire de création de gîte");
        model.addAttribute("giteForm", new GiteForm());
        model.addAttribute("isEdit", false);
        return "admin/gite-form";
    }

    /**
     * Affiche le formulaire d'édition d'un gîte existant
     * avec ses photos actuelles.
     *
     * @param id    identifiant du gîte
     * @param model modèle Thymeleaf
     * @return la vue du formulaire d'édition
     * @throws com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException si le gîte n'existe pas
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
     * Si une photo vitrine est fournie, elle est uploadée
     * et son URL est enregistrée dans le formulaire.
     *
     * @param form          formulaire rempli par l'admin
     * @param result        résultat de la validation
     * @param photoVitrine  fichier image optionnel pour la photo vitrine
     * @param model         modèle Thymeleaf
     * @return redirection vers la liste des gîtes ou retour au formulaire
     */
    @PostMapping("/new")
    public String createGite(@Valid GiteForm form,
                             BindingResult result,
                             @RequestParam(value = "photoVitrine", required = false) MultipartFile photoVitrine,
                             Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire de création de gîte invalide");
            model.addAttribute("isEdit", false);
            return "admin/gite-form";
        }

        // Créer le gîte d'abord pour obtenir l'id
        Long giteId = giteService.create(form);

        // Upload photo vitrine si fournie
        if (photoVitrine != null && !photoVitrine.isEmpty()) {
            try {
                String url = fileUploadService.saveGitePhoto(photoVitrine);
                form.setPhotoUrl(url);
                giteService.update(giteId, form);
            } catch (Exception e) {
                log.warn("Erreur upload photo vitrine : {}", e.getMessage());
            }
        }

        log.info("Gîte créé : {}", form.getName());
        // Rediriger vers l'édition pour ajouter les photos de galerie
        return "redirect:/admin/gites/" + giteId + "/edit";
    }

    /**
     * Traite le formulaire d'édition d'un gîte.
     * Si une nouvelle photo vitrine est fournie, elle remplace
     * la précédente.
     *
     * @param id            identifiant du gîte
     * @param form          formulaire rempli par l'admin
     * @param result        résultat de la validation
     * @param photoVitrine  fichier image optionnel pour la photo vitrine
     * @param model         modèle Thymeleaf
     * @return redirection vers la liste des gîtes ou retour au formulaire
     */
    @PostMapping("/{id}/edit")
    public String updateGite(@PathVariable Long id,
                             @Valid GiteForm form,
                             BindingResult result,
                             @RequestParam(value = "photoVitrine", required = false) MultipartFile photoVitrine,
                             Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire d'édition de gîte invalide");
            model.addAttribute("giteId", id);
            model.addAttribute("isEdit", true);
            return "admin/gite-form";
        }
        // Upload photo vitrine si fournie
        if (photoVitrine != null && !photoVitrine.isEmpty()) {
            try {
                String url = fileUploadService.saveGitePhoto(photoVitrine);
                form.setPhotoUrl(url);
            } catch (Exception e) {
                log.warn("Erreur upload photo vitrine : {}", e.getMessage());
            }
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
     * Affiche la page de confirmation avant suppression d'un gîte.
     *
     * @param id    identifiant du gîte
     * @param model modèle Thymeleaf
     * @return la vue de confirmation de suppression
     */
    @GetMapping("/{id}/delete-confirm")
    public String showDeleteGiteConfirm(@PathVariable Long id, Model model) {
        log.info("Confirmation suppression gîte id={}", id);
        model.addAttribute("gite", giteService.findById(id));
        return "admin/gite-delete-confirm";
    }

    /**
     * Affiche le calendrier de disponibilités d'un gîte
     * pour que l'admin puisse le gérer.
     *
     * @param id    identifiant du gîte
     * @param year  année du calendrier (défaut : année courante)
     * @param month mois du calendrier (défaut : mois courant)
     * @param model modèle Thymeleaf
     * @return la vue du calendrier de disponibilités
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
     * @param year  année de la date à basculer
     * @param month mois de la date à basculer
     * @param day   jour de la date à basculer
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
     * Upload une photo de galerie depuis le formulaire d'édition
     * et la sauvegarde sur le serveur.
     * Valide le type MIME et la taille avant sauvegarde.
     *
     * @param id                 identifiant du gîte
     * @param file               fichier image uploadé
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

    /**
     * Supprime une photo de galerie d'un gîte.
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
     *
     * @param id    identifiant du gîte
     * @param model modèle Thymeleaf
     * @return la vue du formulaire de réservation admin
     */
    @GetMapping("/{id}/bookings/new")
    public String showCreateBookingForm(@PathVariable Long id, Model model) {
        log.info("Affichage du formulaire de réservation téléphonique gîte id={}", id);
        model.addAttribute("gite", giteService.findById(id));
        model.addAttribute("bookingForm", new GiteBookingForm());
        return "admin/gite-booking-form";
    }

    /**
     * Traite le formulaire de création d'une réservation téléphonique.
     * Le statut est PENDING — l'admin valide après avoir raccroché.
     *
     * @param id     identifiant du gîte
     * @param form   formulaire de réservation
     * @param result résultat de la validation
     * @param model  modèle Thymeleaf
     * @return redirection vers le dashboard ou retour au formulaire
     */
    @PostMapping("/{id}/bookings/new")
    public String createBooking(@PathVariable Long id,
                                @Valid GiteBookingForm form,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire de réservation téléphonique invalide");
            model.addAttribute("gite", giteService.findById(id));
            return "admin/gite-booking-form";
        }
        form.setGiteId(id);
        bookingService.saveGiteBooking(form);
        log.info("Réservation téléphonique créée pour le gîte id={}", id);
        return "redirect:/admin/dashboard";
    }
}
