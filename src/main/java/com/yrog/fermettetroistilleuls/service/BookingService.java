package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.ActivityBookingForm;
import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
import com.yrog.fermettetroistilleuls.entity.*;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.ActivityBookingRepository;
import com.yrog.fermettetroistilleuls.repository.ActivityRepository;
import com.yrog.fermettetroistilleuls.repository.GiteBookingRepository;

import com.yrog.fermettetroistilleuls.repository.GiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    public BookingService(GiteBookingRepository giteBookingRepository, GiteRepository giteRepository, ActivityRepository activityRepository, ActivityBookingRepository activityBookingRepository) {
        this.giteBookingRepository = giteBookingRepository;
        this.giteRepository = giteRepository;
        this.activityRepository = activityRepository;
        this.activityBookingRepository = activityBookingRepository;
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

}
