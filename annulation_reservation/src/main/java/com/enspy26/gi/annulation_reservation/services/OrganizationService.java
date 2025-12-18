package com.enspy26.gi.annulation_reservation.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.repositories.AgenceVoyageRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.enspy26.gi.database_agence_voyage.dto.CreateOrganizationRequest;
import com.enspy26.gi.database_agence_voyage.dto.OrganizationDto;
import com.enspy26.gi.database_agence_voyage.dto.organization.OrganizationDetailsDTO;
import com.enspy26.gi.database_agence_voyage.dto.organization.UpdateOrganizationRequest;
import com.enspy26.gi.database_agence_voyage.models.Organization;
import com.enspy26.gi.database_agence_voyage.repositories.OrganizationRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for organization management operations
 * Handles business logic for organizations
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final AgenceVoyageRepository agenceVoyageRepository;

    /**
     * Retrieves all agencies belonging to a specific organization
     *
     * @param organization_id UUID of the organization
     * @return List of AgenceVoyage entities
     */
    public List<AgenceVoyage> findAllAgencies(UUID organization_id) {
        return agenceVoyageRepository.findByOrganisationId(organization_id);
    }

    /**
     * Retrieves detailed information about a specific organization
     *
     * @param organization_id UUID of the organization to retrieve
     * @return OrganizationDetailsDTO containing complete organization information
     * @throws ResponseStatusException if organization not found (404)
     */
    public OrganizationDetailsDTO getOrganizationById(UUID organization_id) {
        Organization organization = organizationRepository.findById(organization_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organization with ID " + organization_id + " not found"
                ));

        return OrganizationDetailsDTO.fromEntity(organization);
    }

    /**
     * Creates a new organization in the system
     *
     * @param request CreateOrganizationRequest containing organization data
     * @return OrganizationDto of the created organization
     */
    public OrganizationDto createOrganization(CreateOrganizationRequest request) {
        Organization organization = new Organization();

        // Set basic fields
        organization.setId(UUID.randomUUID());
        organization.setOrganizationId(UUID.randomUUID());
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());

        // Set fields from request
        organization.setLongName(request.getLongName());
        organization.setShortName(request.getShortName());
        organization.setEmail(request.getEmail());
        organization.setDescription(request.getDescription());
        organization.setLogoUrl(request.getLogoUrl());
        organization.setWebsiteUrl(request.getWebsiteUrl());
        organization.setSocialNetwork(request.getSocialNetwork());
        organization.setLegalForm(request.getLegalForm());
        organization.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        organization.setTaxNumber(request.getTaxNumber());
        organization.setCapitalShare(request.getCapitalShare());
        organization.setRegistrationDate(request.getRegistrationDate());
        organization.setCeoName(request.getCeoName());
        organization.setYearFounded(request.getYearFounded());
        organization.setBusinessDomains(request.getBusinessDomains());
        organization.setKeywords(request.getKeywords());

        // Set default values
        organization.setIndividualBusiness(false);
        organization.setActive(true);
        organization.setStatus("ACTIVE");

        Organization saved_organization = organizationRepository.save(organization);
        return OrganizationDto.fromEntity(saved_organization);
    }

    /**
     * Retrieves all organizations in the system
     *
     * @return List of OrganizationDto
     */
    public List<OrganizationDto> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(OrganizationDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing organization
     * Only updates fields that are provided in the request (non-null)
     *
     * @param organization_id UUID of the organization to update
     * @param request UpdateOrganizationRequest containing fields to update
     * @return OrganizationDetailsDTO with updated organization information
     * @throws ResponseStatusException if organization not found (404)
     */
    public OrganizationDetailsDTO updateOrganization(UUID organization_id, UpdateOrganizationRequest request) {
        Organization organization = organizationRepository.findById(organization_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organization with ID " + organization_id + " not found"
                ));

        if (request.getLongName() != null) {
            organization.setLongName(request.getLongName());
        }

        if (request.getShortName() != null) {
            organization.setShortName(request.getShortName());
        }

        if (request.getEmail() != null) {
            organization.setEmail(request.getEmail());
        }

        if (request.getDescription() != null) {
            organization.setDescription(request.getDescription());
        }

        if (request.getLogoUrl() != null) {
            organization.setLogoUrl(request.getLogoUrl());
        }

        if (request.getWebsiteUrl() != null) {
            organization.setWebsiteUrl(request.getWebsiteUrl());
        }

        if (request.getSocialNetwork() != null) {
            organization.setSocialNetwork(request.getSocialNetwork());
        }

        if (request.getLegalForm() != null) {
            organization.setLegalForm(request.getLegalForm());
        }

        if (request.getBusinessRegistrationNumber() != null) {
            organization.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        }

        if (request.getTaxNumber() != null) {
            organization.setTaxNumber(request.getTaxNumber());
        }

        if (request.getCapitalShare() != null) {
            organization.setCapitalShare(request.getCapitalShare());
        }

        if (request.getRegistrationDate() != null) {
            organization.setRegistrationDate(request.getRegistrationDate());
        }

        if (request.getCeoName() != null) {
            organization.setCeoName(request.getCeoName());
        }

        if (request.getYearFounded() != null) {
            organization.setYearFounded(request.getYearFounded());
        }

        if (request.getBusinessDomains() != null) {
            organization.setBusinessDomains(request.getBusinessDomains());
        }

        if (request.getKeywords() != null) {
            organization.setKeywords(request.getKeywords());
        }

        organization.setUpdatedAt(LocalDateTime.now());

        Organization updated_organization = organizationRepository.save(organization);
        return OrganizationDetailsDTO.fromEntity(updated_organization);
    }

    /**
     * Deletes an organization (soft delete)
     * Sets the deletedAt timestamp instead of physically removing the record
     * Also checks if organization has any active agencies
     *
     * @param organization_id UUID of the organization to delete
     * @throws ResponseStatusException if organization not found (404)
     * @throws ResponseStatusException if organization has active agencies (409)
     */
    public void deleteOrganization(UUID organization_id) {
        Organization organization = organizationRepository.findById(organization_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organization with ID " + organization_id + " not found"
                ));

        List<AgenceVoyage> agencies = agenceVoyageRepository.findByOrganisationId(organization_id);
        if (!agencies.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete organization with " + agencies.size() + " active agencies. " +
                            "Please delete or transfer all agencies first."
            );
        }

        organization.setDeletedAt(LocalDateTime.now());
        organization.setActive(false);
        organization.setStatus("DELETED");
        organization.setUpdatedAt(LocalDateTime.now());

        organizationRepository.save(organization);
    }
}