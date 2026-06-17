package com.yrog.fermedufay.repository;

import com.yrog.fermedufay.entity.Gite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiteRepository extends JpaRepository<Gite, Long> {}
