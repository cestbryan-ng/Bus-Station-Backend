package com.enspy26.gi.annulation_reservation.configurations;

import com.enspy26.gi.database_agence_voyage.models.User;
import com.enspy26.gi.annulation_reservation.exception.AnnulationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Component
@Slf4j
public class SecurityUtils {

    public static UUID getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AnnulationException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
            }

            Object principal = authentication.getPrincipal();

            // Si le principal est une instance de User (votre UserDetails personnalisé)
            if (principal instanceof User) {
                User user = (User) principal;
                return user.getUserId();
            }

            // Si le principal est un nom d'utilisateur (string)
            if (principal instanceof String) {
                String username = (String) principal;
                // Vous devrez adapter cette partie selon votre logique de récupération d'ID
                // Par exemple, si le username est l'email, vous pourriez faire une requête en base
                log.warn("Récupération d'ID utilisateur à partir du username: {}", username);

                // Option 1: Si votre JWT contient l'UUID directement
                try {
                    return UUID.fromString(username);
                } catch (IllegalArgumentException e) {
                    // Option 2: Le username n'est pas un UUID, il faut le récupérer autrement
                    throw new AnnulationException(
                            "Impossible de déterminer l'ID utilisateur à partir de: " + username,
                            HttpStatus.UNAUTHORIZED);
                }
            }

            throw new AnnulationException(
                    "Type de principal non supporté: " + principal.getClass().getSimpleName(),
                    HttpStatus.UNAUTHORIZED);

        } catch (AnnulationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur connecté: {}", e.getMessage());
            throw new AnnulationException(
                    "Erreur lors de la récupération de l'utilisateur connecté",
                    HttpStatus.UNAUTHORIZED, e);
        }
    }


    public static User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AnnulationException("Utilisateur non authentifié", HttpStatus.UNAUTHORIZED);
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                return (User) principal;
            }

            throw new AnnulationException(
                    "Impossible de récupérer l'objet User depuis le contexte de sécurité",
                    HttpStatus.UNAUTHORIZED);

        } catch (AnnulationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur connecté: {}", e.getMessage());
            throw new AnnulationException(
                    "Erreur lors de la récupération de l'utilisateur connecté",
                    HttpStatus.UNAUTHORIZED, e);
        }
    }

    public static boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));

        } catch (Exception e) {
            log.error("Erreur lors de la vérification du rôle {}: {}", role, e.getMessage());
            return false;
        }
    }

    public static boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated();
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'authentification: {}", e.getMessage());
            return false;
        }
    }
}