package com.enspy26.gi.database_agence_voyage.dto.Utilisateur;

import java.util.List;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.RoleType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO2 {
  private UUID userId;
  private String nom;
  private String prenom;
  private String username;
  private String telNumber;
  private List<RoleType> role;

  // Parametre pour un usager
  private String address;

  // Parametre pour une agence
  private UUID idcoordonneeGPS;
}
