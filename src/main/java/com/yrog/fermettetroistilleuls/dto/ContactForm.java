package com.yrog.fermettetroistilleuls.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Formulaire de contact.
 */
@Getter
@Setter
public class ContactForm {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    private String email;

    @NotBlank(message = "Le message est obligatoire")
    private String message;
}
