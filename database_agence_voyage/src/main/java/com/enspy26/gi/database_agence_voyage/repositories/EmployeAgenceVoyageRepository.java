package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.EmployeAgenceVoyage;
import com.enspy26.gi.database_agence_voyage.enums.StatutEmploye;

@Repository
public interface EmployeAgenceVoyageRepository extends CassandraRepository<EmployeAgenceVoyage, UUID> {

    /**
     * Trouve tous les employés d'une agence
     */
    @AllowFiltering
    List<EmployeAgenceVoyage> findByAgenceVoyageId(UUID agenceVoyageId);

    /**
     * Trouve tous les employés d'une agence avec un statut donné
     */
    @AllowFiltering
    List<EmployeAgenceVoyage> findByAgenceVoyageIdAndStatutEmploye(UUID agenceVoyageId, StatutEmploye statut);

    /**
     * Trouve l'employé par son userId
     */
    @AllowFiltering
    EmployeAgenceVoyage findByUserId(UUID userId);

    /**
     * Trouve tous les employés d'un département
     */
    @AllowFiltering
    List<EmployeAgenceVoyage> findByAgenceVoyageIdAndDepartement(UUID agenceVoyageId, String departement);

    /**
     * Trouve tous les employés sous un manager
     */
    @AllowFiltering
    List<EmployeAgenceVoyage> findByManagerId(UUID managerId);

    /**
     * Vérifie si un utilisateur est employé dans une agence
     */
    @AllowFiltering
    boolean existsByUserIdAndAgenceVoyageId(UUID userId, UUID agenceVoyageId);

    /**
     * Compte le nombre d'employés actifs d'une agence
     */
    long countByAgenceVoyageIdAndStatutEmploye(UUID agenceVoyageId, StatutEmploye statut);
}
