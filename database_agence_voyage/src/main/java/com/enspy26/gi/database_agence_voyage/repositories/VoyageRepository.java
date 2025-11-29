package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
//import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.Voyage;

@Repository
public interface VoyageRepository extends CassandraRepository<Voyage, UUID> {

}
// public interface VoyageRepository extends PagingAndSortingRepository<Voyage,
// UUID> {

// }
