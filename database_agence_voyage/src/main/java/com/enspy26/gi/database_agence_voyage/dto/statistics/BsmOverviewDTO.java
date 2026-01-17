package com.enspy26.gi.database_agence_voyage.dto.statistics;

import java.util.Map;

import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Data Transfer Object for BSM overview statistics
 * Contains statistics for all agencies in a BSM's city
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
public class BsmOverviewDTO {
    // Pour graphique linéaire - Évolution mensuelle dans la ville
    @JsonProperty("reservations_per_month")
    private Map<String, Integer> reservationsPerMonth;

    @JsonProperty("revenue_per_month")
    private Map<String, Double> revenuePerMonth;

    // Pour graphique camembert/donut - Répartitions
    @JsonProperty("reservations_by_status")
    private Map<String, Integer> reservationsByStatus;

    @JsonProperty("trips_by_status")
    private Map<String, Integer> tripsByStatus;

    // Pour graphique barres - Top agences de la ville
    @JsonProperty("top_agencies_by_revenue")
    private Map<String, Double> topAgenciesByRevenue;

    @JsonProperty("top_agencies_by_reservations")
    private Map<String, Integer> topAgenciesByReservations;

    // Pour graphique barres - Répartition par organisation
    @JsonProperty("agencies_per_organization")
    private Map<String, Integer> agenciesPerOrganization;

    private String ville;

    @JsonProperty("total_agencies")
    private int totalAgencies;

    @JsonProperty("agencies_by_status")
    private Map<StatutValidation, Integer> agenciesByStatus;

    @JsonProperty("pending_validation_count")
    private int pendingValidationCount;

    @JsonProperty("validated_agencies_count")
    private int validatedAgenciesCount;

    @JsonProperty("rejected_agencies_count")
    private int rejectedAgenciesCount;

    @JsonProperty("total_organizations")
    private int totalOrganizations;

    @JsonProperty("total_trips_in_city")
    private int totalTripsInCity;

    @JsonProperty("total_reservations_in_city")
    private int totalReservationsInCity;

    @JsonProperty("total_vehicles_in_city")
    private int totalVehiclesInCity;

    @JsonProperty("total_drivers_in_city")
    private int totalDriversInCity;

    @JsonProperty("average_occupancy_rate")
    private double averageOccupancyRate;
}