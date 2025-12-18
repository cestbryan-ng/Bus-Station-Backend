package com.enspy26.gi.database_agence_voyage.dto.statistics;

import java.util.Map;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Data Transfer Object for organization statistics
 * Contains consolidated statistics across all agencies of an organization
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
public class OrganizationStatisticsDTO {

    @JsonProperty("organization_id")
    private UUID organizationId;

    @JsonProperty("organization_name")
    private String organizationName;

    @JsonProperty("total_agencies")
    private int totalAgencies;

    @JsonProperty("agencies_by_status")
    private Map<StatutValidation, Integer> agenciesByStatus;

    @JsonProperty("total_employees")
    private int totalEmployees;

    @JsonProperty("total_drivers")
    private int totalDrivers;

    @JsonProperty("total_vehicles")
    private int totalVehicles;

    @JsonProperty("total_trips")
    private int totalTrips;

    @JsonProperty("total_reservations")
    private int totalReservations;

    @JsonProperty("total_revenue")
    private double totalRevenue;

    @JsonProperty("average_occupancy_rate")
    private double averageOccupancyRate;

    @JsonProperty("cities_covered")
    private int citiesCovered;
}