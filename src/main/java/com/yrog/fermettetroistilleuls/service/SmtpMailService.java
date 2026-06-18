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

/**
 * Implémentation SMTP du MailService pour le profil prod.
 * Envoie de vrais emails via le serveur SMTP Hostinger.
 */
@Service
@Profile("prod")
public class SmtpMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpMailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    public SmtpMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un email de confirmation de réservation.
     *
     * @param email     email du client
     * @param firstName prénom du client
     */
    @Override
    public void sendBookingConfirmation(String email, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Votre réservation est confirmée — Fermette des Trois Tilleuls");
            helper.setText(buildConfirmationEmail(firstName), true);

            mailSender.send(message);
            log.info("Email de confirmation envoyé à {}", email);

        } catch (MessagingException e) {
            log.error("Erreur envoi email confirmation à {}", email, e);
        }
    }

    /**
     * Envoie un email de refus de réservation.
     *
     * @param email     email du client
     * @param firstName prénom du client
     */
    @Override
    public void sendBookingRejection(String email, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("Votre demande de réservation — Fermette des Trois Tilleuls");
            helper.setText(buildRejectionEmail(firstName), true);

            mailSender.send(message);
            log.info("Email de refus envoyé à {}", email);

        } catch (MessagingException e) {
            log.error("Erreur envoi email refus à {}", email, e);
        }
    }

    /**
     * Construit le corps HTML de l'email de confirmation.
     */
    private String buildConfirmationEmail(String firstName) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; color: #3A332A;">
                    <h2 style="color: #4A5D3F;">
                        Bonjour %s,
                    </h2>
                    <p>
                        Nous avons le plaisir de vous confirmer votre réservation
                        à la <strong>Fermette des Trois Tilleuls</strong>.
                    </p>
                    <p>
                        Nous vous contacterons prochainement pour les détails
                        et les modalités de paiement.
                    </p>
                    <p>
                        À très bientôt !
                    </p>
                    <hr/>
                    <p style="color: #6B6052; font-size: 0.85rem;">
                        Fermette des Trois Tilleuls
                    </p>
                </body>
                </html>
                """.formatted(firstName);
    }

    /**
     * Construit le corps HTML de l'email de refus.
     */
    private String buildRejectionEmail(String firstName) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; color: #3A332A;">
                    <h2 style="color: #4A5D3F;">
                        Bonjour %s,
                    </h2>
                    <p>
                        Nous vous remercions de l'intérêt que vous portez
                        à la <strong>Fermette des Trois Tilleuls</strong>.
                    </p>
                    <p>
                        Malheureusement, nous ne sommes pas en mesure
                        de donner suite à votre demande de réservation
                        pour le créneau souhaité.
                    </p>
                    <p>
                        N'hésitez pas à nous contacter pour trouver
                        une autre date qui vous conviendrait.
                    </p>
                    <p>
                        Cordialement,
                    </p>
                    <hr/>
                    <p style="color: #6B6052; font-size: 0.85rem;">
                        Fermette des Trois Tilleuls
                    </p>
                </body>
                </html>
                """.formatted(firstName);
    }

    /**
     * Envoie un message de contact à la ferme.
     */
    @Override
    public void sendContactMessage(String nom, String email, String message) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(from); // envoyé à la ferme elle-même
            helper.setReplyTo(email); // répondre au client
            helper.setSubject("Nouveau message de contact — " + nom);
            helper.setText("""
                <html>
                <body style="font-family: Arial, sans-serif; color: #3A332A;">
                    <h2 style="color: #4A5D3F;">Nouveau message de contact</h2>
                    <p><strong>Nom :</strong> %s</p>
                    <p><strong>Email :</strong> %s</p>
                    <p><strong>Message :</strong></p>
                    <p>%s</p>
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
