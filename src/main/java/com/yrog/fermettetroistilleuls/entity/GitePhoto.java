package com.yrog.fermettetroistilleuls.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entité représentant une photo associée à un gîte.
 * Un gîte peut avoir plusieurs photos, affichées
 * dans l'ordre défini par displayOrder.
 */
@Entity
@Table(name = "gite_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Le gîte auquel appartient cette photo.
     */
    @ManyToOne
    @JoinColumn(name = "gite_id", nullable = false)
    private Gite gite;

    /**
     * URL complète de la photo (stockage externe).
     */
    @NotBlank(message = "L'URL de la photo est obligatoire")
    @Column(nullable = false, length = 500)
    private String url;

    /**
     * Ordre d'affichage de la photo dans le carousel/galerie.
     */
    @Column(nullable = false)
    private int displayOrder;
}