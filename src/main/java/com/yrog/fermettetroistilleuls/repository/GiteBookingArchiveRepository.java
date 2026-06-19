package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.GiteBookingArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour les réservations de gîtes archivées.
 */
@Repository
public interface GiteBookingArchiveRepository extends JpaRepository<GiteBookingArchive, Long> {

    /**
     * Retourne toutes les archives triées par date d'arrivée
     * décroissante (les plus récentes en premier).
     */
    java.util.List<GiteBookingArchive> findAllByOrderByCheckInDesc();
}
