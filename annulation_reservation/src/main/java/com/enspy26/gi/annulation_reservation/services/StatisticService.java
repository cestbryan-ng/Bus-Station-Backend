package com.enspy26.gi.annulation_reservation.services;

import com.enspy26.gi.database_agence_voyage.dto.statistics.*;
import com.enspy26.gi.database_agence_voyage.enums.BusinessActorType;
import com.enspy26.gi.database_agence_voyage.models.*;
import com.enspy26.gi.database_agence_voyage.repositories.*;
import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;
import com.enspy26.gi.database_agence_voyage.enums.StatutReservation;
import com.enspy26.gi.database_agence_voyage.models.Organization;
import com.enspy26.gi.database_agence_voyage.repositories.OrganizationRepository;
import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.Comparator;

import java.util.Set;
import java.util.HashSet;

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
    private final OrganizationRepository organizationRepository;
    private final PassagerRepository passagerRepository;
    private final VehiculeRepository vehiculeRepository;

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

        // Revenus par classe de voyage
        Map<String, Double> revenueByClass = new HashMap<>();
        List<ClassVoyage> classes = classVoyageRepository.findByIdAgenceVoyage(agenceId);
        for (ClassVoyage classe : classes) {
            revenueByClass.put(classe.getNom(), 0.0);
        }

// Top destinations et origines
        Map<String, Integer> destinations = new HashMap<>();
        Map<String, Integer> origins = new HashMap<>();

// Réservations par jour de la semaine
        Map<String, Integer> reservationsByDayOfWeek = new LinkedHashMap<>();
        reservationsByDayOfWeek.put("MONDAY", 0);
        reservationsByDayOfWeek.put("TUESDAY", 0);
        reservationsByDayOfWeek.put("WEDNESDAY", 0);
        reservationsByDayOfWeek.put("THURSDAY", 0);
        reservationsByDayOfWeek.put("FRIDAY", 0);
        reservationsByDayOfWeek.put("SATURDAY", 0);
        reservationsByDayOfWeek.put("SUNDAY", 0);

// Voyages par chauffeur
        Map<String, Integer> tripsByDriver = new HashMap<>();

        List<LigneVoyage> lignes = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);

        for (LigneVoyage ligne : lignes) {
            Voyage voyage = voyageRepository.findById(ligne.getIdVoyage()).orElse(null);
            if (voyage != null) {
                // Destinations et origines
                String destination = voyage.getLieuArrive();
                String origin = voyage.getLieuDepart();
                destinations.put(destination, destinations.getOrDefault(destination, 0) + 1);
                origins.put(origin, origins.getOrDefault(origin, 0) + 1);

                // Par chauffeur
                if (ligne.getIdChauffeur() != null) {
                    User chauffeur = userRepository.findById(
                            chauffeurAgenceVoyageRepository.findById(ligne.getIdChauffeur())
                                    .map(ChauffeurAgenceVoyage::getUserId).orElse(null)
                    ).orElse(null);
                    String driverName = chauffeur != null ? chauffeur.getPrenom() + " " + chauffeur.getNom() : "Inconnu";
                    tripsByDriver.put(driverName, tripsByDriver.getOrDefault(driverName, 0) + 1);
                }

                // Réservations
                List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());
                for (Reservation reservation : reservations) {
                    // Par jour de la semaine
                    if (reservation.getDateReservation() != null) {
                        String dayOfWeek = reservation.getDateReservation().toInstant()
                                .atZone(ZoneId.systemDefault()).getDayOfWeek().name();
                        reservationsByDayOfWeek.put(dayOfWeek, reservationsByDayOfWeek.get(dayOfWeek) + 1);
                    }

                    // Revenus par classe
                    ClassVoyage classe = classVoyageRepository.findById(ligne.getIdClassVoyage()).orElse(null);
                    if (classe != null) {
                        revenueByClass.put(classe.getNom(),
                                revenueByClass.getOrDefault(classe.getNom(), 0.0) + reservation.getPrixTotal());
                    }
                }
            }
        }

// Top 5 destinations
        Map<String, Integer> topDestinations = destinations.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

// Top 5 origines
        Map<String, Integer> topOrigins = origins.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        stats.setRevenueByClass(revenueByClass);
        stats.setTopDestinations(topDestinations);
        stats.setTopOrigins(topOrigins);
        stats.setReservationsByDayOfWeek(reservationsByDayOfWeek);
        stats.setTripsByDriver(tripsByDriver);

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

        // Évolution taux d'occupation
        List<EvolutionData> evolutionTauxOccupation = new ArrayList<>();

