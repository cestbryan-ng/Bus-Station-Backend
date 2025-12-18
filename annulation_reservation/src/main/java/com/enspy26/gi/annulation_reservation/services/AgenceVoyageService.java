package com.enspy26.gi.annulation_reservation.services;

import java.util.UUID;
import java.time.LocalDateTime;

import com.enspy26.gi.database_agence_voyage.dto.agence.*;
import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import com.enspy26.gi.database_agence_voyage.repositories.UserRepository;
import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enspy26.gi.database_agence_voyage.dto.agence.AgenceDetailsDTO;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.repositories.AgenceVoyageRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for travel agency operations
 * Handles business logic for agencies
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Service
@RequiredArgsConstructor
public class AgenceVoyageService {

    private final AgenceVoyageRepository agenceVoyageRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves all agencies with optional filters
     * Supports pagination and sorting
     *
     * @param ville Optional city filter
     * @param organisation_id Optional organization filter
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sort_by Field to sort by (default: longName)
     * @param sort_order Sort order: asc or desc (default: asc)
     * @return Page of AgencePreviewDTO
     */
    public Page<AgencePreviewDTO> getAllAgencies(
            String ville,
            UUID organisation_id,
            int page,
            int size,
            String sort_by,
            String sort_order
    ) {
        Sort.Direction direction = sort_order.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort_by));

        Page<AgenceVoyage> agences;

        if (ville != null && organisation_id != null) {
            agences = agenceVoyageRepository.findAll(pageable)
                    .map(agence -> {
                        if (agence.getVille().equals(ville) &&
                                agence.getOrganisationId().equals(organisation_id)) {
                            return agence;
                        }
                        return null;
                    });
        } else if (ville != null) {
            agences = agenceVoyageRepository.findByVille(ville, pageable);
        } else if (organisation_id != null) {
            agences = agenceVoyageRepository.findByOrganisationId(organisation_id, pageable);
        } else {
            agences = agenceVoyageRepository.findAll(pageable);
        }

        return agences.map(AgencePreviewDTO::fromEntity);
    }

    /**
     * Retrieves a specific agency by ID
     *
     * @param agency_id UUID of the agency
     * @return AgenceVoyage entity
     * @throws ResponseStatusException if agency not found (404)
     */
    public AgenceVoyage findAgenceByChefAgenceId(UUID agency_id) {
        return agenceVoyageRepository.findById(agency_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agency with ID " + agency_id + " not found"
                ));
    }

    /**
     * Retrieves detailed information about a specific agency
     *
     * @param agency_id UUID of the agency to retrieve
     * @return AgenceDetailsDTO containing complete agency information
     * @throws ResponseStatusException if agency not found (404)
     */
    public AgenceDetailsDTO getAgencyById(UUID agency_id) {
        AgenceVoyage agence = agenceVoyageRepository.findById(agency_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agency with ID " + agency_id + " not found"
                ));

        return AgenceDetailsDTO.fromEntity(agence);
    }

    /**
     * Retrieves all agencies pending validation
     * Optionally filtered by city for BSM
     *
     * @param ville Optional city filter
     * @param page Page number
     * @param size Page size
     * @return Page of AgencePreviewDTO with EN_ATTENTE status
     */
    public Page<AgencePreviewDTO> getPendingValidationAgencies(String ville, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "longName"));

        Page<AgenceVoyage> agences;

        if (ville != null) {
            agences = agenceVoyageRepository.findByStatutValidationAndVille(
                    StatutValidation.EN_ATTENTE,
                    ville,
                    pageable
            );
        } else {
            agences = agenceVoyageRepository.findByStatutValidation(
                    StatutValidation.EN_ATTENTE,
                    pageable
            );
        }

        return agences.map(AgencePreviewDTO::fromEntity);
    }

    /**
     * Validates an agency (BSM approval)
     * Changes status from EN_ATTENTE to VALIDEE
     *
     * @param agency_id UUID of the agency to validate
     * @param request ValidateAgenceRequest containing BSM ID
     * @return AgenceDetailsDTO with updated validation information
     * @throws ResponseStatusException if agency not found (404)
     * @throws ResponseStatusException if agency not in EN_ATTENTE status (400)
     * @throws ResponseStatusException if BSM not found (404)
     */
    public AgenceDetailsDTO validateAgency(UUID agency_id, ValidateAgenceRequest request) {
        AgenceVoyage agence = agenceVoyageRepository.findById(agency_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agency with ID " + agency_id + " not found"
                ));

        if (agence.getStatutValidation() != StatutValidation.EN_ATTENTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Agency can only be validated if status is EN_ATTENTE. Current status: " +
                            agence.getStatutValidation()
            );
        }

        if (!userRepository.existsById(request.getBsm_id())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "BSM with ID " + request.getBsm_id() + " not found"
            );
        }

        agence.setStatutValidation(StatutValidation.VALIDEE);
        agence.setBsmValidatorId(request.getBsm_id());
        agence.setDateValidation(LocalDateTime.now());
        agence.setMotifRejet(null);

        AgenceVoyage validated_agence = agenceVoyageRepository.save(agence);

        return AgenceDetailsDTO.fromEntity(validated_agence);
    }

    /**
     * Rejects an agency (BSM denial)
     * Changes status from EN_ATTENTE to REJETEE with rejection reason
     *
     * @param agency_id UUID of the agency to reject
     * @param request RejectAgenceRequest containing BSM ID and rejection reason
     * @return AgenceDetailsDTO with updated rejection information
     * @throws ResponseStatusException if agency not found (404)
     * @throws ResponseStatusException if agency not in EN_ATTENTE status (400)
     * @throws ResponseStatusException if BSM not found (404)
     */
    public AgenceDetailsDTO rejectAgency(UUID agency_id, RejectAgenceRequest request) {
        AgenceVoyage agence = agenceVoyageRepository.findById(agency_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agency with ID " + agency_id + " not found"
                ));

        if (agence.getStatutValidation() != StatutValidation.EN_ATTENTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Agency can only be rejected if status is EN_ATTENTE. Current status: " +
                            agence.getStatutValidation()
            );
        }

        if (!userRepository.existsById(request.getBsm_id())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "BSM with ID " + request.getBsm_id() + " not found"
            );
        }

        agence.setStatutValidation(StatutValidation.REJETEE);
        agence.setBsmValidatorId(request.getBsm_id());
        agence.setDateValidation(LocalDateTime.now());
        agence.setMotifRejet(request.getMotif_rejet());

        AgenceVoyage rejected_agence = agenceVoyageRepository.save(agence);

        return AgenceDetailsDTO.fromEntity(rejected_agence);
    }

    /**
     * Retrieves all agencies in a specific city
     * Optionally filtered by validation status
     *
     * @param ville City name
     * @param statut_validation Optional validation status filter
     * @param page Page number
     * @param size Page size
     * @param sort_by Field to sort by
     * @param sort_order Sort order (asc or desc)
     * @return Page of AgencePreviewDTO in the specified city
     */
    public Page<AgencePreviewDTO> getAgenciesByCity(
            String ville,
            StatutValidation statut_validation,
            int page,
            int size,
            String sort_by,
            String sort_order
    ) {
        Sort.Direction direction = sort_order.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort_by));

        Page<AgenceVoyage> agences;

        if (statut_validation != null) {
            agences = agenceVoyageRepository.findByStatutValidationAndVille(
                    statut_validation,
                    ville,
                    pageable
            );
        } else {
            agences = agenceVoyageRepository.findByVille(ville, pageable);
        }

        return agences.map(AgencePreviewDTO::fromEntity);
    }

    /**
     * Retrieves all validated agencies
     * Returns only agencies with VALIDEE status
     * Optionally filtered by city or organization
     *
     * @param ville Optional city filter
     * @param organisation_id Optional organization filter
     * @param page Page number
     * @param size Page size
     * @param sort_by Field to sort by
     * @param sort_order Sort order (asc or desc)
     * @return Page of AgencePreviewDTO with VALIDEE status
     */
    public Page<AgencePreviewDTO> getValidatedAgencies(
            String ville,
            UUID organisation_id,
            int page,
            int size,
            String sort_by,
            String sort_order
    ) {
        Sort.Direction direction = sort_order.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort_by));

        Page<AgenceVoyage> agences;

        if (ville != null && organisation_id != null) {
            agences = agenceVoyageRepository.findAll(pageable)
                    .map(agence -> {
                        if (agence.getStatutValidation() == StatutValidation.VALIDEE &&
                                agence.getVille().equals(ville) &&
                                agence.getOrganisationId().equals(organisation_id)) {
                            return agence;
                        }
                        return null;
                    });
        } else if (ville != null) {
            agences = agenceVoyageRepository.findByStatutValidationAndVille(
                    StatutValidation.VALIDEE,
                    ville,
                    pageable
            );
        } else if (organisation_id != null) {
            agences = agenceVoyageRepository.findByOrganisationId(organisation_id, pageable)
                    .map(agence -> {
                        if (agence.getStatutValidation() == StatutValidation.VALIDEE) {
                            return agence;
                        }
                        return null;
                    });
        } else {
            agences = agenceVoyageRepository.findByStatutValidation(
                    StatutValidation.VALIDEE,
                    pageable
            );
        }

        return agences.map(AgencePreviewDTO::fromEntity);
    }
}