package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.entity.FermetteInfo;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.FermetteInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service gérant les informations de contact
 * de la fermette.
 */
@Service
public class FermetteInfoService {

    private static final Logger log = LoggerFactory.getLogger(FermetteInfoService.class);

    private final FermetteInfoRepository fermetteInfoRepository;

    public FermetteInfoService(FermetteInfoRepository fermetteInfoRepository) {
        this.fermetteInfoRepository = fermetteInfoRepository;
    }

    /**
     * Retourne les informations de contact de la fermette.
     * Il n'existe qu'une seule ligne en BDD (id = 1).
     *
     * @return FermetteInfo
     * @throws ResourceNotFoundException si les infos n'existent pas
     */
    @Transactional(readOnly = true)
    public FermetteInfo findInfo() {
        log.info("Récupération des informations de la fermette");
        return fermetteInfoRepository.findById(1L)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Informations de la fermette introuvables"));
    }

    /**
     * Met à jour les informations de contact de la fermette.
     *
     * @param info les nouvelles informations
     */
    @Transactional
    public void updateInfo(FermetteInfo info) {
        info.setId(1L);
        fermetteInfoRepository.save(info);
        log.info("Informations de la fermette mises à jour");
    }
}