// Évolution annulations
        List<EvolutionData> evolutionAnnulations = new ArrayList<>();

// Maps pour graphiques barres
        Map<String, Double> revenuePerMonth = new LinkedHashMap<>();
        Map<String, Integer> reservationsPerMonth = new LinkedHashMap<>();

        LocalDate now = LocalDate.now();
        List<LigneVoyage> lignes = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);

        for (int i = 5; i >= 0; i--) {
            LocalDate mois = now.minusMonths(i);
            String monthKey = mois.getYear() + "-" + String.format("%02d", mois.getMonthValue());

            int totalPlaces = 0;
            int placesReservees = 0;
            int annulations = 0;
            double revenue = 0.0;
            int reservationCount = 0;

            // Set pour éviter de compter plusieurs fois le même voyage
            Set<UUID> voyagesComptabilises = new HashSet<>();

            for (LigneVoyage ligne : lignes) {
                Voyage voyage = voyageRepository.findById(ligne.getIdVoyage()).orElse(null);
                if (voyage != null) {
                    List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());

                    for (Reservation reservation : reservations) {
                        if (reservation.getDateReservation() != null) {
                            LocalDate dateReservation = reservation.getDateReservation().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate();

                            // Filtrer sur dateReservation
                            if (dateReservation.getYear() == mois.getYear() &&
                                    dateReservation.getMonthValue() == mois.getMonthValue()) {

                                reservationCount++;
                                revenue += reservation.getPrixTotal();
                                placesReservees += reservation.getNbrPassager();

                                if (reservation.getStatutReservation() == StatutReservation.ANNULER) {
                                    annulations++;
                                }

                                // Compter les places totales du voyage une seule fois
                                if (!voyagesComptabilises.contains(voyage.getIdVoyage())) {
                                    totalPlaces += voyage.getNbrPlaceReservable() + voyage.getNbrPlaceReserve();
                                    voyagesComptabilises.add(voyage.getIdVoyage());
                                }
                            }
                        }
                    }
                }
            }

            double tauxOccupation = totalPlaces > 0 ? (double) placesReservees / totalPlaces * 100 : 0.0;
            tauxOccupation = Math.round(tauxOccupation * 100.0) / 100.0;  // Arrondi à 2 décimales
            evolutionTauxOccupation.add(new EvolutionData(mois, 0, tauxOccupation));
            evolutionAnnulations.add(new EvolutionData(mois, annulations, 0.0));
            revenuePerMonth.put(monthKey, revenue);
            reservationsPerMonth.put(monthKey, reservationCount);
        }

        evolution.setEvolutionTauxOccupation(evolutionTauxOccupation);
        evolution.setEvolutionAnnulations(evolutionAnnulations);
        evolution.setRevenuePerMonth(revenuePerMonth);
        evolution.setReservationsPerMonth(reservationsPerMonth);

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
                totalRevenus += reservation.getPrixTotal();
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
                            if (r.getDateReservation() == null) return false;
                            LocalDate dateReservation = r.getDateReservation()
                                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return dateReservation.getYear() == mois.getYear() &&
                                    dateReservation.getMonth() == mois.getMonth();
                        })
                        .mapToDouble(Reservation::getPrixTotal)
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

    /**
     * Retrieves consolidated statistics for an organization
     * Aggregates data from all agencies belonging to the organization
     *
     * @param organization_id UUID of the organization
     * @return OrganizationStatisticsDTO with consolidated statistics
     * @throws ResponseStatusException if organization not found (404)
     */
    public OrganizationStatisticsDTO getOrganizationStatistics(UUID organization_id) {
        Organization organization = organizationRepository.findById(organization_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organization with ID " + organization_id + " not found"
                ));

        List<AgenceVoyage> agencies = agenceVoyageRepository.findByOrganisationId(organization_id);

        OrganizationStatisticsDTO stats = new OrganizationStatisticsDTO();

        stats.setOrganizationId(organization_id);
        stats.setOrganizationName(organization.getLongName());
        stats.setTotalAgencies(agencies.size());

        Map<StatutValidation, Integer> agencies_by_status = new HashMap<>();
        agencies_by_status.put(StatutValidation.EN_ATTENTE, 0);
        agencies_by_status.put(StatutValidation.VALIDEE, 0);
        agencies_by_status.put(StatutValidation.REJETEE, 0);
        agencies_by_status.put(StatutValidation.SUSPENDUE, 0);

        for (AgenceVoyage agence : agencies) {
            StatutValidation statut = agence.getStatutValidation();
            if (statut != null) {
                agencies_by_status.put(statut, agencies_by_status.get(statut) + 1);
            }
        }
        stats.setAgenciesByStatus(agencies_by_status);

        int total_employees = 0;
        int total_drivers = 0;
        int total_vehicles = 0;
        int total_trips = 0;
        int total_reservations = 0;
        double total_revenue = 0.0;
        double total_occupancy = 0.0;
        int validated_agencies_count = 0;

        for (AgenceVoyage agence : agencies) {
            if (agence.getStatutValidation() == StatutValidation.VALIDEE) {
                validated_agencies_count++;

                total_employees += getNombreEmployes(agence.getAgencyId());
                total_drivers += getNombreChauffeurs(agence.getAgencyId());

                List<LigneVoyage> lignes = ligneVoyageRepository.findByIdAgenceVoyage(agence.getAgencyId());
                total_vehicles += lignes.stream()
                        .map(LigneVoyage::getIdVehicule)
                        .distinct()
                        .count();

                for (LigneVoyage ligne : lignes) {
                    Voyage voyage = voyageRepository.findById(ligne.getIdVoyage()).orElse(null);

                    if (voyage != null) {
                        total_trips++;

                        List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());
                        total_reservations += reservations.size();

                        for (Reservation reservation : reservations) {
                            total_revenue += reservation.getPrixTotal();
                        }

                        if (voyage.getNbrPlaceReservable() > 0) {
                            double occupancy = (double) voyage.getNbrPlaceReserve() / voyage.getNbrPlaceReservable();
                            total_occupancy += occupancy;
                        }
                    }
                }
            }
        }

        stats.setTotalEmployees(total_employees);
        stats.setTotalDrivers(total_drivers);
        stats.setTotalVehicles(total_vehicles);
        stats.setTotalTrips(total_trips);
        stats.setTotalReservations(total_reservations);
        stats.setTotalRevenue(total_revenue);

        if (total_trips > 0) {
            stats.setAverageOccupancyRate(total_occupancy / total_trips);
        } else {
            stats.setAverageOccupancyRate(0.0);
        }

        long cities_covered = agencies.stream()
                .filter(agence -> agence.getStatutValidation() == StatutValidation.VALIDEE)
                .map(AgenceVoyage::getVille)
                .distinct()
                .count();
        stats.setCitiesCovered((int) cities_covered);

        // Évolution mensuelle (6 derniers mois)
        Map<String, Integer> reservationsPerMonth = new LinkedHashMap<>();
        Map<String, Double> revenuePerMonth = new LinkedHashMap<>();

        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String monthKey = month.getYear() + "-" + String.format("%02d", month.getMonthValue());
            reservationsPerMonth.put(monthKey, 0);
            revenuePerMonth.put(monthKey, 0.0);
        }

