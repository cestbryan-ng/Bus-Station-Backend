package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.ChauffeurAgenceVoyage;

@Repository
public interface ChauffeurAgenceVoyageRepository extends JpaRepository<ChauffeurAgenceVoyage, UUID> {

    List<ChauffeurAgenceVoyage> findByAgenceVoyageId(UUID agenceVoyageId);

    int countByAgenceVoyageId(UUID agence_voyage_id);

    Optional<ChauffeurAgenceVoyage> findByUserId(UUID userId);
}
