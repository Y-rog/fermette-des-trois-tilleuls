package com.yrog.fermettetroistilleuls.dto;

import com.yrog.fermettetroistilleuls.entity.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO en lecture seule pour l'affichage détaillé
 * d'une réservation d'une activité dans l'espace admin.
 * Contient toutes les informations nécessaires
 * pour accepter ou refuser la demande.
 */
public record ActivityBookingDetailDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        int nbGuests,
        String message,
        BookingStatus status,
        LocalDateTime createdAt,
        String activityName,
        LocalDate activityDate,
        LocalTime activityTime
) { }