// Répartitions
        Map<String, Integer> reservationsByStatus = new HashMap<>();
        reservationsByStatus.put("EN_ATTENTE", 0);
        reservationsByStatus.put("CONFIRMER", 0);
        reservationsByStatus.put("ANNULER", 0);
        reservationsByStatus.put("VALIDER", 0);

        Map<String, Integer> tripsByStatus = new HashMap<>();
        tripsByStatus.put("EN_ATTENTE", 0);
        tripsByStatus.put("EN_COURS", 0);
        tripsByStatus.put("TERMINE", 0);
        tripsByStatus.put("ANNULE", 0);
        tripsByStatus.put("PUBLIE", 0);

// Par agence
        Map<String, Double> revenueByAgency = new HashMap<>();
        Map<String, Integer> reservationsByAgency = new HashMap<>();

// Dans la boucle existante des agences, ajouter :
        for (AgenceVoyage agence : agencies) {
            if (agence.getStatutValidation() == StatutValidation.VALIDEE) {
                String agencyName = agence.getShortName() != null ? agence.getShortName() : agence.getLongName();
                double agencyRevenue = 0.0;
                int agencyReservations = 0;

                List<LigneVoyage> lignes = ligneVoyageRepository.findByIdAgenceVoyage(agence.getAgencyId());

                for (LigneVoyage ligne : lignes) {
                    Voyage voyage = voyageRepository.findById(ligne.getIdVoyage()).orElse(null);

                    if (voyage != null) {
                        // Trips by status
                        String tripStatus = voyage.getStatusVoyage() != null ? voyage.getStatusVoyage().name() : "EN_ATTENTE";
                        tripsByStatus.put(tripStatus, tripsByStatus.getOrDefault(tripStatus, 0) + 1);

                        List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());

                        for (Reservation reservation : reservations) {
                            agencyReservations++;
                            agencyRevenue += reservation.getPrixTotal();

                            // Reservations by status
                            String resStatus = reservation.getStatutReservation() != null ? reservation.getStatutReservation().name() : "EN_ATTENTE";
                            reservationsByStatus.put(resStatus, reservationsByStatus.getOrDefault(resStatus, 0) + 1);

                            // Par mois
                            if (reservation.getDateReservation() != null) {
                                LocalDate resDate = reservation.getDateReservation().toInstant()
                                        .atZone(ZoneId.systemDefault()).toLocalDate();
                                String monthKey = resDate.getYear() + "-" + String.format("%02d", resDate.getMonthValue());

                                if (reservationsPerMonth.containsKey(monthKey)) {
                                    reservationsPerMonth.put(monthKey, reservationsPerMonth.get(monthKey) + 1);
                                    revenuePerMonth.put(monthKey, revenuePerMonth.get(monthKey) + reservation.getPrixTotal());
                                }
                            }
                        }
                    }
                }

                revenueByAgency.put(agencyName, agencyRevenue);
                reservationsByAgency.put(agencyName, agencyReservations);
            }
        }

