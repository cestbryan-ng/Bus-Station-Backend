package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;

@Repository
public interface AgenceVoyageRepository extends JpaRepository<AgenceVoyage, UUID> {

    List<AgenceVoyage> findByLongName(String longName);

    List<AgenceVoyage> findByShortName(String shortName);

    List<AgenceVoyage> findByUserId(UUID userId);

    boolean existsByLongName(String longName);

    boolean existsByShortName(String shortName);

    List<AgenceVoyage> findByOrganisationId(UUID organisationId);
}
