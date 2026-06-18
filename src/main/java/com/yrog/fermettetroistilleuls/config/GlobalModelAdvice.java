package com.yrog.fermettetroistilleuls.config;

import com.yrog.fermettetroistilleuls.service.FermetteInfoService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Injecte les informations de la fermette
 * dans tous les templates Thymeleaf automatiquement.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final FermetteInfoService fermetteInfoService;

    public GlobalModelAdvice(FermetteInfoService fermetteInfoService) {
        this.fermetteInfoService = fermetteInfoService;
    }

    /**
     * Ajoute les infos de la fermette dans tous les modèles.
     */
    @ModelAttribute
    public void addFermetteInfo(Model model) {
        model.addAttribute("fermette", fermetteInfoService.findInfo());
    }
}
