package com.yrog.fermettetroistilleuls.repository;

import com.yrog.fermettetroistilleuls.entity.Gite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiteRepository extends JpaRepository<Gite, Long> {
    List<Gite> id(Long id);
}
