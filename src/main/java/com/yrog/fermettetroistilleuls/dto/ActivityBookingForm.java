package com.yrog.fermettetroistilleuls.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Formulaire de demande de réservation pour une activité.
 * Rempli par le client via le formulaire HTML.
 * Le statut et la date de création sont gérés côté serveur.
 */
@Getter
@Setter
public class ActivityBookingForm {

    /** Id de l'activité — passé en champ caché dans le formulaire. */
    private Long activityId;

    @NotBlank(message = "Le nom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le prénom est obligatoire")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    private String email;

    private String phone;

    @Min(value = 1, message = "Minimum 1 participant")
    private int nbParticipants;

    private String message;
}
