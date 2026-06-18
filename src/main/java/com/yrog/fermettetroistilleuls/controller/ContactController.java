package com.yrog.fermettetroistilleuls.controller;

import com.yrog.fermettetroistilleuls.dto.ContactForm;
import com.yrog.fermettetroistilleuls.service.MailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur public pour la page de contact.
 */
@Controller
@RequestMapping("/contact")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    private final MailService mailService;

    public ContactController(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Affiche la page de contact.
     *
     * @param model modèle Thymeleaf
     * @return la vue de contact
     */
    @GetMapping
    public String getContactPage(Model model) {
        log.info("Accès à la page de contact");
        model.addAttribute("contactForm", new ContactForm());
        return "public/contact";
    }

    /**
     * Traite le formulaire de contact.
     *
     * @param form   formulaire de contact
     * @param result résultat de la validation
     * @return redirection vers la confirmation
     */
    @PostMapping
    public String submitContact(@Valid ContactForm form,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            log.warn("Formulaire de contact invalide");
            return "public/contact";
        }
        mailService.sendContactMessage(form.getNom(), form.getEmail(), form.getMessage());
        log.info("Message de contact envoyé depuis {}", form.getEmail());
        return "redirect:/confirmation";
    }
}