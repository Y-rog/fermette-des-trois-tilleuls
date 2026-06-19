package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.ActivityBookingForm;
import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
import com.yrog.fermettetroistilleuls.entity.*;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service gérant la logique métier des réservations.
 * Centralise les réservations de gîtes et d'activités.
 */
@Service
public class BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private final GiteBookingRepository giteBookingRepository;
    private final GiteRepository giteRepository;
    private final ActivityRepository activityRepository;
    private final ActivityBookingRepository activityBookingRepository;
    private final GiteAvailabilityRepository giteAvailabilityRepository;

    public BookingService(GiteBookingRepository giteBookingRepository, GiteRepository giteRepository, ActivityRepository activityRepository, ActivityBookingRepository activityBookingRepository, GiteAvailabilityRepository giteAvailabilityRepository) {
        this.giteBookingRepository = giteBookingRepository;
        this.giteRepository = giteRepository;
        this.activityRepository = activityRepository;
        this.activityBookingRepository = activityBookingRepository;
        this.giteAvailabilityRepository = giteAvailabilityRepository;
    }

    /**
     * Crée une nouvelle demande de réservation pour un gîte.
     * Le statut est automatiquement défini à PENDING.
     * La ferme devra valider ou refuser depuis l'espace admin.
     *
     * @param form formulaire rempli par le client
     * @throws ResourceNotFoundException si le gîte n'existe pas
     */
    @Transactional
    public void saveGiteBooking(GiteBookingForm form) {

        Gite gite = giteRepository.findById(form.getGiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Gîte introuvable"));

        // Vérifier que checkOut est après checkIn
        if (!form.getCheckOut().isAfter(form.getCheckIn())) {
            throw new IllegalStateException(
                    "La date de départ doit être après la date d'arrivée");
        }

        // Vérification des disponibilités avant de sauvegarder
        checkAvailability(form.getGiteId(), form.getCheckIn(), form.getCheckOut());

        GiteBooking booking = GiteBooking.builder()
                .gite(gite)
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .email(form.getEmail())
                .phone(form.getPhone())
                .checkIn(form.getCheckIn())
                .checkOut(form.getCheckOut())
                .nbGuests(form.getNbGuests())
                .message(form.getMessage())
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        giteBookingRepository.save(booking);
        log.info("Nouvelle réservation gîte créée pour {}", form.getEmail());
    }

    /**
     * Crée une nouvelle demande de réservation pour une activité.
     * Le statut est automatiquement défini à PENDING.
     * La ferme devra valider ou refuser depuis l'espace admin.
     *
     * @param form formulaire rempli par le client
     * @throws ResourceNotFoundException si l'activité n'existe pas
     */
    @Transactional
    public void saveActivityBooking(ActivityBookingForm form) {

        Activity activity = activityRepository.findById(form.getActivityId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Activité introuvable"));

        ActivityBooking booking = ActivityBooking.builder()
                .activity(activity)
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .email(form.getEmail())
                .phone(form.getPhone())
                .nbGuests(form.getNbGuests())
                .message(form.getMessage())
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        activityBookingRepository.save(booking);
        log.info("Nouvelle réservation activité pour {}", form.getEmail());
    }

    /**
     * Vérifie que toutes les dates entre checkIn et checkOut
     * sont disponibles pour ce gîte.
     *
     * @param giteId   identifiant du gîte
     * @param checkIn  date d'arrivée
     * @param checkOut date de départ
     * @throws IllegalStateException si une date n'est pas disponible
     */
    private void checkAvailability(Long giteId, LocalDate checkIn, LocalDate checkOut) {

        LocalDate current = checkIn;

        while (!current.isAfter(checkOut)) {

            GiteAvailability availability = giteAvailabilityRepository
                    .findByGiteIdAndDate(giteId, current)
                    .orElse(null);

            boolean isAvailable = availability != null
                    && availability.getStatus() == AvailabilityStatus.AVAILABLE;

            if (!isAvailable) {
                throw new IllegalStateException(
                        "La date " + current + " n'est pas disponible pour ce gîte");
            }

            current = current.plusDays(1);
        }
    }



}
