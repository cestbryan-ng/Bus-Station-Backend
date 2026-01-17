package com.enspy26.gi.database_agence_voyage.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgenceStatisticsDTO {
    // Pour graphique camembert - Répartition des revenus par classe de voyage
    @JsonProperty("revenue_by_class")
    private Map<String, Double> revenueByClass;

    // Pour graphique barres - Top destinations (villes d'arrivée)
    @JsonProperty("top_destinations")
    private Map<String, Integer> topDestinations;

    // Pour graphique barres - Top origines (villes de départ)
    @JsonProperty("top_origins")
    private Map<String, Integer> topOrigins;

    // Pour graphique camembert - Réservations par jour de la semaine
    @JsonProperty("reservations_by_day_of_week")
    private Map<String, Integer> reservationsByDayOfWeek;

    // Pour graphique barres - Performance des chauffeurs (nombre de voyages)
    @JsonProperty("trips_by_driver")
    private Map<String, Integer> tripsByDriver;
    private long nombreEmployes;
    private long nombreChauffeurs;
    private long nombreVoyages;
    private Map<String, Long> voyagesParStatut;
    private long nombreReservations;
    private Map<String, Long> reservationsParStatut;
    private double revenus;
    private long nouveauxUtilisateurs;
    private double tauxOccupation; // Pourcentage de places réservées
}
