package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.PolitiqueAnnulation;

@Repository
public interface PolitiqueAnnulationRepository extends JpaRepository<PolitiqueAnnulation, UUID> {

    PolitiqueAnnulation findByIdAgenceVoyage(UUID idAgenceVoyage);

}
