package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.Voyage;

/**
 * Repository interface for Voyage entity
 * Provides search methods with filters on cities (lieu) and zones (point)
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-18
 */
@Repository
public interface VoyageRepository extends JpaRepository<Voyage, UUID> {

    /**
     * Search voyages by departure and arrival cities
     */
    Page<Voyage> findByLieuDepartAndLieuArrive(
            String lieuDepart,
            String lieuArrive,
            Pageable pageable
    );

    /**
     * Search voyages by cities and zones
     */
    Page<Voyage> findByLieuDepartAndLieuArriveAndPointDeDepartAndPointArrivee(
            String lieuDepart,
            String lieuArrive,
            String pointDeDepart,
            String pointArrivee,
            Pageable pageable
    );

    /**
     * Advanced search with optional filters
     * Uses JPQL with CAST for PostgreSQL compatibility
     */
    @Query("SELECT v FROM Voyage v WHERE " +
            "v.lieuDepart = :lieuDepart AND " +
            "v.lieuArrive = :lieuArrive AND " +
            "(:pointDeDepart IS NULL OR v.pointDeDepart = :pointDeDepart) AND " +
            "(:pointArrivee IS NULL OR v.pointArrivee = :pointArrivee) AND " +
            "(:dateDepart IS NULL OR CAST(v.dateDepartPrev AS date) = CAST(:dateDepart AS date))")
    Page<Voyage> searchVoyages(
            @Param("lieuDepart") String lieuDepart,
            @Param("lieuArrive") String lieuArrive,
            @Param("pointDeDepart") String pointDeDepart,
            @Param("pointArrivee") String pointArrivee,
            @Param("dateDepart") Date dateDepart,
            Pageable pageable
    );
}