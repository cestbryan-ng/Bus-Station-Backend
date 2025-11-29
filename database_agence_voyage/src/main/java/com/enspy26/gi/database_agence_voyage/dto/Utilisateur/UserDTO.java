package com.enspy26.gi.database_agence_voyage.dto.Utilisateur;

import java.util.List;

import com.enspy26.gi.database_agence_voyage.enums.Gender;
import com.enspy26.gi.database_agence_voyage.enums.RoleType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
  private String last_name;
  private String first_name;

  @NotNull(message = "L'email ne peut pas être null")
  @Email(message = "L'email doit être valide")
  private String email;

  @NotNull(message = "Le username ne peut pas être null")
  private String username;

  @NotNull(message = "Le password ne peut pas être null")
  private String password;

  private String phone_number;
  @NotNull(message = "Le Role ne peut pas être null")
  private List<RoleType> role;
  @NotNull(message = "Le genre ne peut pas être null")
  private Gender gender;
}
