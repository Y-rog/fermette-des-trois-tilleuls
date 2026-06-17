package com.yrog.fermedufay.repository;

import com.yrog.fermedufay.entity.ActivityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityBookingRepository extends JpaRepository<ActivityBooking, Long> { }
