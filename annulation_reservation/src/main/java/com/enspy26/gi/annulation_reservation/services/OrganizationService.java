package com.enspy26.gi.annulation_reservation.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.repositories.AgenceVoyageRepository;
import org.springframework.stereotype.Service;

import com.enspy26.gi.database_agence_voyage.dto.CreateOrganizationRequest;
import com.enspy26.gi.database_agence_voyage.dto.OrganizationDto;
import com.enspy26.gi.database_agence_voyage.models.Organization;
import com.enspy26.gi.database_agence_voyage.repositories.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationService {

  private final OrganizationRepository organizationRepository;
  private final AgenceVoyageRepository agenceVoyageRepository;

  public List<AgenceVoyage> findAllAgencies(UUID organizationId) {
    return agenceVoyageRepository.findByOrganisationId(organizationId);
  }

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
    organization.setBusinessDomains(request.getBusinessDomains());
    organization.setLogoUrl(request.getLogoUrl());
    organization.setLegalForm(request.getLegalForm());
    organization.setWebsiteUrl(request.getWebsiteUrl());
    organization.setSocialNetwork(request.getSocialNetwork());
    organization.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
    organization.setTaxNumber(request.getTaxNumber());
    organization.setCapitalShare(request.getCapitalShare());
    organization.setRegistrationDate(request.getRegistrationDate());
    organization.setCeoName(request.getCeoName());
    organization.setYearFounded(request.getYearFounded());
    organization.setKeywords(request.getKeywords());

    // Set default values
    organization.setActive(true);
    organization.setStatus("ACTIVE");
    organization.setIndividualBusiness(false);

    Organization savedOrganization = organizationRepository.save(organization);
    return convertToDto(savedOrganization);
  }

  private OrganizationDto convertToDto(Organization organization) {
    OrganizationDto dto = new OrganizationDto();

    dto.setCreatedAt(organization.getCreatedAt());
    dto.setUpdatedAt(organization.getUpdatedAt());
    dto.setDeletedAt(organization.getDeletedAt());
    dto.setCreatedBy(organization.getCreatedBy());
    dto.setUpdatedBy(organization.getUpdatedBy());
    dto.setOrganizationId(organization.getOrganizationId());
    dto.setBusinessDomains(organization.getBusinessDomains());
    dto.setEmail(organization.getEmail());
    dto.setShortName(organization.getShortName());
    dto.setLongName(organization.getLongName());
    dto.setDescription(organization.getDescription());
    dto.setLogoUrl(organization.getLogoUrl());
    dto.setIndividualBusiness(organization.isIndividualBusiness());
    dto.setLegalForm(organization.getLegalForm());
    dto.setActive(organization.isActive());
    dto.setWebsiteUrl(organization.getWebsiteUrl());
    dto.setSocialNetwork(organization.getSocialNetwork());
    dto.setBusinessRegistrationNumber(organization.getBusinessRegistrationNumber());
    dto.setTaxNumber(organization.getTaxNumber());
    dto.setCapitalShare(organization.getCapitalShare());
    dto.setRegistrationDate(organization.getRegistrationDate());
    dto.setCeoName(organization.getCeoName());
    dto.setYearFounded(organization.getYearFounded());
    dto.setKeywords(organization.getKeywords());
    dto.setStatus(organization.getStatus());

    return dto;
  }
}
