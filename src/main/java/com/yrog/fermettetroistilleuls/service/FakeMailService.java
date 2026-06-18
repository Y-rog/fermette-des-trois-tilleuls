package com.yrog.fermettetroistilleuls.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Implémentation FAKE du MailService pour le profil dev.
 * Log les emails dans la console au lieu de les envoyer.
 * Aucune configuration SMTP nécessaire en dev.
 */
@Service
@Profile("dev")
public class FakeMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(FakeMailService.class);

    /**
     * Simule l'envoi d'un email de confirmation.
     * Log le message dans la console.
     */
    @Override
    public void sendBookingConfirmation(String email, String firstName) {
        log.info("📧 [FAKE MAIL] Confirmation envoyée à {} ({})", firstName, email);
    }

    /**
     * Simule l'envoi d'un email de refus.
     * Log le message dans la console.
     */
    @Override
    public void sendBookingRejection(String email, String firstName) {
        log.info("📧 [FAKE MAIL] Refus envoyé à {} ({})", firstName, email);
    }

    @Override
    public void sendContactMessage(String nom, String email, String message) {
        log.info("📧 [FAKE MAIL] Message de contact de {} ({}) : {}", nom, email, message);
    }
}