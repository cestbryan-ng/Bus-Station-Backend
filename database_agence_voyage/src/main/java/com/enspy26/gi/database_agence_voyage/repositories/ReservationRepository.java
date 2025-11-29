package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.enums.StatutReservation;
import com.enspy26.gi.database_agence_voyage.models.Reservation;

@Repository
public interface ReservationRepository extends CassandraRepository<Reservation, UUID> {
    @AllowFiltering
    Optional<Reservation> findByIdUserAndIdVoyage(UUID IdUser, UUID IdVoyage);

    @AllowFiltering
    Slice<Reservation> findByIdUser(UUID idUser, Pageable pageable);

    @AllowFiltering
    List<Reservation> findByIdUser(UUID idUser);

    @AllowFiltering
    List<Reservation> findByIdVoyage(UUID idVoyage);

    @AllowFiltering
    List<Reservation> findAllByStatutReservationIsIn(Collection<StatutReservation> statutReservations);
}
