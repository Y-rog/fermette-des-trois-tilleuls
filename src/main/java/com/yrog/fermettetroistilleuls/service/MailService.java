package com.yrog.fermettetroistilleuls.service;

import java.time.LocalDate;

/**
 * Interface du service d'envoi d'emails.
 * Définit les contrats d'envoi pour les confirmations,
 * refus de réservation et messages de contact.
 */
public interface MailService {

    /**
     * Envoie un email de confirmation de réservation de gîte
     * avec les détails du séjour.
     *
     * @param email     email du client
     * @param firstName prénom du client
     * @param giteName  nom du gîte
     * @param checkIn   date d'arrivée
     * @param checkOut  date de départ
     */
    void sendBookingConfirmation(String email, String firstName,
                                 String giteName, LocalDate checkIn, LocalDate checkOut);

    /**
     * Envoie un email de refus de réservation de gîte
     * avec les détails du séjour.
     *
     * @param email     email du client
     * @param firstName prénom du client
     * @param giteName  nom du gîte
     * @param checkIn   date d'arrivée
     * @param checkOut  date de départ
     */
    void sendBookingRejection(String email, String firstName,
                              String giteName, LocalDate checkIn, LocalDate checkOut);

    /**
     * Envoie un message de contact à la ferme.
     *
     * @param nom     nom de l'expéditeur
     * @param email   email de l'expéditeur
     * @param message contenu du message
     */
    void sendContactMessage(String nom, String email, String message);
}
