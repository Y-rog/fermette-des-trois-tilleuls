package com.yrog.fermettetroistilleuls.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Écouteur d'événements d'authentification.
 * Logue les tentatives de connexion réussies et échouées
 * pour détecter d'éventuelles attaques par force brute.
 */
@Component
public class AuthenticationEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationEventListener.class);

    /**
     * Logue une connexion réussie à l'espace admin.
     *
     * @param event l'événement d'authentification réussie
     */
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        log.info("✅ Connexion admin réussie pour l'utilisateur : {}",
                event.getAuthentication().getName());
    }

    /**
     * Logue une tentative de connexion échouée.
     * Permet de détecter des attaques par force brute.
     *
     * @param event l'événement d'échec d'authentification
     */
    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        log.warn("❌ Tentative de connexion échouée pour : {} — Raison : {}",
                event.getAuthentication().getName(),
                event.getException().getMessage());
    }
}
