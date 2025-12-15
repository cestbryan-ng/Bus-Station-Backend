package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.Historique;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, UUID> {

    Optional<Historique> findByIdReservation(UUID idReservation);

}