// Setter les nouvelles stats
        stats.setReservationsPerMonth(reservationsPerMonth);
        stats.setRevenuePerMonth(revenuePerMonth);
        stats.setReservationsByStatus(reservationsByStatus);
        stats.setTripsByStatus(tripsByStatus);
        stats.setRevenueByAgency(revenueByAgency);
        stats.setReservationsByAgency(reservationsByAgency);

        return stats;
    }

    /**
     * Retrieves comparison statistics for all agencies of an organization
     * Provides individual performance metrics for each agency
     *
     * @param organization_id UUID of the organization
     * @return OrganizationAgenciesComparisonDTO with comparison data
     * @throws ResponseStatusException if organization not found (404)
     */
    public OrganizationAgenciesComparisonDTO getOrganizationAgenciesComparison(UUID organization_id) {
        Organization organization = organizationRepository.findById(organization_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organization with ID " + organization_id + " not found"
                ));

        List<AgenceVoyage> agencies = agenceVoyageRepository.findByOrganisationId(organization_id);

        OrganizationAgenciesComparisonDTO comparison = new OrganizationAgenciesComparisonDTO();
        comparison.setOrganizationId(organization_id);
        comparison.setOrganizationName(organization.getLongName());
        comparison.setTotalAgencies(agencies.size());

        List<AgencyComparisonDTO> agency_comparisons = new ArrayList<>();

        for (AgenceVoyage agence : agencies) {
            AgencyComparisonDTO agency_stats = new AgencyComparisonDTO();

            agency_stats.setAgencyId(agence.getAgencyId());
            agency_stats.setAgencyName(agence.getLongName());
            agency_stats.setShortName(agence.getShortName());
            agency_stats.setVille(agence.getVille());
            agency_stats.setStatutValidation(agence.getStatutValidation());

            if (agence.getStatutValidation() == StatutValidation.VALIDEE) {
                int employees = employeAgenceVoyageRepository.countByAgenceVoyageId(agence.getAgencyId());
                agency_stats.setNumberOfEmployees(employees);

                int drivers = chauffeurAgenceVoyageRepository.countByAgenceVoyageId(agence.getAgencyId());
                agency_stats.setNumberOfDrivers(drivers);

                List<LigneVoyage> lignes = ligneVoyageRepository.findByIdAgenceVoyage(agence.getAgencyId());
                int vehicles = (int) lignes.stream()
                        .map(LigneVoyage::getIdVehicule)
                        .distinct()
                        .count();
                agency_stats.setNumberOfVehicles(vehicles);

                int trips = 0;
                int reservations = 0;
                double revenue = 0.0;
                double total_occupancy = 0.0;

                for (LigneVoyage ligne : lignes) {
                    Voyage voyage = voyageRepository.findById(ligne.getIdVoyage()).orElse(null);

                    if (voyage != null) {
                        trips++;

                        List<Reservation> voyage_reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());
                        reservations += voyage_reservations.size();

                        for (Reservation reservation : voyage_reservations) {
                            revenue += reservation.getPrixTotal();
                        }

                        if (voyage.getNbrPlaceReservable() > 0) {
                            double occupancy = (double) voyage.getNbrPlaceReserve() / voyage.getNbrPlaceReservable();
                            total_occupancy += occupancy;
                        }
                    }
                }

                agency_stats.setNumberOfTrips(trips);
                agency_stats.setNumberOfReservations(reservations);
                agency_stats.setTotalRevenue(revenue);

                if (trips > 0) {
                    agency_stats.setAverageOccupancyRate(total_occupancy / trips);
                } else {
                    agency_stats.setAverageOccupancyRate(0.0);
                }
            } else {
                agency_stats.setNumberOfEmployees(0);
                agency_stats.setNumberOfDrivers(0);
                agency_stats.setNumberOfVehicles(0);
                agency_stats.setNumberOfTrips(0);
                agency_stats.setNumberOfReservations(0);
                agency_stats.setTotalRevenue(0.0);
                agency_stats.setAverageOccupancyRate(0.0);
            }

            agency_comparisons.add(agency_stats);
        }

        comparison.setAgencies(agency_comparisons);

        AgencyComparisonDTO best_performing = agency_comparisons.stream()
                .filter(agency -> agency.getStatutValidation() == StatutValidation.VALIDEE)
                .max(Comparator.comparingDouble(AgencyComparisonDTO::getTotalRevenue))
                .orElse(null);

        if (best_performing != null) {
            comparison.setBestPerformingAgencyId(best_performing.getAgencyId());
            comparison.setBestPerformingAgencyName(best_performing.getAgencyName());
        }

        return comparison;
    }

    /**
     * Retrieves overview statistics for a BSM's city
     * Provides aggregated data for all agencies in the city
     *
     * @param ville City name
     * @return BsmOverviewDTO with city-level statistics
     * @throws ResponseStatusException if no agencies found in city (404)
     */
    public BsmOverviewDTO getBsmOverview(String ville) {
        List<AgenceVoyage> agencies_in_city = agenceVoyageRepository.findByVille(ville,
                org.springframework.data.domain.Pageable.unpaged()).getContent();

        if (agencies_in_city.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No agencies found in city: " + ville
            );
        }

        BsmOverviewDTO overview = new BsmOverviewDTO();
        overview.setVille(ville);
        overview.setTotalAgencies(agencies_in_city.size());

        Map<StatutValidation, Integer> agencies_by_status = new HashMap<>();
        agencies_by_status.put(StatutValidation.EN_ATTENTE, 0);
        agencies_by_status.put(StatutValidation.VALIDEE, 0);
        agencies_by_status.put(StatutValidation.REJETEE, 0);
        agencies_by_status.put(StatutValidation.SUSPENDUE, 0);

        int pending = 0;
        int validated = 0;
        int rejected = 0;

        for (AgenceVoyage agence : agencies_in_city) {
            StatutValidation statut = agence.getStatutValidation();
            if (statut != null) {
                agencies_by_status.put(statut, agencies_by_status.get(statut) + 1);

                if (statut == StatutValidation.EN_ATTENTE) {
                    pending++;
                } else if (statut == StatutValidation.VALIDEE) {
                    validated++;
                } else if (statut == StatutValidation.REJETEE) {
                    rejected++;
                }
            }
        }

        overview.setAgenciesByStatus(agencies_by_status);
        overview.setPendingValidationCount(pending);
        overview.setValidatedAgenciesCount(validated);
        overview.setRejectedAgenciesCount(rejected);

        Set<UUID> unique_organizations = new HashSet<>();
        int total_trips = 0;
        int total_reservations = 0;
        Set<UUID> unique_vehicles = new HashSet<>();
        Set<UUID> unique_drivers = new HashSet<>();
        double total_occupancy = 0.0;
        int occupancy_count = 0;

        for (AgenceVoyage agence : agencies_in_city) {
            unique_organizations.add(agence.getOrganisationId());

            if (agence.getStatutValidation() == StatutValidation.VALIDEE) {
                List<ChauffeurAgenceVoyage> chauffeurs = chauffeurAgenceVoyageRepository
                        .findByAgenceVoyageId(agence.getAgencyId());
                for (ChauffeurAgenceVoyage chauffeur : chauffeurs) {
                    unique_drivers.add(chauffeur.getUserId());
                }

                List<LigneVoyage> lignes = ligneVoyageRepository.findByIdAgenceVoyage(agence.getAgencyId());

                for (LigneVoyage ligne : lignes) {
                    unique_vehicles.add(ligne.getIdVehicule());

                    Voyage voyage = voyageRepository.findById(ligne.getIdVoyage()).orElse(null);

                    if (voyage != null) {
                        total_trips++;

                        List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());
                        total_reservations += reservations.size();

                        if (voyage.getNbrPlaceReservable() > 0) {
                            double occupancy = (double) voyage.getNbrPlaceReserve() / voyage.getNbrPlaceReservable();
                            total_occupancy += occupancy;
                            occupancy_count++;
                        }
                    }
                }
            }
        }

        overview.setTotalOrganizations(unique_organizations.size());
        overview.setTotalTripsInCity(total_trips);
        overview.setTotalReservationsInCity(total_reservations);
        overview.setTotalVehiclesInCity(unique_vehicles.size());
        overview.setTotalDriversInCity(unique_drivers.size());

        if (occupancy_count > 0) {
            overview.setAverageOccupancyRate(total_occupancy / occupancy_count);
        } else {
            overview.setAverageOccupancyRate(0.0);
        }

        // Évolution mensuelle (6 derniers mois)
        Map<String, Integer> reservationsPerMonth = new LinkedHashMap<>();
        Map<String, Double> revenuePerMonth = new LinkedHashMap<>();

        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String monthKey = month.getYear() + "-" + String.format("%02d", month.getMonthValue());
            reservationsPerMonth.put(monthKey, 0);
            revenuePerMonth.put(monthKey, 0.0);
        }

