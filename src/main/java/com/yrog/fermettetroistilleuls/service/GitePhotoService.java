package com.yrog.fermettetroistilleuls.service;

import com.yrog.fermettetroistilleuls.dto.GitePhotoDto;
import com.yrog.fermettetroistilleuls.entity.Gite;
import com.yrog.fermettetroistilleuls.entity.GitePhoto;
import com.yrog.fermettetroistilleuls.exception.ResourceNotFoundException;
import com.yrog.fermettetroistilleuls.repository.GitePhotoRepository;
import com.yrog.fermettetroistilleuls.repository.GiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant les photos des gîtes :
 * ajout, suppression et consultation.
 */
@Service
public class GitePhotoService {

    private static final Logger log = LoggerFactory.getLogger(GitePhotoService.class);

    private final GitePhotoRepository gitePhotoRepository;
    private final GiteRepository giteRepository;

    public GitePhotoService(GitePhotoRepository gitePhotoRepository,
                            GiteRepository giteRepository) {
        this.gitePhotoRepository = gitePhotoRepository;
        this.giteRepository = giteRepository;
    }

    /**
     * Retourne les photos d'un gîte triées par ordre d'affichage.
     *
     * @param giteId identifiant du gîte
     * @return liste de GitePhotoDto
     */
    @Transactional(readOnly = true)
    public List<GitePhotoDto> findPhotos(Long giteId) {
        log.info("Récupération des photos du gîte id={}", giteId);
        return gitePhotoRepository.findByGiteIdOrderByDisplayOrderAsc(giteId)
                .stream()
                .map(photo -> new GitePhotoDto(photo.getId(), photo.getUrl()))
                .toList();
    }

    /**
     * Ajoute une photo à un gîte. L'ordre d'affichage
     * est automatiquement défini après les photos existantes.
     *
     * @param giteId identifiant du gîte
     * @param url     URL de la photo
     * @throws ResourceNotFoundException si le gîte n'existe pas
     */
    @Transactional
    public void addPhoto(Long giteId, String url) {
        Gite gite = giteRepository.findById(giteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Gîte introuvable : " + giteId));

        int nextOrder = gitePhotoRepository.findByGiteIdOrderByDisplayOrderAsc(giteId).size();

        GitePhoto photo = GitePhoto.builder()
                .gite(gite)
                .url(url)
                .displayOrder(nextOrder)
                .build();

        gitePhotoRepository.save(photo);
        log.info("Photo ajoutée au gîte {} : {}", giteId, url);
    }

    /**
     * Supprime une photo.
     *
     * @param photoId identifiant de la photo
     * @throws ResourceNotFoundException si la photo n'existe pas
     */
    @Transactional
    public void deletePhoto(Long photoId) {
        if (!gitePhotoRepository.existsById(photoId)) {
            throw new ResourceNotFoundException("Photo introuvable : " + photoId);
        }
        gitePhotoRepository.deleteById(photoId);
        log.info("Photo {} supprimée", photoId);
    }
}