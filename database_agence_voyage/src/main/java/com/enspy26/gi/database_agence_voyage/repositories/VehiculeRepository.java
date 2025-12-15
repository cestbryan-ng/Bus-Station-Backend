package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.Vehicule;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, UUID> {

    List<Vehicule> findByIdAgenceVoyage(UUID idAgenceVoyage);
}
