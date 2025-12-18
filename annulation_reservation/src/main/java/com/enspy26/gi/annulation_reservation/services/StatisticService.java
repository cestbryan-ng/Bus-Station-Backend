package com.enspy26.gi.annulation_reservation.services;

import com.enspy26.gi.database_agence_voyage.dto.statistics.AgenceStatisticsDTO;
import com.enspy26.gi.database_agence_voyage.dto.statistics.AgenceEvolutionDTO;
import com.enspy26.gi.database_agence_voyage.dto.statistics.EvolutionData;
import com.enspy26.gi.database_agence_voyage.enums.BusinessActorType;
import com.enspy26.gi.database_agence_voyage.models.*;
import com.enspy26.gi.database_agence_voyage.repositories.*;
import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;
import com.enspy26.gi.database_agence_voyage.enums.StatutReservation;
import com.enspy26.gi.database_agence_voyage.dto.statistics.OrganizationStatisticsDTO;
import com.enspy26.gi.database_agence_voyage.models.Organization;
import com.enspy26.gi.database_agence_voyage.repositories.OrganizationRepository;
import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.enspy26.gi.database_agence_voyage.dto.statistics.AgencyComparisonDTO;
import com.enspy26.gi.database_agence_voyage.dto.statistics.OrganizationAgenciesComparisonDTO;
import java.util.ArrayList;
import java.util.Comparator;
import com.enspy26.gi.database_agence_voyage.dto.statistics.BsmOverviewDTO;
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

                total_employees += employeAgenceVoyageRepository.countByAgenceVoyageId(agence.getAgencyId());

                total_drivers += chauffeurAgenceVoyageRepository.countByAgenceVoyageId(agence.getAgencyId());

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

        return overview;
    }
}