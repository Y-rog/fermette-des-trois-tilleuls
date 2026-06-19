package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.entity.FermetteInfo;
import com.yrog.fermettetroistilleuls.service.FermetteInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    public AdminInfoController(FermetteInfoService fermetteInfoService) {
        this.fermetteInfoService = fermetteInfoService;
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
}
