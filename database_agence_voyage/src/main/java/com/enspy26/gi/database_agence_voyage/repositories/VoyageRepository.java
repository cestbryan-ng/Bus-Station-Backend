package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.Voyage;

@Repository
public interface VoyageRepository extends JpaRepository<Voyage, UUID> {

}
// Optionally, if you want paging and sorting:
// public interface VoyageRepository extends PagingAndSortingRepository<Voyage, UUID> {}
