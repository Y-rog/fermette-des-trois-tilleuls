package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.GiteBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiteBookingRepository extends JpaRepository<GiteBooking, Long> { }
