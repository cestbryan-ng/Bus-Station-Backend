package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;

@Repository
public interface AgenceVoyageRepository extends CassandraRepository<AgenceVoyage, UUID> {

  @AllowFiltering
  List<AgenceVoyage> findByLongName(String longName);

  @AllowFiltering
  List<AgenceVoyage> findByShortName(String shortName);

  @AllowFiltering
  List<AgenceVoyage> findByUserId(UUID userId);

  @AllowFiltering
  boolean existsByLongName(String longName);

  @AllowFiltering
  boolean existsByShortName(String shortName);

  @AllowFiltering
  List<AgenceVoyage> findByOrganisationId(UUID organizationId);
}
