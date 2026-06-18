package com.yrog.fermettetroistilleuls.dto;

import com.yrog.fermettetroistilleuls.entity.AvailabilityStatus;
import java.time.LocalDate;
import java.util.Map;

/**
 * DTO pour l'affichage d'un calendrier mensuel
 * de disponibilités d'un gîte.
 */
public record GiteCalendarDto(
        int year,
        int month,
        String monthName,
        int firstDayOfWeek,  // 1=lundi, 7=dimanche
        int daysInMonth,
        Map<Integer, AvailabilityStatus> availabilities
) { }
