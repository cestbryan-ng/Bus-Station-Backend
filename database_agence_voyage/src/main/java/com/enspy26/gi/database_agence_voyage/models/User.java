package com.enspy26.gi.database_agence_voyage.models;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.enspy26.gi.database_agence_voyage.enums.BusinessActorType;
import com.enspy26.gi.database_agence_voyage.enums.Gender;
import com.enspy26.gi.database_agence_voyage.enums.RoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

  @PrimaryKey
  private UUID userId;

  private String nom;
  private String prenom;
  private Gender genre;
  private String username;
  private String email;
  private String password;
  private String telNumber;
  private String role;
  private BusinessActorType businessActorType;

  // Helper methods for role handling
  public List<RoleType> getRole() {
    if (role == null || role.isEmpty()) {
      return Collections.emptyList();
    }
    return Arrays.stream(role.split(","))
        .map(RoleType::valueOf)
        .collect(Collectors.toList());
  }

  public void setRole(List<RoleType> roles) {
    if (roles == null || roles.isEmpty()) {
      this.role = "";
      return;
    }
    if (roles.contains(RoleType.AGENCE_VOYAGE) || roles.contains(RoleType.ORGANISATION)) {
      this.businessActorType = BusinessActorType.PROVIDER;
    } else {
      this.businessActorType = BusinessActorType.CONSUMER;
    }
    this.role = roles.stream()
        .map(RoleType::name)
        .collect(Collectors.joining(","));
  }

  // Parametre pour un usager
  private String address;

  // Parametre pour une agence
  private UUID idcoordonneeGPS;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getRole().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .collect(Collectors.toList());
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

}
