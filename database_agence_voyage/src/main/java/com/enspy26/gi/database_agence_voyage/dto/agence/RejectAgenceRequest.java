package com.enspy26.gi.database_agence_voyage.dto.agence;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for agency rejection request
 * Contains BSM information and rejection reason
 *
 * @author Thomas Djotio Ndié
 * @version 1.0
 * @since 2025-12-17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectAgenceRequest {

    @NotNull(message = "BSM ID is required")
    private UUID bsm_id;

    @NotBlank(message = "Rejection reason is required")
    @Size(min = 10, max = 1000, message = "Rejection reason must be between 10 and 1000 characters")
    private String motif_rejet;
}