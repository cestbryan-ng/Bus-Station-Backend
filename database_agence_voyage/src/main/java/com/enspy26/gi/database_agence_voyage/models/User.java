package com.enspy26.gi.database_agence_voyage.models;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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

@Entity
@Table(name = "app_user")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @Column(name = "userid")
    private UUID userId;

    private String nom;
    private String prenom;

    @Column(nullable = true)
    private Gender genre;

    @Column(nullable = false, unique = true)
    private String username;

    private String email;

    private String password;

    @Column(name = "telnumber")
    private String telNumber;

    @Column(name = "role")
    private String role; // Stored as comma-separated string

    @Column(name = "businessactortype")
    private BusinessActorType businessActorType;

    @Column(nullable = true, name = "address")
    private String address;

    @Column(name = "idcoordonneeGPS", nullable = true)
    private UUID idcoordonneeGPS;

    // Helper methods for role handling
    @Transient
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

        // Determine business actor type based on roles
        if (roles.contains(RoleType.AGENCE_VOYAGE) || roles.contains(RoleType.ORGANISATION)) {
            this.businessActorType = BusinessActorType.PROVIDER;
        } else if (roles.contains(RoleType.BSM)) {
            this.businessActorType = BusinessActorType.REGULATOR;
        } else {
            this.businessActorType = BusinessActorType.CONSUMER;
        }

        this.role = roles.stream()
                .map(RoleType::name)
                .collect(Collectors.joining(","));
    }

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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
