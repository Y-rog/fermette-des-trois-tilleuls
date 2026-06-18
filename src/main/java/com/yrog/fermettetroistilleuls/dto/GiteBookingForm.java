package com.yrog.fermettetroistilleuls.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Formulaire de demande de réservation pour un gîte.
 * Rempli par le client via le formulaire HTML.
 * Le statut et la date de création sont gérés côté serveur.
 */
@Getter
@Setter
public class GiteBookingForm {

    /** Id du gîte — passé en champ caché dans le formulaire. */
    private Long giteId;

    @NotBlank(message = "Le nom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le prénom est obligatoire")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    private String email;

    private String phone;

    @NotNull(message = "La date d'arrivée est obligatoire")
    private LocalDate checkIn;

    @NotNull(message = "La date de départ est obligatoire")
    private LocalDate checkOut;

    @Min(value = 1, message = "Minimum 1 personne")
    private int nbGuests;

    private String message;
}
