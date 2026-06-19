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
}
