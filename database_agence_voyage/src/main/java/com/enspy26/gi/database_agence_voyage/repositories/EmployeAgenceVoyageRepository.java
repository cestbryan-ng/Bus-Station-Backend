package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.EmployeAgenceVoyage;
import com.enspy26.gi.database_agence_voyage.enums.StatutEmploye;

@Repository
public interface EmployeAgenceVoyageRepository extends JpaRepository<EmployeAgenceVoyage, UUID> {

    /**
     * Trouve tous les employés d'une agence
     */
    List<EmployeAgenceVoyage> findByAgenceVoyageId(UUID agenceVoyageId);

    int countByAgenceVoyageId(UUID agence_voyage_id);
    /**
     * Trouve tous les employés d'une agence avec un statut donné
     */
    List<EmployeAgenceVoyage> findByAgenceVoyageIdAndStatutEmploye(UUID agenceVoyageId, StatutEmploye statut);

    /**
     * Trouve l'employé par son userId
     */
    EmployeAgenceVoyage findByUserId(UUID userId);

    /**
     * Trouve tous les employés d'un département
     */
    List<EmployeAgenceVoyage> findByAgenceVoyageIdAndDepartement(UUID agenceVoyageId, String departement);

    /**
     * Trouve tous les employés sous un manager
     */
    List<EmployeAgenceVoyage> findByManagerId(UUID managerId);

    /**
     * Vérifie si un utilisateur est employé dans une agence
     */
    boolean existsByUserIdAndAgenceVoyageId(UUID userId, UUID agenceVoyageId);

    /**
     * Compte le nombre d'employés actifs d'une agence
     */
    long countByAgenceVoyageIdAndStatutEmploye(UUID agenceVoyageId, StatutEmploye statut);
}
