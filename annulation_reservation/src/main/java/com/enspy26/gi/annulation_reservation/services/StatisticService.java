package com.enspy26.gi.annulation_reservation.services;

import com.enspy26.gi.database_agence_voyage.dto.statistics.AgenceStatisticsDTO;
import com.enspy26.gi.database_agence_voyage.dto.statistics.AgenceEvolutionDTO;
import com.enspy26.gi.database_agence_voyage.dto.statistics.EvolutionData;
import com.enspy26.gi.database_agence_voyage.enums.BusinessActorType;
import com.enspy26.gi.database_agence_voyage.models.*;
import com.enspy26.gi.database_agence_voyage.repositories.*;
import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;
import com.enspy26.gi.database_agence_voyage.enums.StatutReservation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@AllArgsConstructor
public class StatisticService {

    private final UserRepository userRepository;
    private final ChauffeurAgenceVoyageRepository chauffeurAgenceVoyageRepository;
    private final VoyageRepository voyageRepository;
    private final LigneVoyageRepository ligneVoyageRepository;
    private final ReservationRepository reservationRepository;
    private final ClassVoyageRepository classVoyageRepository;
    private final AgenceVoyageRepository agenceVoyageRepository;
    private final EmployeAgenceVoyageRepository employeAgenceVoyageRepository;

    public AgenceStatisticsDTO getAgenceStatistics(UUID agenceId) {
        // Vérifier que l'agence existe
        if (!agenceVoyageRepository.existsById(agenceId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agence non trouvée");
        }

        AgenceStatisticsDTO stats = new AgenceStatisticsDTO();

        // Nombre d'employés
        stats.setNombreEmployes(getNombreEmployes(agenceId));

        // Nombre de chauffeurs
        stats.setNombreChauffeurs(getNombreChauffeurs(agenceId));

        // Nombre de voyages
        stats.setNombreVoyages(getNombreVoyages(agenceId));

        // Voyages par statut
        stats.setVoyagesParStatut(getVoyagesParStatut(agenceId));

        // Nombre de réservations
        stats.setNombreReservations(getNombreReservations(agenceId));

        // Réservations par statut
        stats.setReservationsParStatut(getReservationsParStatut(agenceId));

        // Revenus
        stats.setRevenus(getRevenus(agenceId));

        // Nouveaux utilisateurs (dans les 30 derniers jours)
        stats.setNouveauxUtilisateurs(getNouveauxUtilisateurs(agenceId));

        // Taux d'occupation
        stats.setTauxOccupation(getTauxOccupation(agenceId));

        return stats;
    }

    public AgenceEvolutionDTO getAgenceEvolution(UUID agenceId) {
        // Vérifier que l'agence existe
        if (!agenceVoyageRepository.existsById(agenceId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agence non trouvée");
        }

        AgenceEvolutionDTO evolution = new AgenceEvolutionDTO();

        // Évolution des réservations (6 derniers mois)
        evolution.setEvolutionReservations(getEvolutionReservations(agenceId));

        // Évolution des voyages
        evolution.setEvolutionVoyages(getEvolutionVoyages(agenceId));

        // Évolution des revenus
        evolution.setEvolutionRevenus(getEvolutionRevenus(agenceId));

        // Évolution des utilisateurs
        evolution.setEvolutionUtilisateurs(getEvolutionUtilisateurs(agenceId));

        return evolution;
    }

    private long getNombreEmployes(UUID agenceId) {
        // Récupérer tous les employés directs
        List<EmployeAgenceVoyage> employes = employeAgenceVoyageRepository.findByAgenceVoyageId(agenceId);

        // Récupérer tous les chauffeurs
        List<ChauffeurAgenceVoyage> chauffeurs = chauffeurAgenceVoyageRepository.findByAgenceVoyageId(agenceId);

        // Utiliser un Set pour éviter les doublons basés sur userId
        Set<UUID> userIds = new HashSet<>();

        // Ajouter les userId des employés
        for (EmployeAgenceVoyage employe : employes) {
            userIds.add(employe.getUserId());
        }

        // Ajouter les userId des chauffeurs
        for (ChauffeurAgenceVoyage chauffeur : chauffeurs) {
            userIds.add(chauffeur.getUserId());
        }

        return userIds.size();
    }


    private long getNombreChauffeurs(UUID agenceId) {
        return chauffeurAgenceVoyageRepository.findByAgenceVoyageId(agenceId).size();
    }

    private long getNombreVoyages(UUID agenceId) {
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);
        return lignesVoyage.size();
    }

    private Map<String, Long> getVoyagesParStatut(UUID agenceId) {
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);
        Map<String, Long> voyagesParStatut = new HashMap<>();

        for (StatutVoyage statut : StatutVoyage.values()) {
            voyagesParStatut.put(statut.name(), 0L);
        }

        for (LigneVoyage ligneVoyage : lignesVoyage) {
            Voyage voyage = voyageRepository.findById(ligneVoyage.getIdVoyage()).orElse(null);
            if (voyage != null) {
                String statut = voyage.getStatusVoyage().name();
                voyagesParStatut.put(statut, voyagesParStatut.get(statut) + 1);
            }
        }

        return voyagesParStatut;
    }

