package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.ChauffeurAgenceVoyage;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChauffeurAgenceVoyageRepository extends CassandraRepository<ChauffeurAgenceVoyage, UUID> {

  @AllowFiltering
  List<ChauffeurAgenceVoyage> findByAgenceVoyageId(UUID agenceVoyageId);

  @AllowFiltering
  Optional<ChauffeurAgenceVoyage> findByUserId(UUID userId);
}
