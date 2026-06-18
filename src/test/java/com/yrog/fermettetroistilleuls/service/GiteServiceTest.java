package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.GiteDto;
import com.yrog.fermettetroistilleuls.entity.Gite;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.GiteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du GiteService.
 * On utilise Mockito pour simuler le repository
 * et tester la logique métier de manière isolée.
 */
@ExtendWith(MockitoExtension.class)
public class GiteServiceTest {

    @Mock
    private GiteRepository giteRepository;

    @InjectMocks
    private GiteService giteService;

    /**
     * Construit un gîte de test réutilisable
     * pour éviter la duplication dans les tests.
     */
    private Gite buildGite() {
        return Gite.builder()
                .id(1L)
                .name("Gîte du Fay")
                .location("Le Fay")
                .description("Belle maison")
                .capacity(6)
                .bedrooms(3)
                .photoUrl(null)
                .active(true)
                .build();
    }

    /**
     * Vérifie que findAll() retourne bien
     * une liste de GiteDtos non vide.
     */
    @Test
    void findAll_shouldReturnListOfGiteDtos() {
        // Arrange
        when(giteRepository.findAll()).thenReturn(List.of(buildGite()));

        // Act
        List<GiteDto> result = giteService.findAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Gîte du Fay");
        assertThat(result.get(0).capacity()).isEqualTo(6);
    }

    /**
     * Vérifie que findById() retourne bien
     * un GiteDto quand l'id existe en BDD.
     */
    @Test
    void findById_shouldReturnGiteDto_whenIdExists() {
        // Arrange
        when(giteRepository.findById(1L)).thenReturn(Optional.of(buildGite()));

        // Act
        GiteDto result = giteService.findById(1L);

        // Assert
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Gîte du Fay");
    }

    /**
     * Vérifie que findById() lance une
     * ResourceNotFoundException quand l'id
     * n'existe pas en BDD.
     */
    @Test
    void findById_shouldThrowException_whenIdNotFound() {
        // Arrange
        when(giteRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> giteService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
