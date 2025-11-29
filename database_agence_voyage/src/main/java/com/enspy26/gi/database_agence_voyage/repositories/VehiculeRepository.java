package com.enspy26.gi.database_agence_voyage.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.Vehicule;

import java.util.UUID;
import java.util.List;

@Repository
public interface VehiculeRepository extends CassandraRepository<Vehicule, UUID> {

    @AllowFiltering
    List<Vehicule> findByIdAgenceVoyage(UUID idAgenceVoyage);
}
