package com.enspy26.gi.database_agence_voyage.dto.statistics;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutVoyage;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VoyageStatisticsDTO {

    // === INFOS GÉNÉRALES ===
    @JsonProperty("voyage_id")
    private UUID voyageId;

    private String titre;
    private String description;

    @JsonProperty("lieu_depart")
    private String lieuDepart;

    @JsonProperty("lieu_arrive")
    private String lieuArrive;

    @JsonProperty("point_depart")
    private String pointDepart;

    @JsonProperty("point_arrivee")
    private String pointArrivee;

    @JsonProperty("date_depart_prev")
    private Date dateDepartPrev;

    @JsonProperty("date_depart_effectif")
    private Date dateDepartEffectif;

    @JsonProperty("statut_voyage")
    private StatutVoyage statutVoyage;

    @JsonProperty("nom_agence")
    private String nomAgence;

    @JsonProperty("nom_classe_voyage")
    private String nomClasseVoyage;

    private double prix;

    @JsonProperty("nom_chauffeur")
    private String nomChauffeur;

    @JsonProperty("vehicule_nom")
    private String vehiculeNom;

    @JsonProperty("vehicule_plaque")
    private String vehiculePlaque;

    // === STATS GÉNÉRALES ===
    @JsonProperty("total_places")
    private int totalPlaces;

    @JsonProperty("places_reservees")
    private int placesReservees;

    @JsonProperty("places_confirmees")
    private int placesConfirmees;

    @JsonProperty("places_restantes")
    private int placesRestantes;

    @JsonProperty("taux_occupation")
    private double tauxOccupation;

    @JsonProperty("total_reservations")
    private int totalReservations;

    @JsonProperty("total_passagers")
    private int totalPassagers;

    @JsonProperty("revenus_totaux")
    private double revenusTotaux;

    @JsonProperty("revenus_confirmes")
    private double revenusConfirmes;

    // === STATS POUR GRAPHIQUES ===

    // Camembert - Répartition des réservations par statut
    @JsonProperty("reservations_by_status")
    private Map<String, Integer> reservationsByStatus;

    // Camembert - Répartition par genre des passagers
    @JsonProperty("passengers_by_gender")
    private Map<String, Integer> passengersByGender;

    // Barres - Répartition par tranche d'âge
    @JsonProperty("passengers_by_age_group")
    private Map<String, Integer> passengersByAgeGroup;

    // Ligne - Évolution des réservations dans le temps (par jour)
    @JsonProperty("reservations_per_day")
    private Map<String, Integer> reservationsPerDay;

    // Ligne - Évolution des revenus dans le temps (par jour)
    @JsonProperty("revenue_per_day")
    private Map<String, Double> revenuePerDay;

    // Camembert - Répartition des bagages
    @JsonProperty("baggage_distribution")
    private Map<String, Integer> baggageDistribution;

    // Barres - Top villes d'origine des passagers (si disponible)
    @JsonProperty("passengers_origin_city")
    private Map<String, Integer> passengersOriginCity;
}