// Répartitions
        Map<String, Integer> reservationsByStatus = new HashMap<>();
        reservationsByStatus.put("EN_ATTENTE", 0);
        reservationsByStatus.put("CONFIRMER", 0);
        reservationsByStatus.put("ANNULER", 0);
        reservationsByStatus.put("VALIDER", 0);

        Map<String, Integer> tripsByStatus = new HashMap<>();
        tripsByStatus.put("EN_ATTENTE", 0);
        tripsByStatus.put("EN_COURS", 0);
        tripsByStatus.put("TERMINE", 0);
        tripsByStatus.put("ANNULE", 0);
        tripsByStatus.put("PUBLIE", 0);

// Par agence
        Map<String, Double> agencyRevenues = new HashMap<>();
        Map<String, Integer> agencyReservations = new HashMap<>();

// Par organisation
        Map<String, Integer> agenciesPerOrganization = new HashMap<>();

        for (AgenceVoyage agence : agencies_in_city) {
            // Compter agences par organisation
            if (agence.getOrganisationId() != null) {
                Organization org = organizationRepository.findById(agence.getOrganisationId()).orElse(null);
                String orgName = org != null ? org.getShortName() : "Inconnue";
                agenciesPerOrganization.put(orgName, agenciesPerOrganization.getOrDefault(orgName, 0) + 1);
            }

            if (agence.getStatutValidation() == StatutValidation.VALIDEE) {
                String agencyName = agence.getShortName() != null ? agence.getShortName() : agence.getLongName();
                double agencyRevenue = 0.0;
                int agencyReservationCount = 0;

                List<LigneVoyage> lignes = ligneVoyageRepository.findByIdAgenceVoyage(agence.getAgencyId());

                for (LigneVoyage ligne : lignes) {
                    Voyage voyage = voyageRepository.findById(ligne.getIdVoyage()).orElse(null);

                    if (voyage != null) {
                        // Trips by status
                        String tripStatus = voyage.getStatusVoyage() != null ? voyage.getStatusVoyage().name() : "EN_ATTENTE";
                        tripsByStatus.put(tripStatus, tripsByStatus.getOrDefault(tripStatus, 0) + 1);

                        List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());

                        for (Reservation reservation : reservations) {
                            agencyReservationCount++;
                            agencyRevenue += reservation.getPrixTotal();

                            // Reservations by status
                            String resStatus = reservation.getStatutReservation() != null ? reservation.getStatutReservation().name() : "EN_ATTENTE";
                            reservationsByStatus.put(resStatus, reservationsByStatus.getOrDefault(resStatus, 0) + 1);

                            // Par mois
                            if (reservation.getDateReservation() != null) {
                                LocalDate resDate = reservation.getDateReservation().toInstant()
                                        .atZone(ZoneId.systemDefault()).toLocalDate();
                                String monthKey = resDate.getYear() + "-" + String.format("%02d", resDate.getMonthValue());

                                if (reservationsPerMonth.containsKey(monthKey)) {
                                    reservationsPerMonth.put(monthKey, reservationsPerMonth.get(monthKey) + 1);
                                    revenuePerMonth.put(monthKey, revenuePerMonth.get(monthKey) + reservation.getPrixTotal());
                                }
                            }
                        }
                    }
                }

                agencyRevenues.put(agencyName, agencyRevenue);
                agencyReservations.put(agencyName, agencyReservationCount);
            }
        }

