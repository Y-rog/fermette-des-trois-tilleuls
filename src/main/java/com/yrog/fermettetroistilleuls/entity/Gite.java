package com.yrog.fermettetroistilleuls.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entité représentant un gîte de la fermette.
 * Un gîte peut avoir plusieurs disponibilités
 * et plusieurs demandes de réservation.
 */
@Entity
@Table(name = "gites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gite {

    /**
     * Identifiant unique, auto-généré par PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom du gîte — obligatoire, 100 caractères max.
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Description du gîte.
     */
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    private String description;

    /**
     * Localisation du gîte.
     */
    @Size(max = 200, message = "La localisation ne peut pas dépasser 200 caractères")
    @Column(length = 200)
    private String location;

    /**
     * Capacité maximale en nombre de personnes.
     */
    @Min(value = 1, message = "La capacité doit être d'au moins 1 personne")
    @Column(nullable = false)
    private int capacity;

    /**
     * Nombre de chambres.
     */
    @Min(value = 1, message = "Le nombre de chambres doit être d'au moins 1")
    @Column(nullable = false)
    private int bedrooms;

    /**
     * Chemin vers la photo principale du gîte.
     * Fichier stocké dans static/img/
     */
    @Size(max = 300, message = "L'URL de la photo ne peut pas dépasser 300 caractères")
    @Column(length = 300)
    private String photoUrl;

    /**
     * Indique si le gîte est actif et visible sur le site.
     * Permet de désactiver un gîte sans le supprimer.
     */
    @Column(nullable = false)
    private boolean active;
}
