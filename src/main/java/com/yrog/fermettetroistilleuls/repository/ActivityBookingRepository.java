package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.ActivityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityBookingRepository extends JpaRepository<ActivityBooking, Long> {
    /**
     * Retourne toutes les réservations triées par statut
     * (PENDING en premier) puis par date de création décroissante.
     */
    List<ActivityBooking> findAllByOrderByStatusAscCreatedAtDesc();
}
