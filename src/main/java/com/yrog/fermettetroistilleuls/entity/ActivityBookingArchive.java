package com.yrog.fermettetroistilleuls.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entité représentant une réservation d'activité archivée
 * (activité terminée). Indépendante de l'entité Activity pour
 * que l'archive reste valide même si l'activité est supprimée.
 */
@Entity
@Table(name = "activity_booking_archives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityBookingArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom de l'activité au moment de l'archivage (pas de relation FK). */
    @Column(nullable = false, length = 100)
    private String activityName;

    /** Date de l'activité au moment de l'archivage. */
    @Column(nullable = false)
    private LocalDate activityDate;

    /** Heure de l'activité au moment de l'archivage. */
    @Column(nullable = false)
    private LocalTime activityTime;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

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