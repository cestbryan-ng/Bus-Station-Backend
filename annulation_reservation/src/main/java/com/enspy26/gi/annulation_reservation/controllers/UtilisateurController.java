package com.enspy26.gi.annulation_reservation.controllers;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;

import com.enspy26.gi.annulation_reservation.exception.RegistrationException;
import com.enspy26.gi.database_agence_voyage.dto.Utilisateur.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.enspy26.gi.database_agence_voyage.dto.BilletDTO;
import com.enspy26.gi.database_agence_voyage.models.User;
import com.enspy26.gi.external_api.proxies.AuthProxies;
import com.enspy26.gi.annulation_reservation.configurations.JwtService;
import com.enspy26.gi.annulation_reservation.services.ReservationService;
import com.enspy26.gi.annulation_reservation.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/utilisateur")
@AllArgsConstructor
public class UtilisateurController {

    private final ReservationService reservationService;
    private AuthProxies authProxies;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/test")
    @Operation(summary = "Test endpoint")
    public String test() {
        return "Hello World";
    }

    /**
     * Récupère les informations complètes d'un billet en fonction de l'ID du
     * passager.
     *
     * @param idPassager L'ID du passager pour lequel récupérer les informations du
     *                   billet.
     * @return BilletDTO représentant les informations du billet.
     */
    @Operation(summary = "Obtenir les informations d'un billet", description = "Cette méthode permet de récupérer toutes les informations liées à un billet, y compris les informations sur le passager et le voyage.", tags = {
            "Billet"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Billet trouvé et retourné avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BilletDTO.class))),
            @ApiResponse(responseCode = "404", description = "Le passager ou les informations associées n'ont pas été trouvés", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/billet/{idPassager}")
    @SecurityRequirement(name = "bearerAuth") // JWT requis pour le endpoints
    public ResponseEntity<BilletDTO> getBilletInformation(@PathVariable UUID idPassager) {
        try {
            // Appel du service pour récupérer les informations du billet
            BilletDTO billetDTO = reservationService.informationPourBillet(idPassager);
            return ResponseEntity.ok(billetDTO); // Retourne un billet avec un statut HTTP 200 OK
        } catch (EntityNotFoundException ex) {
            // Si l'entité n'est pas trouvée, retourner une erreur 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RuntimeException ex) {
            // En cas d'erreur interne, retourner une erreur 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/inscription")
    @Operation(summary = "Sign up a user")
    public ResponseEntity<UserResponseCreatedDTO> inscription(@RequestBody @Valid UserDTO user) {
        UserResponseCreatedDTO createdUser = userService.createFromDTO(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping(path = "/connexion", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a token for an user")
    public ResponseEntity<UserResponseDTO> getToken(@RequestBody AuthentificationDTO authentificationDTO) {
        UserResponseDTO user = null;

        final Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(),
                        authentificationDTO.password()));

        if (authentication.isAuthenticated()) {
            user = this.jwtService.generateJwt(authentificationDTO.username());
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/profil")
    @SecurityRequirement(name = "bearerAuth") // JWT requis pour le endpoints
    @Operation(summary = "Get a user information by token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO2.class))),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<UserResponseDTO> getMethodName() {
        // UserResponseDTO user = authProxies.getUser(token, username);
        // return ResponseEntity.ok(user);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setUsername(user.getUsername());
            userResponseDTO.setLast_name(user.getNom());
            userResponseDTO.setFirst_name(user.getPrenom());
            userResponseDTO.setEmail(user.getEmail());
            userResponseDTO.setUserId(user.getUserId());
            userResponseDTO.setRole(user.getRole());
            userResponseDTO.setPhone_number(user.getTelNumber());

            // UserResponseDTO userResponseDTO = new UserResponseDTO(userDTO2);
            return new ResponseEntity<>(userResponseDTO, HttpStatus.OK);
        }
    }

    @PostMapping("/chauffeur")
    @SecurityRequirement(name = "bearerAuth") // JWT requis pour le endpoint
    @Operation(summary = "Create chauffeur agence voyage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chauffeur created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseCreatedDTO.class))),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<UserResponseCreatedDTO> createChauffeurAgenceVoyage(
            @RequestBody @Valid ChauffeurRequestDTO chauffeurRequestDTO) {
        UserResponseCreatedDTO chauffeur = this.userService
                .createChauffeurAgenceVoyage(chauffeurRequestDTO);

        return ResponseEntity.created(null).body(chauffeur);
    }

    @GetMapping("/chauffeurs/{agenceId}")
    @SecurityRequirement(name = "bearerAuth") // JWT requis pour le endpoint
    @Operation(summary = "Get chauffeurs by agence ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chauffeurs found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Agence not Found")
    })
    public ResponseEntity<List<UserResponseDTO>> getChauffeursByAgenceId(@PathVariable UUID agenceId) {
        List<UserResponseDTO> chauffeurs = this.userService.getChauffeursByAgenceId(agenceId);
        return ResponseEntity.ok(chauffeurs);
    }

    @PutMapping("/chauffeur/{chauffeurId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update chauffeur agence voyage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chauffeur updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseCreatedDTO.class))),
            @ApiResponse(responseCode = "404", description = "Chauffeur not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with existing data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateChauffeurAgenceVoyage(
            @PathVariable UUID chauffeurId,
            @RequestBody @Valid ChauffeurRequestDTO chauffeurRequestDTO) {
        try {
            UserResponseCreatedDTO updatedChauffeur = this.userService
                    .updateChauffeurAgenceVoyage(chauffeurId, chauffeurRequestDTO);
            return ResponseEntity.ok(updatedChauffeur);
        } catch (RegistrationException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrors());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur");
        }
    }

    @DeleteMapping("/chauffeur/{chauffeurId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete chauffeur agence voyage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chauffeur deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Chauffeur not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteChauffeurAgenceVoyage(@PathVariable UUID chauffeurId) {
        try {
            this.userService.deleteChauffeurAgenceVoyage(chauffeurId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur");
        }
    }

    @PostMapping("/employe")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create employe agence voyage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employe created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseCreatedDTO.class))),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createEmployeAgenceVoyage(@RequestBody @Valid EmployeRequestDTO employeRequestDTO) {
        try {
            UserResponseCreatedDTO employe = this.userService
                    .createEmployeAgenceVoyage(employeRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(employe);
        } catch (RegistrationException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrors());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur");
        }
    }

    @PutMapping("/employe/{employeId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update employe agence voyage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employe updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseCreatedDTO.class))),
            @ApiResponse(responseCode = "404", description = "Employe not found"),
            @ApiResponse(responseCode = "409", description = "Conflict with existing data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateEmployeAgenceVoyage(
            @PathVariable UUID employeId,
            @RequestBody @Valid EmployeRequestDTO employeRequestDTO) {
        try {
            UserResponseCreatedDTO updatedEmploye = this.userService
                    .updateEmployeAgenceVoyage(employeId, employeRequestDTO);
            return ResponseEntity.ok(updatedEmploye);
        } catch (RegistrationException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getErrors());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur");
        }
    }

    @GetMapping("/employes/{agenceId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get employes by agence ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employes found successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Agence not found")
    })
    public ResponseEntity<List<EmployeResponseDTO>> getEmployesByAgenceId(@PathVariable UUID agenceId) {
        try {
            List<EmployeResponseDTO> employes = this.userService.getEmployesByAgenceId(agenceId);
            return ResponseEntity.ok(employes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/employe/{employeId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete employe agence voyage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employe deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employe not found"),
            @ApiResponse(responseCode = "400", description = "User is not an employe"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteEmployeAgenceVoyage(@PathVariable UUID employeId) {
        try {
            this.userService.deleteEmployeAgenceVoyage(employeId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur");
        }
    }

}
