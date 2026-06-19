package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.ActivityBookingArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour les réservations d'activités archivées.
 */
@Repository
public interface ActivityBookingArchiveRepository extends JpaRepository<ActivityBookingArchive, Long> {

    /**
     * Retourne toutes les archives triées par date d'activité
     * décroissante (les plus récentes en premier).
     */
    java.util.List<ActivityBookingArchive> findAllByOrderByActivityDateDesc();
}
