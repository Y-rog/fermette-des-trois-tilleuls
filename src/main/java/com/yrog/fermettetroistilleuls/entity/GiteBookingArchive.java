package com.yrog.fermettetroistilleuls.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une réservation de gîte archivée
 * (séjour terminé). Indépendante de l'entité Gite pour
 * que l'archive reste valide même si le gîte est supprimé.
 */
@Entity
@Table(name = "gite_booking_archives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiteBookingArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom du gîte au moment de l'archivage (pas de relation FK). */
    @Column(nullable = false, length = 100)
    private String giteName;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private int nbGuests;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** Date à laquelle l'archivage a eu lieu. */
    @Column(nullable = false)
    private LocalDateTime archivedAt;
}
