package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.GiteBookingForm;
import com.yrog.fermettetroistilleuls.entity.BookingStatus;
import com.yrog.fermettetroistilleuls.entity.Gite;
import com.yrog.fermettetroistilleuls.entity.GiteBooking;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
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

    public BookingService(GiteBookingRepository giteBookingRepository, GiteRepository giteRepository) {
        this.giteBookingRepository = giteBookingRepository;
        this.giteRepository = giteRepository;
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

}
