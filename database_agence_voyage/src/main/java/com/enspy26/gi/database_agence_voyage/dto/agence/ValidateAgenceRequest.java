package com.enspy26.gi.database_agence_voyage.dto.agence;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for agency validation request
 * Contains BSM information who is validating the agency
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateAgenceRequest {

    @NotNull(message = "BSM ID is required")
    private UUID bsm_id;
}