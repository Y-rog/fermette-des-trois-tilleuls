package com.yrog.fermettetroistilleuls.service;

/**
 * Interface définissant les méthodes d'envoi d'emails.
 * Deux implémentations :
 * - FakeMailService (dev) → log dans la console
 * - SmtpMailService (prod) → vrai SMTP Hostinger
 */
public interface MailService {

    /**
     * Envoie un email de confirmation au client.
     *
     * @param email     email du client
     * @param firstName prénom du client
     */
    void sendBookingConfirmation(String email, String firstName);

    /**
     * Envoie un email de refus au client.
     *
     * @param email     email du client
     * @param firstName prénom du client
     */
    void sendBookingRejection(String email, String firstName);

    /**
     * Envoie un message de contact à la ferme.
     *
     * @param nom     nom de l'expéditeur
     * @param email   email de l'expéditeur
     * @param message message
     */
    void sendContactMessage(String nom, String email, String message);
}
