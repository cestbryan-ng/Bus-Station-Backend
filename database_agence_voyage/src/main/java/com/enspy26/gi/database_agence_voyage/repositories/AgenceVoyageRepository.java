package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;

/**
 * Repository interface for AgenceVoyage entity
 * Provides database access methods for travel agencies
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Repository
public interface AgenceVoyageRepository extends JpaRepository<AgenceVoyage, UUID> {

    List<AgenceVoyage> findByLongName(String long_name);

    List<AgenceVoyage> findByShortName(String short_name);

    boolean existsByLongName(String long_name);

    boolean existsByShortName(String short_name);

    List<AgenceVoyage> findByOrganisationId(UUID organisation_id);

    Page<AgenceVoyage> findAll(Pageable pageable);

    Page<AgenceVoyage> findByVille(String ville, Pageable pageable);

    Page<AgenceVoyage> findByOrganisationId(UUID organisation_id, Pageable pageable);

    Page<AgenceVoyage> findByStatutValidation(StatutValidation statut_validation, Pageable pageable);

    Page<AgenceVoyage> findByStatutValidationAndVille(StatutValidation statut_validation, String ville, Pageable pageable);
}