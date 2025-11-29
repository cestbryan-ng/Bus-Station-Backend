package com.enspy26.gi.annulation_reservation.services;

import com.enspy26.gi.database_agence_voyage.enums.StatutHistorique;
import com.enspy26.gi.database_agence_voyage.models.Historique;
import com.enspy26.gi.database_agence_voyage.models.Reservation;
import com.enspy26.gi.database_agence_voyage.repositories.HistoriqueRepository;
import com.enspy26.gi.database_agence_voyage.repositories.ReservationRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HistoriqueService {

    private final HistoriqueRepository historiqueRepository;

    private final ReservationRepository reservationRepository;

    public Historique findById(UUID id) {
        return historiqueRepository.findById(id).orElse(null);
    }

    public List<Historique> findAll() {
        return historiqueRepository.findAll();
    }

    public Historique create(Historique historique) {
        return historiqueRepository.save(historique);
    }

    public Historique update(Historique historique) {
        return historiqueRepository.save(historique);
    }

    public void delete(UUID id) {
        historiqueRepository.deleteById(id);
    }

    public List<Historique> historiqueReservationParUtilisateur(UUID userId) {
        List<Reservation> reservations = reservationRepository.findByIdUser(userId);

        List<Historique> historiques = new ArrayList<>();

        for (Reservation reservation : reservations) {
            Historique historique = historiqueRepository.findByIdReservation(reservation.getIdReservation())
                    .orElse(null);
            if (historique != null && historique.getStatusHistorique() == StatutHistorique.VALIDER) {
                historiques.add(historique);
            }
        }

        return historiques;
    }
}
