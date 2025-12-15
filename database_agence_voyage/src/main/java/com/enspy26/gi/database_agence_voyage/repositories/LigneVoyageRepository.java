package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.LigneVoyage;

@Repository
public interface LigneVoyageRepository extends JpaRepository<LigneVoyage, UUID> {

    // Recherche par idVoyage (supposé unique ici, retourne un seul résultat)
    LigneVoyage findByIdVoyage(UUID idVoyage);

    // Recherche par idAgenceVoyage (plusieurs résultats)
    List<LigneVoyage> findByIdAgenceVoyage(UUID agenceId);

}
