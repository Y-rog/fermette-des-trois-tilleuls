package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.FermetteInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour les informations de contact
 * de la fermette. Une seule ligne en BDD (id = 1).
 */
@Repository
public interface FermetteInfoRepository extends JpaRepository<FermetteInfo, Long> { }
