package com.yrog.fermedufay.dto;

/**
 * DTO en lecture seule pour l'affichage d'un gîte.
 * Utilisé par les controllers pour passer les données
 * aux templates Thymeleaf sans exposer l'entité JPA.
 */
public record GiteDto(
        Long id,
        String name,
        String location,
        String description,
        int capacity,
        int bedrooms,
        String photoUrl
) { }