    private long getNombreReservations(UUID agenceId) {
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);
        long totalReservations = 0;

        for (LigneVoyage ligneVoyage : lignesVoyage) {
            List<Reservation> reservations = reservationRepository.findByIdVoyage(ligneVoyage.getIdVoyage());
            totalReservations += reservations.size();
        }

        return totalReservations;
    }

    private Map<String, Long> getReservationsParStatut(UUID agenceId) {
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);
        Map<String, Long> reservationsParStatut = new HashMap<>();

        for (StatutReservation statut : StatutReservation.values()) {
            reservationsParStatut.put(statut.name(), 0L);
        }

        for (LigneVoyage ligneVoyage : lignesVoyage) {
            List<Reservation> reservations = reservationRepository.findByIdVoyage(ligneVoyage.getIdVoyage());
            for (Reservation reservation : reservations) {
                String statut = reservation.getStatutReservation().name();
                reservationsParStatut.put(statut, reservationsParStatut.get(statut) + 1);
            }
        }

        return reservationsParStatut;
    }

    private double getRevenus(UUID agenceId) {
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);
        double totalRevenus = 0.0;

        for (LigneVoyage ligneVoyage : lignesVoyage) {
            List<Reservation> reservations = reservationRepository.findByIdVoyage(ligneVoyage.getIdVoyage());
            for (Reservation reservation : reservations) {
                if (reservation.getStatutReservation() == StatutReservation.CONFIRMER ||
                        reservation.getStatutReservation() == StatutReservation.VALIDER) {
                    totalRevenus += reservation.getMontantPaye();
                }
            }
        }

        return totalRevenus;
    }

    private long getNouveauxUtilisateurs(UUID agenceId) {
        // Récupérer toutes les réservations de l'agence
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);
        Set<UUID> utilisateursConsommateurs = new HashSet<>();

        for (LigneVoyage ligneVoyage : lignesVoyage) {
            List<Reservation> reservations = reservationRepository.findByIdVoyage(ligneVoyage.getIdVoyage());
            for (Reservation reservation : reservations) {
                // Ajouter l'utilisateur qui a fait la réservation
                utilisateursConsommateurs.add(reservation.getIdUser());
            }
        }

        // Filtrer pour ne garder que les utilisateurs CONSUMER
        long countConsommateurs = 0;
        for (UUID userId : utilisateursConsommateurs) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getBusinessActorType() == BusinessActorType.CONSUMER) {
                countConsommateurs++;
            }
        }

        // Simulation : considérer qu'environ 20% sont de nouveaux utilisateurs dans les 30 derniers jours
        return (long) (countConsommateurs * 0.2);
    }

    private double getTauxOccupation(UUID agenceId) {
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);
        int totalPlaces = 0;
        int placesReservees = 0;

        for (LigneVoyage ligneVoyage : lignesVoyage) {
            Voyage voyage = voyageRepository.findById(ligneVoyage.getIdVoyage()).orElse(null);
            if (voyage != null) {
                totalPlaces += (voyage.getNbrPlaceReservable() + voyage.getNbrPlaceReserve());
                placesReservees += voyage.getNbrPlaceReserve();
            }
        }

        return totalPlaces > 0 ? (double) placesReservees / totalPlaces * 100 : 0.0;
    }

    private List<EvolutionData> getEvolutionReservations(UUID agenceId) {
        List<EvolutionData> evolution = new ArrayList<>();
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);

        // Grouper les réservations par mois sur les 6 derniers mois
        LocalDate maintenant = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate mois = maintenant.minusMonths(i);
            long countReservations = 0;

            for (LigneVoyage ligneVoyage : lignesVoyage) {
                List<Reservation> reservations = reservationRepository.findByIdVoyage(ligneVoyage.getIdVoyage());
                countReservations += reservations.stream()
                        .filter(r -> {
                            LocalDate dateReservation = r.getDateReservation()
                                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return dateReservation.getYear() == mois.getYear() &&
                                    dateReservation.getMonth() == mois.getMonth();
                        })
                        .count();
            }

            evolution.add(new EvolutionData(mois, countReservations, 0.0));
        }

        return evolution;
    }

    private List<EvolutionData> getEvolutionVoyages(UUID agenceId) {
        List<EvolutionData> evolution = new ArrayList<>();
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);

        LocalDate maintenant = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate mois = maintenant.minusMonths(i);
            long countVoyages = 0;

            for (LigneVoyage ligneVoyage : lignesVoyage) {
                Voyage voyage = voyageRepository.findById(ligneVoyage.getIdVoyage()).orElse(null);
                if (voyage != null && voyage.getDatePublication() != null) {
                    LocalDate datePublication = voyage.getDatePublication()
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (datePublication.getYear() == mois.getYear() &&
                            datePublication.getMonth() == mois.getMonth()) {
                        countVoyages++;
                    }
                }
            }

            evolution.add(new EvolutionData(mois, countVoyages, 0.0));
        }

        return evolution;
    }

    private List<EvolutionData> getEvolutionRevenus(UUID agenceId) {
        List<EvolutionData> evolution = new ArrayList<>();
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);

        LocalDate maintenant = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate mois = maintenant.minusMonths(i);
            double revenus = 0.0;

            for (LigneVoyage ligneVoyage : lignesVoyage) {
                List<Reservation> reservations = reservationRepository.findByIdVoyage(ligneVoyage.getIdVoyage());
                revenus += reservations.stream()
                        .filter(r -> {
                            if (r.getDateConfirmation() == null) return false;
                            LocalDate dateConfirmation = r.getDateConfirmation()
                                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return dateConfirmation.getYear() == mois.getYear() &&
                                    dateConfirmation.getMonth() == mois.getMonth();
                        })
                        .mapToDouble(Reservation::getMontantPaye)
                        .sum();
            }

            evolution.add(new EvolutionData(mois, 0, revenus));
        }

        return evolution;
    }

    private List<EvolutionData> getEvolutionUtilisateurs(UUID agenceId) {
        List<EvolutionData> evolution = new ArrayList<>();
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);

        LocalDate maintenant = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate mois = maintenant.minusMonths(i);
            Set<UUID> utilisateursMois = new HashSet<>();

            // Collecter tous les utilisateurs ayant fait des réservations ce mois-ci
            for (LigneVoyage ligneVoyage : lignesVoyage) {
                List<Reservation> reservations = reservationRepository.findByIdVoyage(ligneVoyage.getIdVoyage());
                for (Reservation reservation : reservations) {
                    LocalDate dateReservation = reservation.getDateReservation()
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (dateReservation.getYear() == mois.getYear() &&
                            dateReservation.getMonth() == mois.getMonth()) {
                        utilisateursMois.add(reservation.getIdUser());
                    }
                }
            }

            // Compter uniquement les utilisateurs CONSUMER
            long nouveauxUtilisateursConsommateurs = 0;
            for (UUID userId : utilisateursMois) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null && user.getBusinessActorType() == BusinessActorType.CONSUMER) {
                    nouveauxUtilisateursConsommateurs++;
                }
            }

            evolution.add(new EvolutionData(mois, nouveauxUtilisateursConsommateurs, 0.0));
        }

        return evolution;
    }
}