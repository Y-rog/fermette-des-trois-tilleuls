package com.yrog.fermedufay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entité représentant la disponibilité d'un gîte
 * pour une date donnée.
 * Une ligne par jour et par gîte.
 */
@Entity
@Table(name = "gite_availabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiteAvailability {

    /**
     * Identifiant unique, auto-généré par PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Le gîte concerné par cette disponibilité.
     */
    @ManyToOne
    @JoinColumn(name = "gite_id", nullable = false)
    private Gite gite;

    /**
     * La date concernée.
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Statut de disponibilité pour cette date.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status;
}
