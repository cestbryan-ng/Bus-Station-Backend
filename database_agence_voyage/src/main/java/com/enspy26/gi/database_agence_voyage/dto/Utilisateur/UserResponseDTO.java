package com.enspy26.gi.database_agence_voyage.dto.Utilisateur;

import java.util.List;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.enums.RoleType;
import com.enspy26.gi.database_agence_voyage.models.User;

import lombok.Data;

@Data
public class UserResponseDTO {
  private UUID userId;
  private String token;
  private String last_name;
  private String first_name;
  private String email;
  private String username;
  private String phone_number;
  private String address;
  private List<RoleType> role;

  public static UserResponseDTO fromUser(User user) {
    UserResponseDTO userResponseDTO = new UserResponseDTO();
    userResponseDTO.setUsername(user.getUsername());
    userResponseDTO.setLast_name(user.getNom());
    userResponseDTO.setFirst_name(user.getPrenom());
    userResponseDTO.setEmail(user.getEmail());
    userResponseDTO.setUserId(user.getUserId());
    userResponseDTO.setRole(user.getRole());
    userResponseDTO.setPhone_number(user.getTelNumber());
    userResponseDTO.setAddress(user.getAddress());

    return userResponseDTO;
  }
}
