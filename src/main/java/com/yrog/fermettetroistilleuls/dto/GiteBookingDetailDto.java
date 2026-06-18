package com.yrog.fermettetroistilleuls.dto;

import com.yrog.fermettetroistilleuls.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO en lecture seule pour l'affichage détaillé
 * d'une réservation de gîte dans l'espace admin.
 * Contient toutes les informations nécessaires
 * pour accepter ou refuser la demande.
 */
public record GiteBookingDetailDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate checkIn,
        LocalDate checkOut,
        int nbGuests,
        String message,
        BookingStatus status,
        LocalDateTime createdAt,
        String giteName
) { }