// Top 5 agences par revenus
        Map<String, Double> topAgenciesByRevenue = agencyRevenues.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

// Top 5 agences par réservations
        Map<String, Integer> topAgenciesByReservations = agencyReservations.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

// Setter les nouvelles stats
        overview.setReservationsPerMonth(reservationsPerMonth);
        overview.setRevenuePerMonth(revenuePerMonth);
        overview.setReservationsByStatus(reservationsByStatus);
        overview.setTripsByStatus(tripsByStatus);
        overview.setTopAgenciesByRevenue(topAgenciesByRevenue);
        overview.setTopAgenciesByReservations(topAgenciesByReservations);
        overview.setAgenciesPerOrganization(agenciesPerOrganization);

        return overview;
    }

    public VoyageStatisticsDTO getVoyageStatistics(UUID voyageId) {
        // Vérifier que le voyage existe
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voyage non trouvé"));

        VoyageStatisticsDTO stats = new VoyageStatisticsDTO();

        // === INFOS GÉNÉRALES ===
        stats.setVoyageId(voyage.getIdVoyage());
        stats.setTitre(voyage.getTitre());
        stats.setDescription(voyage.getDescription());
        stats.setLieuDepart(voyage.getLieuDepart());
        stats.setLieuArrive(voyage.getLieuArrive());
        stats.setPointDepart(voyage.getPointDeDepart());
        stats.setPointArrivee(voyage.getPointArrivee());
        stats.setDateDepartPrev(voyage.getDateDepartPrev());
        stats.setDateDepartEffectif(voyage.getDateDepartEffectif());
        stats.setStatutVoyage(voyage.getStatusVoyage());

        // Récupérer la ligne de voyage pour les infos complémentaires
        LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyageId);

        if (ligneVoyage != null) {
            // Agence
            AgenceVoyage agence = agenceVoyageRepository.findById(ligneVoyage.getIdAgenceVoyage()).orElse(null);
            if (agence != null) {
                stats.setNomAgence(agence.getLongName());
            }

            // Classe de voyage
            ClassVoyage classeVoyage = classVoyageRepository.findById(ligneVoyage.getIdClassVoyage()).orElse(null);
            if (classeVoyage != null) {
                stats.setNomClasseVoyage(classeVoyage.getNom());
                stats.setPrix(classeVoyage.getPrix());
            }

            // Chauffeur
            if (ligneVoyage.getIdChauffeur() != null) {
                ChauffeurAgenceVoyage chauffeur = chauffeurAgenceVoyageRepository.findById(ligneVoyage.getIdChauffeur()).orElse(null);
                if (chauffeur != null) {
                    User chauffeurUser = userRepository.findById(chauffeur.getUserId()).orElse(null);
                    if (chauffeurUser != null) {
                        stats.setNomChauffeur(chauffeurUser.getPrenom() + " " + chauffeurUser.getNom());
                    }
                }
            }

            // Véhicule
            Vehicule vehicule = vehiculeRepository.findById(ligneVoyage.getIdVehicule()).orElse(null);
            if (vehicule != null) {
                stats.setVehiculeNom(vehicule.getNom());
                stats.setVehiculePlaque(vehicule.getPlaqueMatricule());
            }
        }

        // === STATS GÉNÉRALES ===
        stats.setTotalPlaces(voyage.getNbrPlaceReservable() + voyage.getNbrPlaceReserve());
        stats.setPlacesReservees(voyage.getNbrPlaceReserve());
        stats.setPlacesConfirmees(voyage.getNbrPlaceConfirm());
        stats.setPlacesRestantes(voyage.getNbrPlaceRestante());

        int totalPlaces = voyage.getNbrPlaceReservable() + voyage.getNbrPlaceReserve();
        stats.setTauxOccupation(totalPlaces > 0 ? (double) voyage.getNbrPlaceReserve() / totalPlaces : 0.0);

        // Récupérer les réservations
        List<Reservation> reservations = reservationRepository.findByIdVoyage(voyageId);
        stats.setTotalReservations(reservations.size());

        double revenusTotaux = 0.0;
        double revenusConfirmes = 0.0;
        int totalPassagers = 0;

        // === STATS POUR GRAPHIQUES ===
        Map<String, Integer> reservationsByStatus = new HashMap<>();
        for (StatutReservation statut : StatutReservation.values()) {
            reservationsByStatus.put(statut.name(), 0);
        }

        Map<String, Integer> passengersByGender = new HashMap<>();
        passengersByGender.put("MALE", 0);
        passengersByGender.put("FEMALE", 0);
        passengersByGender.put("OTHER", 0);

        Map<String, Integer> passengersByAgeGroup = new LinkedHashMap<>();
        passengersByAgeGroup.put("0-17", 0);
        passengersByAgeGroup.put("18-25", 0);
        passengersByAgeGroup.put("26-35", 0);
        passengersByAgeGroup.put("36-50", 0);
        passengersByAgeGroup.put("51-65", 0);
        passengersByAgeGroup.put("65+", 0);

        Map<String, Integer> baggageDistribution = new LinkedHashMap<>();
        baggageDistribution.put("0", 0);
        baggageDistribution.put("1", 0);
        baggageDistribution.put("2", 0);
        baggageDistribution.put("3+", 0);

        Map<String, Integer> reservationsPerDay = new LinkedHashMap<>();
        Map<String, Double> revenuePerDay = new LinkedHashMap<>();

        for (Reservation reservation : reservations) {
            revenusTotaux += reservation.getPrixTotal();

            // Réservations par statut
            String statut = reservation.getStatutReservation().name();
            reservationsByStatus.put(statut, reservationsByStatus.get(statut) + 1);

            if (reservation.getStatutReservation() == StatutReservation.CONFIRMER ||
                    reservation.getStatutReservation() == StatutReservation.VALIDER) {
                revenusConfirmes += reservation.getPrixTotal();
            }

            // Par jour
            if (reservation.getDateReservation() != null) {
                String dayKey = reservation.getDateReservation().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate().toString();
                reservationsPerDay.put(dayKey, reservationsPerDay.getOrDefault(dayKey, 0) + 1);
                revenuePerDay.put(dayKey, revenuePerDay.getOrDefault(dayKey, 0.0) + reservation.getPrixTotal());
            }

            // Récupérer les passagers de cette réservation
            List<Passager> passagers = passagerRepository.findAllByIdReservation(reservation.getIdReservation());
            totalPassagers += passagers.size();

            for (Passager passager : passagers) {
                // Par genre
                if (passager.getGenre() != null) {
                    String genre = passager.getGenre();
                    passengersByGender.put(genre, passengersByGender.getOrDefault(genre, 0) + 1);
                }

                // Par tranche d'âge
                if (passager.getAge() != 0) {
                    int age = passager.getAge();
                    String ageGroup;
                    if (age < 18) ageGroup = "0-17";
                    else if (age <= 25) ageGroup = "18-25";
                    else if (age <= 35) ageGroup = "26-35";
                    else if (age <= 50) ageGroup = "36-50";
                    else if (age <= 65) ageGroup = "51-65";
                    else ageGroup = "65+";
                    passengersByAgeGroup.put(ageGroup, passengersByAgeGroup.get(ageGroup) + 1);
                }

                // Par nombre de bagages
                if (passager.getNbrBaggage() != 0) {
                    int nbBaggage = passager.getNbrBaggage();
                    String baggageKey;
                    if (nbBaggage == 0) baggageKey = "0";
                    else if (nbBaggage == 1) baggageKey = "1";
                    else if (nbBaggage == 2) baggageKey = "2";
                    else baggageKey = "3+";
                    baggageDistribution.put(baggageKey, baggageDistribution.get(baggageKey) + 1);
                }
            }
        }

        stats.setTotalPassagers(totalPassagers);
        stats.setRevenusTotaux(revenusTotaux);
        stats.setRevenusConfirmes(revenusConfirmes);
        stats.setReservationsByStatus(reservationsByStatus);
        stats.setPassengersByGender(passengersByGender);
        stats.setPassengersByAgeGroup(passengersByAgeGroup);
        stats.setReservationsPerDay(reservationsPerDay);
        stats.setRevenuePerDay(revenuePerDay);
        stats.setBaggageDistribution(baggageDistribution);

        return stats;
    }
}