package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.SoldeIndemnisation;

@Repository
public interface SoldeIndemnisationRepository extends JpaRepository<SoldeIndemnisation, UUID> {

    Optional<SoldeIndemnisation> findByIdUserAndIdAgenceVoyage(UUID idUser, UUID idAgenceVoyage);

}
