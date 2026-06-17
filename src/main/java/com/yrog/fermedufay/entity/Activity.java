package com.yrog.fermedufay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entité représentant une activité proposée
 * par la fermette (balade en alpagas, atelier...).
 * Conçue pour être extensible à d'autres activités.
 */
@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    /**
     * Identifiant unique, auto-généré par PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de l'activité — ex: "Balade en alpagas".
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Description de l'activité.
     */
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    private String description;

    /**
     * Date de l'activité.
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Heure de l'activité.
     */
    @Column(nullable = false)
    private LocalTime time;

    /**
     * Indique si l'activité est active et visible sur le site.
     * Permet de désactiver une activité sans la supprimer.
     */
    @Column(nullable = false)
    private boolean active;
}