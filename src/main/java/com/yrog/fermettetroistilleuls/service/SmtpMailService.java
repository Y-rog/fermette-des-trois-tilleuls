package com.yrog.fermettetroistilleuls.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Implémentation SMTP du MailService pour le profil prod.
 * Envoie de vrais emails via le serveur SMTP Hostinger.
 */
@Service
@Profile("prod")
public class SmtpMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpMailService.class);
    private static final DateTimeFormatter FR_DATE =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH);

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    public SmtpMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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
    @Override
    public void sendBookingConfirmation(String email, String firstName,
                                        String giteName, LocalDate checkIn, LocalDate checkOut) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Votre réservation est confirmée — Fermette des Trois Tilleuls");
            helper.setText(buildConfirmationEmail(firstName, giteName, checkIn, checkOut), true);

            mailSender.send(message);
            log.info("Email de confirmation envoyé à {}", email);

        } catch (MessagingException e) {
            log.error("Erreur envoi email confirmation à {}", email, e);
        }
    }

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
    @Override
    public void sendBookingRejection(String email, String firstName,
                                     String giteName, LocalDate checkIn, LocalDate checkOut) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Votre demande de réservation — Fermette des Trois Tilleuls");
            helper.setText(buildRejectionEmail(firstName, giteName, checkIn, checkOut), true);

            mailSender.send(message);
            log.info("Email de refus envoyé à {}", email);

        } catch (MessagingException e) {
            log.error("Erreur envoi email refus à {}", email, e);
        }
    }

    /**
     * Construit le corps HTML de l'email de confirmation.
     */
    private String buildConfirmationEmail(String firstName, String giteName,
                                          LocalDate checkIn, LocalDate checkOut) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; color: #3A332A;
                         max-width: 600px; margin: 0 auto;">

                <div style="background-color: #38462F; padding: 24px;
                            border-radius: 8px 8px 0 0; text-align: center;">
                    <img src="https://fermette.y-rog.com/img/logo-fermette.png"
                         alt="Fermette des Trois Tilleuls"
                         style="height: 80px; width: auto;"/>
                </div>

                <div style="background-color: white; padding: 32px;
                            border: 1px solid #EFE7D8; border-top: none;">

                    <h2 style="color: #38462F;">
                        Bonjour %s,
                    </h2>

                        <p>
                            Nous avons le plaisir de vous confirmer votre réservation
                            à la <strong>Fermette des Trois Tilleuls</strong>.
                        </p>

                        <div style="background-color: #F6F1E7; border-left: 4px solid #C17A4D;
                                    padding: 16px; border-radius: 4px; margin: 24px 0;">
                            <p style="margin: 0 0 8px 0;">
                                <strong>🏠 Gîte :</strong> %s
                            </p>
                            <p style="margin: 0 0 8px 0;">
                                <strong>📅 Arrivée :</strong> %s
                            </p>
                            <p style="margin: 0;">
                                <strong>📅 Départ :</strong> %s
                            </p>
                        </div>

                        <p>
                            Nous vous contacterons prochainement pour les détails
                            et les modalités de paiement.
                        </p>

                        <p>À très bientôt !</p>

                        <p style="color: #6B6052; font-size: 0.85rem; margin-top: 32px;
                                  border-top: 1px solid #EFE7D8; padding-top: 16px;">
                            Fermette des Trois Tilleuls — Bezinghem, Pas-de-Calais<br/>
                            <a href="https://fermette.y-rog.com"
                               style="color: #C17A4D;">fermette.y-rog.com</a>
                        </p>
                    </div>

                </body>
                </html>
                """.formatted(firstName, giteName,
                checkIn.format(FR_DATE), checkOut.format(FR_DATE));
    }

    /**
     * Construit le corps HTML de l'email de refus.
     */
    private String buildRejectionEmail(String firstName, String giteName,
                                       LocalDate checkIn, LocalDate checkOut) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; color: #3A332A;
                         max-width: 600px; margin: 0 auto;">

                <div style="background-color: #38462F; padding: 24px;
                            border-radius: 8px 8px 0 0; text-align: center;">
                    <img src="https://fermette.y-rog.com/img/logo-fermette.png"
                         alt="Fermette des Trois Tilleuls"
                         style="height: 80px; width: auto;"/>
                </div>

                <div style="background-color: white; padding: 32px;
                            border: 1px solid #EFE7D8; border-top: none;">

                    <h2 style="color: #38462F;">
                        Bonjour %s,
                    </h2>

                        <p>
                            Nous vous remercions de l'intérêt que vous portez
                            à la <strong>Fermette des Trois Tilleuls</strong>.
                        </p>

                        <div style="background-color: #F6F1E7; border-left: 4px solid #C17A4D;
                                    padding: 16px; border-radius: 4px; margin: 24px 0;">
                            <p style="margin: 0 0 8px 0;">
                                <strong>🏠 Gîte :</strong> %s
                            </p>
                            <p style="margin: 0 0 8px 0;">
                                <strong>📅 Arrivée :</strong> %s
                            </p>
                            <p style="margin: 0;">
                                <strong>📅 Départ :</strong> %s
                            </p>
                        </div>

                        <p>
                            Malheureusement, nous ne sommes pas en mesure
                            de donner suite à votre demande de réservation
                            pour le créneau souhaité.
                        </p>

                        <p>
                            N'hésitez pas à nous contacter pour trouver
                            une autre date qui vous conviendrait.
                        </p>

                        <p>Cordialement,</p>

                        <p style="color: #6B6052; font-size: 0.85rem; margin-top: 32px;
                                  border-top: 1px solid #EFE7D8; padding-top: 16px;">
                            Fermette des Trois Tilleuls — Bezinghem, Pas-de-Calais<br/>
                            <a href="https://fermette.y-rog.com"
                               style="color: #C17A4D;">fermette.y-rog.com</a>
                        </p>
                    </div>

                </body>
                </html>
                """.formatted(firstName, giteName,
                checkIn.format(FR_DATE), checkOut.format(FR_DATE));
    }

    /**
     * Envoie un message de contact à la ferme.
     *
     * @param nom     nom de l'expéditeur
     * @param email   email de l'expéditeur
     * @param message contenu du message
     */
    @Override
    public void sendContactMessage(String nom, String email, String message) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(from);
            helper.setReplyTo(email);
            helper.setSubject("Nouveau message de contact — " + nom);
            helper.setText("""
                    <html>
                    <body style="font-family: Arial, sans-serif; color: #3A332A;
                                 max-width: 600px; margin: 0 auto;">
                        <div style="background-color: #38462F; padding: 24px;
                                    border-radius: 8px 8px 0 0;">
                            <h1 style="color: #F6F1E7; margin: 0; font-size: 1.4rem;">
                                🌿 Fermette des Trois Tilleuls
                            </h1>
                        </div>
                        <div style="background-color: white; padding: 32px;
                                    border: 1px solid #EFE7D8; border-top: none;">
                            <h2 style="color: #38462F;">Nouveau message de contact</h2>
                            <p><strong>Nom :</strong> %s</p>
                            <p><strong>Email :</strong> %s</p>
                            <p><strong>Message :</strong></p>
                            <p style="background-color: #F6F1E7; padding: 16px;
                                      border-radius: 4px;">%s</p>
                        </div>
                    </body>
                    </html>
                    """.formatted(nom, email, message), true);
            mailSender.send(msg);
            log.info("Message de contact envoyé depuis {}", email);
        } catch (MessagingException e) {
            log.error("Erreur envoi message de contact", e);
        }
    }
}