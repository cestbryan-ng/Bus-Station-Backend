package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.PolitiqueAnnulation;

@Repository
public interface PolitiqueAnnulationRepository extends CassandraRepository<PolitiqueAnnulation, UUID> {

    @AllowFiltering
    PolitiqueAnnulation findByIdAgenceVoyage(UUID idAgenceVoyage);

}
