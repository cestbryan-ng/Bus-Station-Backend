package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.ClassVoyage;

@Repository
public interface ClassVoyageRepository extends JpaRepository<ClassVoyage, UUID> {

    List<ClassVoyage> findByIdAgenceVoyage(UUID id);

}
