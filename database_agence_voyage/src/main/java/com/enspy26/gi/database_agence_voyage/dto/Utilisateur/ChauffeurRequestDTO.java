package com.enspy26.gi.database_agence_voyage.dto.Utilisateur;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChauffeurRequestDTO extends UserDTO {

  @NotNull(message = "L'ID de l'agence de voyage est requis")
  private UUID agenceVoyageId;

  private boolean isUserExist = false;
}
