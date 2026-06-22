package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.entity.AvailabilityStatus;
import com.yrog.fermettetroistilleuls.entity.Gite;
import com.yrog.fermettetroistilleuls.entity.GiteAvailability;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.GiteAvailabilityRepository;
import com.yrog.fermettetroistilleuls.repository.GiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Service gérant les disponibilités des gîtes.
 * Permet à l'admin de gérer le calendrier de réservation.
 */
@Service
public class AvailabilityService {

    private static final Logger log = LoggerFactory.getLogger(AvailabilityService.class);

    private final GiteAvailabilityRepository giteAvailabilityRepository;
    private final GiteRepository giteRepository;

    public AvailabilityService(GiteAvailabilityRepository giteAvailabilityRepository,
                               GiteRepository giteRepository) {
        this.giteAvailabilityRepository = giteAvailabilityRepository;
        this.giteRepository = giteRepository;
    }

    /**
     * Bascule le statut de disponibilité d'un gîte pour une date donnée.
     * Si aucune disponibilité n'existe pour cette date, elle est créée
     * avec le statut AVAILABLE. Sinon, le statut bascule entre
     * AVAILABLE et UNAVAILABLE.
     *
     * @param giteId identifiant du gîte
     * @param date   date à modifier
     * @throws ResourceNotFoundException si le gîte n'existe pas
     */
    @Transactional
    public void toggleAvailability(Long giteId, LocalDate date) {

        Optional<GiteAvailability> existing =
                giteAvailabilityRepository.findByGiteIdAndDate(giteId, date);

        if (existing.isPresent()) {
            GiteAvailability availability = existing.get();

            // Sécurité : on ne touche jamais à une date RESERVED
            if (availability.getStatus() == AvailabilityStatus.RESERVED) {
                throw new IllegalStateException(
                        "Impossible de modifier une date réservée : " + date);
            }

            if (availability.getStatus() == AvailabilityStatus.AVAILABLE) {
                availability.setStatus(AvailabilityStatus.UNAVAILABLE);
            } else {
                availability.setStatus(AvailabilityStatus.AVAILABLE);
            }

            giteAvailabilityRepository.save(availability);

        } else {
            Gite gite = giteRepository.findById(giteId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Gîte introuvable"));

            GiteAvailability newAvailability = GiteAvailability.builder()
                    .gite(gite)
                    .date(date)
                    .status(AvailabilityStatus.AVAILABLE)
                    .build();

            giteAvailabilityRepository.save(newAvailability);
        }

        log.info("Disponibilité gîte {} pour {} mise à jour", giteId, date);
    }

    /**
     * Marque toutes les dates du mois comme AVAILABLE.
     *
     * @param giteId identifiant du gîte
     * @param year   année
     * @param month  mois (1-12)
     */
    @Transactional
    public void setMonthAvailable(Long giteId, int year, int month) {
        log.info("Mise en disponible du mois {}/{} pour gîte id={}", month, year, giteId);
        Gite gite = giteRepository.findById(giteId)
                .orElseThrow(() -> new ResourceNotFoundException("Gîte introuvable"));

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDate current = firstDay;
        while (!current.isAfter(lastDay)) {
            final LocalDate date = current;
            GiteAvailability availability = giteAvailabilityRepository
                    .findByGiteIdAndDate(giteId, date)
                    .orElse(null);

            if (availability != null) {
                // Ne pas toucher aux dates RESERVED
                if (availability.getStatus() != AvailabilityStatus.RESERVED) {
                    availability.setStatus(AvailabilityStatus.AVAILABLE);
                    giteAvailabilityRepository.save(availability);
                }
            } else {
                giteAvailabilityRepository.save(
                        GiteAvailability.builder()
                                .gite(gite)
                                .date(date)
                                .status(AvailabilityStatus.AVAILABLE)
                                .build()
                );
            }
            current = current.plusDays(1);
        }
    }

    /**
     * Marque toutes les dates du mois comme UNAVAILABLE.
     * Les dates RESERVED ne sont pas modifiées.
     *
     * @param giteId identifiant du gîte
     * @param year   année
     * @param month  mois (1-12)
     */
    @Transactional
    public void setMonthUnavailable(Long giteId, int year, int month) {
        log.info("Blocage du mois {}/{} pour gîte id={}", month, year, giteId);
        Gite gite = giteRepository.findById(giteId)
                .orElseThrow(() -> new ResourceNotFoundException("Gîte introuvable"));

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDate current = firstDay;
        while (!current.isAfter(lastDay)) {
            final LocalDate date = current;
            GiteAvailability availability = giteAvailabilityRepository
                    .findByGiteIdAndDate(giteId, date)
                    .orElse(null);

            if (availability != null) {
                // Ne pas toucher aux dates RESERVED
                if (availability.getStatus() != AvailabilityStatus.RESERVED) {
                    availability.setStatus(AvailabilityStatus.UNAVAILABLE);
                    giteAvailabilityRepository.save(availability);
                }
            } else {
                giteAvailabilityRepository.save(
                        GiteAvailability.builder()
                                .gite(gite)
                                .date(date)
                                .status(AvailabilityStatus.UNAVAILABLE)
                                .build()
                );
            }
            current = current.plusDays(1);
        }
    }
}
