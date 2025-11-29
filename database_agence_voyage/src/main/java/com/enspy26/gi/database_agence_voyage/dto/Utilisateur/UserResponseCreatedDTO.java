package com.enspy26.gi.database_agence_voyage.dto.Utilisateur;

import java.util.List;
import java.time.ZonedDateTime;

import com.enspy26.gi.database_agence_voyage.enums.Gender;
import com.enspy26.gi.database_agence_voyage.enums.RoleType;
import com.enspy26.gi.database_agence_voyage.enums.BusinessActorType;
import com.enspy26.gi.database_agence_voyage.models.User;

import lombok.Data;

@Data
public class UserResponseCreatedDTO {
  private String created_at;
  private String updated_at;
  private String deleted_at;
  private String created_by;
  private String updated_by;
  private String id;
  private String email;
  private String friendly_name;
  private String secondary_email;
  private String date_of_birth;
  private Gender gender;
  private String country_code;
  private String dial_code;
  private String secondary_phone_number;
  private String avatar_picture;
  private String profile_picture;
  private String country_id;
  private String last_login_time;
  private List<String> keywords;
  private String registration_date;
  private BusinessActorType type;
  private String first_name;
  private String last_name;
  private String username;
  private String phone_number;
  private List<RoleType> roles;

  public static UserResponseCreatedDTO fromUser(User user) {
    UserResponseCreatedDTO dto = new UserResponseCreatedDTO();

    // Map basic user information
    dto.setId(user.getUserId().toString());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setFirst_name(user.getPrenom());
    dto.setLast_name(user.getNom());
    dto.setPhone_number(user.getTelNumber());
    dto.setGender(user.getGenre());

    // Map roles list
    dto.setRoles(user.getRole());

    // Set some default values for required fields
    dto.setCreated_at(ZonedDateTime.now().toString());
    dto.setUpdated_at(ZonedDateTime.now().toString());

    // Map address to friendly_name if available
    if (user.getAddress() != null) {
      dto.setFriendly_name(user.getAddress());
    }

    dto.setType(user.getBusinessActorType());

    // Set registration date
    dto.setRegistration_date(ZonedDateTime.now().toString());

    return dto;
  }
}
