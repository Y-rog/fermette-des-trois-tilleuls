package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.ActivityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityBookingRepository extends JpaRepository<ActivityBooking, Long> { }
