package com.yrog.fermedufay.repository;

import com.yrog.fermedufay.entity.GiteAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiteAvailabilityRepository extends JpaRepository<GiteAvailability, Long> { }
