package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.entity.FermetteInfo;
import com.yrog.fermettetroistilleuls.service.FermetteInfoService;
import com.yrog.fermettetroistilleuls.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur admin pour la gestion des informations
 * générales de la fermette (adresse, contact, horaires...).
 * Toutes les routes sont protégées par Spring Security.
 */
@Controller
@RequestMapping("/admin/infos")
public class AdminInfoController {

    private static final Logger log = LoggerFactory.getLogger(AdminInfoController.class);

    private final FermetteInfoService fermetteInfoService;
    private final FileUploadService fileUploadService;

    public AdminInfoController(FermetteInfoService fermetteInfoService, FileUploadService fileUploadService) {
        this.fermetteInfoService = fermetteInfoService;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Affiche la page de modification des infos de la fermette.
     */
    @GetMapping
    public String getInfosPage(Model model) {
        log.info("Accès à la page des infos de la fermette");
        model.addAttribute("infos", fermetteInfoService.findInfo());
        return "admin/infos";
    }

    /**
     * Met à jour les infos de la fermette.
     */
    @PostMapping
    public String updateInfos(FermetteInfo infos) {
        log.info("Mise à jour des infos de la fermette");
        fermetteInfoService.updateInfo(infos);
        return "redirect:/admin/dashboard";
    }

    /**
     * Upload la grille tarifaire en PDF.
     * Remplace l'ancien fichier si il existe déjà.
     *
     * @param file               fichier PDF uploadé
     * @param redirectAttributes attributs pour le message flash
     * @return redirection vers la page des infos
     */
    @PostMapping("/tarifs/upload")
    public String uploadTarifs(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        log.info("Upload de la grille tarifaire");
        try {
            fileUploadService.saveTarifsPdf(file);
            redirectAttributes.addFlashAttribute("success",
                    "Grille tarifaire mise à jour avec succès !");
        } catch (IllegalArgumentException e) {
            log.warn("Upload refusé : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur upload grille tarifaire", e);
            redirectAttributes.addFlashAttribute("error",
                    "Erreur lors de l'upload.");
        }
        return "redirect:/admin/infos";
    }
}
