package com.yrog.fermettetroistilleuls.repository;

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
}
