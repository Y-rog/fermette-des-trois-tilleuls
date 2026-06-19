package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.BookingStatus;
import com.yrog.fermettetroistilleuls.entity.GiteBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiteBookingRepository extends JpaRepository<GiteBooking, Long> {
    /**
     * Retourne toutes les réservations triées par statut
     * (PENDING en premier) puis par date de création décroissante.
     */
    List<GiteBooking> findAllByOrderByStatusAscCreatedAtDesc();

    /**
     * Vérifie si des réservations existent pour ce gîte
     * avec l'un des statuts donnés.
     */
    boolean existsByGiteIdAndStatusIn(Long giteId, List<BookingStatus> statuses);
}
