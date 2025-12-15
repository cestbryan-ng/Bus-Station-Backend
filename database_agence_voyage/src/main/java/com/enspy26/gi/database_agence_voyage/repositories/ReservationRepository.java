package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.enums.StatutReservation;
import com.enspy26.gi.database_agence_voyage.models.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findByIdUserAndIdVoyage(UUID idUser, UUID idVoyage);

    Slice<Reservation> findByIdUser(UUID idUser, Pageable pageable);

    List<Reservation> findByIdUser(UUID idUser);

    List<Reservation> findByIdVoyage(UUID idVoyage);

    List<Reservation> findAllByStatutReservationIsIn(Collection<StatutReservation> statutReservations);
}
