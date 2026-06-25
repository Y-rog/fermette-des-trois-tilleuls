package com.yrog.fermettetroistilleuls.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Implémentation FAKE du MailService pour le profil dev.
 * Log les emails dans la console au lieu de les envoyer.
 * Aucune configuration SMTP nécessaire en dev.
 */
@Service
@Profile({"dev", "test"})
public class FakeMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(FakeMailService.class);

    /**
     * Simule l'envoi d'un email de confirmation de réservation de gîte.
     * Log les détails dans la console.
     *
     * @param email     email du client
     * @param firstName prénom du client
     * @param giteName  nom du gîte
     * @param checkIn   date d'arrivée
     * @param checkOut  date de départ
     */
    @Override
    public void sendBookingConfirmation(String email, String firstName,
                                        String giteName, LocalDate checkIn, LocalDate checkOut) {
        log.info("📧 [FAKE MAIL] Confirmation envoyée à {} ({}) — {} du {} au {}",
                firstName, email, giteName, checkIn, checkOut);
    }

    /**
     * Simule l'envoi d'un email de refus de réservation de gîte.
     * Log les détails dans la console.
     *
     * @param email     email du client
     * @param firstName prénom du client
     * @param giteName  nom du gîte
     * @param checkIn   date d'arrivée
     * @param checkOut  date de départ
     */
    @Override
    public void sendBookingRejection(String email, String firstName,
                                     String giteName, LocalDate checkIn, LocalDate checkOut) {
        log.info("📧 [FAKE MAIL] Refus envoyé à {} ({}) — {} du {} au {}",
                firstName, email, giteName, checkIn, checkOut);
    }

    /**
     * Simule l'envoi d'un message de contact.
     * Log le message dans la console.
     *
     * @param nom     nom de l'expéditeur
     * @param email   email de l'expéditeur
     * @param message contenu du message
     */
    @Override
    public void sendContactMessage(String nom, String email, String message) {
        log.info("📧 [FAKE MAIL] Message de contact de {} ({}) : {}", nom, email, message);
    }
}