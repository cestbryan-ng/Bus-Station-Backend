package com.enspy26.gi.database_agence_voyage.dto.statistics;

import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Data Transfer Object for individual agency performance metrics
 * Used in organization-level comparison of agencies
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
public class AgencyComparisonDTO {

    @JsonProperty("agency_id")
    private UUID agencyId;

    @JsonProperty("agency_name")
    private String agencyName;

    @JsonProperty("short_name")
    private String shortName;

    private String ville;

    @JsonProperty("statut_validation")
    private StatutValidation statutValidation;

    @JsonProperty("number_of_employees")
    private int numberOfEmployees;

    @JsonProperty("number_of_drivers")
    private int numberOfDrivers;

    @JsonProperty("number_of_vehicles")
    private int numberOfVehicles;

    @JsonProperty("number_of_trips")
    private int numberOfTrips;

    @JsonProperty("number_of_reservations")
    private int numberOfReservations;

    @JsonProperty("total_revenue")
    private double totalRevenue;

    @JsonProperty("average_occupancy_rate")
    private double averageOccupancyRate;
}