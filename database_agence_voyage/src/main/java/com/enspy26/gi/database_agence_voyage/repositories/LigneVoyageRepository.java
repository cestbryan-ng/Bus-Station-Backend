package com.enspy26.gi.database_agence_voyage.repositories;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.LigneVoyage;

import java.util.List;
import java.util.UUID;

@Repository
public interface LigneVoyageRepository extends CassandraRepository<LigneVoyage, UUID> {
    // @Query("SELECT * FROM lignevoyage WHERE idUser = ?0")
    // List<LigneVoyage> findByIdUser(UUID idUser);

    // @Query("SELECT * FROM lignevoyage WHERE idVoyage = ?0")
    @AllowFiltering
    LigneVoyage findByIdVoyage(UUID idVoyage);

    @AllowFiltering
    List<LigneVoyage> findByIdAgenceVoyage(UUID agenceId);

    // @Query("SELECT * FROM lignevoyage WHERE idUser = ?0 AND idVoyage = ?1 ALLOW
    // FILTERING")
    // Optional<LigneVoyage> findByIdUserAndIdVoyage(UUID idUser, UUID idVoyage);

    // @Transactional
    // @Query("UPDATE lignevoyage SET status = ?1, prixPaye = ?2, classe = ?3,
    // dateReservation = ?4, dateConfirmation = ?5, idVoyage = ?6, idUser = ?7 WHERE
    // idLigne = ?0")
    // void updateLigneVoyage(UUID idLigne, String status, BigDecimal prixPaye,
    // String classe, Instant dateReservation, Instant dateConfirmation, UUID
    // idVoyage, UUID idUser);
}
