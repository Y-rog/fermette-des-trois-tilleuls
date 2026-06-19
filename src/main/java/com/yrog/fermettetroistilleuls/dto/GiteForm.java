package com.yrog.fermettetroistilleuls.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Formulaire de création/modification d'un gîte.
 * Utilisé dans l'espace admin.
 */
@Getter
@Setter
public class GiteForm {

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "La localisation est obligatoire")
    private String location;

    private String description;

    @Min(value = 1, message = "La capacité doit être d'au moins 1")
    private int capacity;

    @Min(value = 1, message = "Le nombre de chambres doit être d'au moins 1")
    private int bedrooms;

    private String photoUrl;
}