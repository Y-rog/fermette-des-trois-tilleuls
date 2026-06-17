package com.yrog.fermedufay.service;

import com.yrog.fermedufay.dto.GiteDto;
import com.yrog.fermedufay.repository.GiteRepository;
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

    public GiteService(GiteRepository giteRepository) {
        this.giteRepository = giteRepository;
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
}
