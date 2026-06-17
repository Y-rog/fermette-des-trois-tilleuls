package com.yrog.fermedufay.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO en lecture seule pour l'affichage d'une activité.
 * Utilisé par les controllers pour passer les données
 * aux templates Thymeleaf sans exposer l'entité JPA.
 */
public record ActivityDto(
        Long id,
        String name,
        String description,
        LocalDate date,
        LocalTime time
) { }
