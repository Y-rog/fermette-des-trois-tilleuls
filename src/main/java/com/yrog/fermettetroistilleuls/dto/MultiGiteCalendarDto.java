package com.yrog.fermettetroistilleuls.dto;

import com.yrog.fermettetroistilleuls.entity.AvailabilityStatus;

import java.util.List;
import java.util.Map;

/**
 * DTO pour l'affichage du calendrier multi-gîtes
 * sur le dashboard admin. Permet de visualiser
 * l'occupation de tous les gîtes en un coup d'œil.
 */
public record MultiGiteCalendarDto(
        int year,
        int month,
        String monthName,
        int daysInMonth,
        List<String> dayLetters,
        List<GiteCalendarRowDto> rows
) {
    public record GiteCalendarRowDto(
            Long giteId,
            String giteName,
            Map<Integer, AvailabilityStatus> availabilities
    ) { }
}