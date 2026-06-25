package com.yrog.fermettetroistilleuls.integration;

import com.yrog.fermettetroistilleuls.BaseIntegrationTest;
import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
import com.yrog.fermettetroistilleuls.entity.*;
import com.yrog.fermettetroistilleuls.repository.*;
import com.yrog.fermettetroistilleuls.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests d'intégration du BookingService.
 * Utilise un vrai PostgreSQL via Testcontainers.
 * Vérifie le flux complet de réservation en BDD.
 */
@Transactional
class BookingServiceIT extends BaseIntegrationTest {

    @Autowired private BookingService bookingService;
    @Autowired private GiteRepository giteRepository;
    @Autowired private GiteAvailabilityRepository availabilityRepository;
    @Autowired private GiteBookingRepository bookingRepository;

    private Gite gite;
    private LocalDate checkIn;
    private LocalDate checkOut;

    /**
     * Crée un gîte et des disponibilités AVAILABLE
     * avant chaque test.
     */
    @BeforeEach
    void setUp() {
        gite = giteRepository.save(Gite.builder()
                .name("Gîte de la Hétraie")
                .location("Bezinghem")
                .description("Un beau gîte")
                .capacity(6)
                .bedrooms(3)
                .active(true)
                .build());

        checkIn  = LocalDate.now().plusDays(10);
        checkOut = LocalDate.now().plusDays(15);

        LocalDate current = checkIn;
        while (!current.isAfter(checkOut)) {
            availabilityRepository.save(
                    GiteAvailability.builder()
                            .gite(gite)
                            .date(current)
                            .status(AvailabilityStatus.AVAILABLE)
                            .build());
            current = current.plusDays(1);
        }
    }

    /**
     * Construit un formulaire de réservation valide.
     */
    private GiteBookingForm buildForm() {
        GiteBookingForm form = new GiteBookingForm();
        form.setGiteId(gite.getId());
        form.setFirstName("Marie");
        form.setLastName("Dupont");
        form.setEmail("marie@test.fr");
        form.setPhone("0612345678");
        form.setCheckIn(checkIn);
        form.setCheckOut(checkOut);
        form.setNbGuests(4);
        return form;
    }

    @Test
    @DisplayName("Doit sauvegarder une réservation valide en BDD")
    void should_save_valid_booking_in_database() {
        bookingService.saveGiteBooking(buildForm());

        assertThat(bookingRepository.count()).isEqualTo(1);
        var booking = bookingRepository.findAll().get(0);
        assertThat(booking.getEmail()).isEqualTo("marie@test.fr");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getGite().getId()).isEqualTo(gite.getId());
        assertThat(booking.getCheckIn()).isEqualTo(checkIn);
        assertThat(booking.getCheckOut()).isEqualTo(checkOut);
    }

    @Test
    @DisplayName("Doit refuser si une date est UNAVAILABLE")
    void should_reject_when_date_unavailable() {
        availabilityRepository.findAll().stream()
                .filter(a -> a.getDate().equals(checkIn.plusDays(2)))
                .findFirst()
                .ifPresent(a -> {
                    a.setStatus(AvailabilityStatus.UNAVAILABLE);
                    availabilityRepository.save(a);
                });

        assertThatThrownBy(() ->
                bookingService.saveGiteBooking(buildForm()))
                .isInstanceOf(IllegalStateException.class);

        assertThat(bookingRepository.count()).isZero();
    }

    @Test
    @DisplayName("Doit refuser si une date est RESERVED")
    void should_reject_when_date_reserved() {
        availabilityRepository.findAll().stream()
                .filter(a -> a.getDate().equals(checkIn))
                .findFirst()
                .ifPresent(a -> {
                    a.setStatus(AvailabilityStatus.RESERVED);
                    availabilityRepository.save(a);
                });

        assertThatThrownBy(() ->
                bookingService.saveGiteBooking(buildForm()))
                .isInstanceOf(IllegalStateException.class);

        assertThat(bookingRepository.count()).isZero();
    }

    @Test
    @DisplayName("Doit refuser si le gîte n'existe pas")
    void should_reject_when_gite_not_found() {
        GiteBookingForm form = buildForm();
        form.setGiteId(999L);

        assertThatThrownBy(() ->
                bookingService.saveGiteBooking(form))
                .isInstanceOf(Exception.class);

        assertThat(bookingRepository.count()).isZero();
    }

    @Test
    @DisplayName("La réservation doit avoir le statut PENDING")
    void booking_should_have_pending_status() {
        bookingService.saveGiteBooking(buildForm());

        var booking = bookingRepository.findAll().get(0);
        assertThat(booking.getStatus())
                .isEqualTo(BookingStatus.PENDING);
    }

    @Test
    @DisplayName("Doit associer la réservation au bon gîte")
    void booking_should_be_linked_to_correct_gite() {
        bookingService.saveGiteBooking(buildForm());

        var booking = bookingRepository.findAll().get(0);
        assertThat(booking.getGite().getName())
                .isEqualTo("Gîte de la Hétraie");
    }
}