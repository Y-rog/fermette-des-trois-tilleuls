package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.GiteAvailabilityDto;
import com.yrog.fermettetroistilleuls.dto.GiteDto;
import com.yrog.fermettetroistilleuls.dto.GiteForm;
import com.yrog.fermettetroistilleuls.entity.BookingStatus;
import com.yrog.fermettetroistilleuls.entity.Gite;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.GiteAvailabilityRepository;
import com.yrog.fermettetroistilleuls.repository.GiteBookingRepository;
import com.yrog.fermettetroistilleuls.repository.GiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant la logique métier des gîtes.
 */
@Service
public class GiteService {

    private static final Logger log = LoggerFactory.getLogger(GiteService.class);

    private final GiteRepository giteRepository;
    private final GiteAvailabilityRepository giteAvailabilityRepository;
    private final GiteBookingRepository giteBookingRepository;

    public GiteService(GiteRepository giteRepository, GiteAvailabilityRepository giteAvailabilityRepository, GiteBookingRepository giteBookingRepository) {
        this.giteRepository = giteRepository;
        this.giteAvailabilityRepository = giteAvailabilityRepository;
        this.giteBookingRepository = giteBookingRepository;
    }

    /**
     * Retourne la liste de tous les gîtes actifs.
     */
    @Transactional(readOnly = true)
    public List<GiteDto> findAll() {
        log.info("Récupération de la liste des gîtes");
        return giteRepository.findAll()
                .stream()
                .map(gite -> new GiteDto(
                        gite.getId(),
                        gite.getName(),
                        gite.getLocation(),
                        gite.getDescription(),
                        gite.getCapacity(),
                        gite.getBedrooms(),
                        gite.getPhotoUrl()
                ))
                .toList();
    }

    /**
     * Retourne le détail d'un gite.
     */
    @Transactional(readOnly = true)
    public GiteDto findById(Long id) {
        log.info("Récupération du gîte id={}", id);
        return giteRepository.findById(id)
                .map(gite -> new GiteDto(
                        gite.getId(),
                        gite.getName(),
                        gite.getLocation(),
                        gite.getDescription(),
                        gite.getCapacity(),
                        gite.getBedrooms(),
                        gite.getPhotoUrl()
                ))
                .orElseThrow(() ->
                        new ResourceNotFoundException("Gîte introuvable : " + id)
                );
    }

    /**
     * Retourne les disponibilités d'un gîte.
     *
     * @param giteId identifiant du gîte
     * @return liste de GiteAvailabilityDto
     */
    @Transactional(readOnly = true)
    public List<GiteAvailabilityDto> findAvailabilities(Long giteId) {
        log.info("Récupération des disponibilités du gîte id={}", giteId);
        return giteAvailabilityRepository.findByGiteIdOrderByDateAsc(giteId)
                .stream()
                .map(a -> new GiteAvailabilityDto(
                        a.getDate(),
                        a.getStatus()
                ))
                .toList();
    }

    /**
     * Crée un nouveau gîte et retourne son identifiant.
     *
     * @param form formulaire rempli par l'admin
     * @return l'identifiant du gîte créé
     */
    @Transactional
    public Long create(GiteForm form) {
        Gite gite = Gite.builder()
                .name(form.getName())
                .location(form.getLocation())
                .description(form.getDescription())
                .capacity(form.getCapacity())
                .bedrooms(form.getBedrooms())
                .photoUrl(form.getPhotoUrl())
                .active(true)
                .build();

        Gite saved = giteRepository.save(gite);
        log.info("Nouveau gîte créé : {}", form.getName());
        return saved.getId();
    }

    /**
     * Met à jour un gîte existant.
     *
     * @param id   identifiant du gîte
     * @param form formulaire rempli par l'admin
     * @throws ResourceNotFoundException si le gîte n'existe pas
     */
    @Transactional
    public void update(Long id, GiteForm form) {
        Gite gite = giteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Gîte introuvable : " + id));

        gite.setName(form.getName());
        gite.setLocation(form.getLocation());
        gite.setDescription(form.getDescription());
        gite.setCapacity(form.getCapacity());
        gite.setBedrooms(form.getBedrooms());
        gite.setPhotoUrl(form.getPhotoUrl());

        giteRepository.save(gite);
        log.info("Gîte {} mis à jour", id);
    }

    /**
     * Supprime un gîte.
     * Refuse la suppression si des réservations PENDING ou ACCEPTED
     * sont en cours pour ce gîte, afin d'éviter de perdre
     * l'historique d'une réservation active.
     *
     * @param id identifiant du gîte
     * @throws ResourceNotFoundException si le gîte n'existe pas
     * @throws IllegalStateException     si des réservations sont en cours
     */
    @Transactional
    public void delete(Long id) {
        if (!giteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gîte introuvable : " + id);
        }

        boolean hasActiveBookings = giteBookingRepository
                .existsByGiteIdAndStatusIn(id, List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED));

        if (hasActiveBookings) {
            throw new IllegalStateException(
                    "Impossible de supprimer ce gîte : des réservations sont en cours");
        }

        // Supprimer d'abord les disponibilités liées (pas de contrainte métier ici,
        // juste du nettoyage technique avant suppression)
        giteAvailabilityRepository.deleteByGiteId(id);

        giteRepository.deleteById(id);
        log.info("Gîte {} supprimé", id);
    }

}
