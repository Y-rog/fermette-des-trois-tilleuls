package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.GiteAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiteAvailabilityRepository extends JpaRepository<GiteAvailability, Long> {
    /**
     * Retourne les disponibilités d'un gîte
     * triées par date.
     */
    List<GiteAvailability> findByGiteIdOrderByDateAsc(Long giteId);
}
