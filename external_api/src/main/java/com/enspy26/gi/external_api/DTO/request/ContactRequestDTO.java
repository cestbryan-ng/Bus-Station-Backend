package com.enspy26.gi.external_api.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactRequestDTO {

    @NotBlank(message = "Le sujet est obligatoire")
    @Size(max = 200, message = "Le sujet ne peut pas dépasser 200 caractères")
    private String subject;

    @NotBlank(message = "Le nom de l'expéditeur est obligatoire")
    @JsonProperty("senderName")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String senderName;

    @NotBlank(message = "L'email de l'expéditeur est obligatoire")
    @Email(message = "Email expéditeur invalide")
    @JsonProperty("senderEmail")
    private String senderEmail;

    @JsonProperty("senderPhone")
    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    private String senderPhone;

    @NotBlank(message = "Le message est obligatoire")
    @Size(min = 10, max = 2000, message = "Le message doit contenir entre 10 et 2000 caractères")
    private String message;
}