package com.enspy26.gi.external_api.DTO.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponseDTO {

    private boolean success;

    private String message;

    @JsonProperty("sent_at")
    private String sentAt;
}