package com.yrog.fermettetroistilleuls.dto;

/**
 * DTO en lecture seule pour l'affichage d'une photo de gîte.
 */
public record GitePhotoDto(
        Long id,
        String url
) { }
