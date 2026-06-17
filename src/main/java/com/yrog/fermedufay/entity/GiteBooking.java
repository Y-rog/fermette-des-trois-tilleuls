package com.yrog.fermedufay.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité représentant une demande de réservation
 * pour un gîte de la fermette.
 */
@Entity
@Table(name = "gite_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiteBooking {

    /**
     * Identifiant unique, auto-généré par PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Le gîte concerné par cette réservation.
     */
    @ManyToOne
    @JoinColumn(name = "gite_id", nullable = false)
    private Gite gite;

    /**
     * Nom du client — obligatoire.
     */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String firstName;

    /**
     * Prénom du client — obligatoire.
     */
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String lastName;

    /**
     * Email du client — obligatoire et format valide.
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * Numéro de téléphone du client.
     */
    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    @Column(length = 20)
    private String phone;

    /**
     * Date d'arrivée — obligatoire.
     */
    @Column(nullable = false)
    private LocalDate checkIn;

    /**
     * Date de départ — obligatoire.
     */
    @Column(nullable = false)
    private LocalDate checkOut;

    /**
     * Nombre de personnes — minimum 1.
     */
    @Min(value = 1, message = "Le nombre de personnes doit être d'au moins 1")
    @Column(nullable = false)
    private int nbGuests;

    /**
     * Message optionnel du client.
     */
    @Size(max = 500, message = "Le message ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    private String message;

    /**
     * Statut de la demande de réservation.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    /**
     * Date de création de la demande.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
