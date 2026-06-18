package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
import com.yrog.fermettetroistilleuls.entity.Gite;
import com.yrog.fermettetroistilleuls.entity.GiteBooking;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.GiteBookingRepository;
import com.yrog.fermettetroistilleuls.repository.GiteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires du BookingService.
 * On utilise Mockito pour simuler les repositories
 * et tester la logique métier de manière isolée,
 * sans connexion à la base de données.
 */
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private GiteRepository giteRepository;

    @Mock
    private GiteBookingRepository giteBookingRepository;

    @InjectMocks
    private BookingService bookingService;

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
     * Construit un formulaire de réservation de test
     * réutilisable pour éviter la duplication dans les tests.
     */
    private GiteBookingForm buildBookingForm() {
        GiteBookingForm form = new GiteBookingForm();
        form.setGiteId(1L);
        form.setFirstName("Jean");
        form.setLastName("Dupont");
        form.setEmail("jean.dupont@email.com");
        form.setPhone("06 12 34 56 78");
        form.setCheckIn(LocalDate.of(2026, 8, 1));
        form.setCheckOut(LocalDate.of(2026, 8, 7));
        form.setNbGuests(2);
        form.setMessage("Séjour en famille");
        return form;
    }

    /**
     * Vérifie que saveGiteBooking() sauvegarde bien
     * la réservation en BDD quand le gîte existe.
     */
    @Test
    void saveGiteBooking_shouldSaveBooking_whenGiteExists() {
        // Arrange
        when(giteRepository.findById(1L))
                .thenReturn(Optional.of(buildGite()));

        // Act
        bookingService.saveGiteBooking(buildBookingForm());

        // Assert
        verify(giteBookingRepository, times(1))
                .save(any(GiteBooking.class));
    }

    /**
     * Vérifie que saveGiteBooking() lance une
     * ResourceNotFoundException quand le gîte
     * n'existe pas en BDD.
     */
    @Test
    void saveGiteBooking_shouldThrowException_whenGiteNotFound() {
        // Arrange
        when(giteRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                bookingService.saveGiteBooking(buildBookingForm()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
