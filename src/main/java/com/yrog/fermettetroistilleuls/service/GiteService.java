package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.GiteAvailabilityDto;
import com.yrog.fermettetroistilleuls.dto.GiteDto;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.GiteAvailabilityRepository;
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

    public GiteService(GiteRepository giteRepository, GiteAvailabilityRepository giteAvailabilityRepository) {
        this.giteRepository = giteRepository;
        this.giteAvailabilityRepository = giteAvailabilityRepository;
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


}
