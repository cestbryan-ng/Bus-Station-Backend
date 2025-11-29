package com.enspy26.gi.database_agence_voyage.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.ClassVoyage;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClassVoyageRepository extends CassandraRepository<ClassVoyage, UUID> {

    @AllowFiltering
    List<ClassVoyage> findByIdAgenceVoyage(UUID id);

}
