package com.yrog.fermettetroistilleuls.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant les informations
 * de contact de la fermette.
 * Une seule ligne en BDD (id = 1).
 */
@Entity
@Table(name = "fermette_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FermetteInfo {

    @Id
    private Long id;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String horaires;

    @Column
    private String horairesBoutique;
}
