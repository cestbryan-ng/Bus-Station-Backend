package com.enspy26.gi.external_api.DTO.responses;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class UserResponseDTO implements UserDetails {
  private double createdAt;
  private double updatedAt;
  private double deletedAt;
  private UUID createdBy;
  private UUID updatedBy;
  private UUID id;
  private String username;
  private String email;
  private String name;
  private String phoneNumber;
  private boolean active;
  private List<String> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> (GrantedAuthority) () -> role)
        .toList();
  }

  @Override
  public String getPassword() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
  }
}
