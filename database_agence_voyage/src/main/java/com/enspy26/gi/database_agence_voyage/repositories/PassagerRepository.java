package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.Passager;

@Repository
public interface PassagerRepository extends JpaRepository<Passager, UUID> {

    List<Passager> findAllByIdReservation(UUID idReservation);
}
