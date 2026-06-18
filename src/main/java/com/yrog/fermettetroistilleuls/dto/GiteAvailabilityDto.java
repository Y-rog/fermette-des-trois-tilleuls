package com.yrog.fermettetroistilleuls.dto;

import com.yrog.fermettetroistilleuls.entity.AvailabilityStatus;
import java.time.LocalDate;

/**
 * DTO en lecture seule pour l'affichage
 * d'une disponibilité de gîte.
 */
public record GiteAvailabilityDto(
        LocalDate date,
        AvailabilityStatus status
) { }
