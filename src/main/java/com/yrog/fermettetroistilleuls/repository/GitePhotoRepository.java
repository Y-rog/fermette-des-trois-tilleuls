package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.GitePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les photos des gîtes.
 */
@Repository
public interface GitePhotoRepository extends JpaRepository<GitePhoto, Long> {

    /**
     * Retourne les photos d'un gîte triées par ordre d'affichage.
     */
    List<GitePhoto> findByGiteIdOrderByDisplayOrderAsc(Long giteId);
}