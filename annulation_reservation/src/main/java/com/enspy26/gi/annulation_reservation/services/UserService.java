package com.enspy26.gi.annulation_reservation.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.enspy26.gi.database_agence_voyage.dto.Utilisateur.*;
import com.enspy26.gi.database_agence_voyage.enums.StatutEmploye;
import com.enspy26.gi.database_agence_voyage.enums.StatutValidation;
import com.enspy26.gi.database_agence_voyage.models.EmployeAgenceVoyage;
import com.enspy26.gi.database_agence_voyage.repositories.EmployeAgenceVoyageRepository;
import com.enspy26.gi.notification.factory.NotificationFactory;
import com.enspy26.gi.notification.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.enspy26.gi.annulation_reservation.exception.RegistrationException;
import com.enspy26.gi.database_agence_voyage.dto.agence.AgenceVoyageDTO;
import com.enspy26.gi.database_agence_voyage.enums.RoleType;
import com.enspy26.gi.database_agence_voyage.enums.StatutChauffeur;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.ChauffeurAgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.User;
import com.enspy26.gi.database_agence_voyage.repositories.AgenceVoyageRepository;
import com.enspy26.gi.database_agence_voyage.repositories.ChauffeurAgenceVoyageRepository;
import com.enspy26.gi.database_agence_voyage.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

  private UserRepository userRepository;
  private AgenceVoyageRepository agenceVoyageRepository;
  private PasswordEncoder passwordEncoder;
  private ChauffeurAgenceVoyageRepository chauffeurAgenceVoyageRepository;
  private EmployeAgenceVoyageRepository employeAgenceVoyageRepository;
  private NotificationService notificationService;

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public User findById(UUID id) {
    return userRepository.findById(id).orElse(null);
  }

  public User findByUsername(String email) {
    return userRepository.findByUsername(email).get(0);
  }

  public User create(User user) {
    user.setPassword(this.passwordEncoder.encode(user.getPassword()));
    user.setUserId(UUID.randomUUID());
    return userRepository.save(user);
  }

  public UserResponseCreatedDTO createFromDTO(UserDTO userDTO) {

    List<User> userWithUsername = userRepository.findByUsername(userDTO.getUsername());
    boolean isEmailExist = userRepository.existsByEmail(userDTO.getEmail());
    boolean isTelNumberExist = userRepository.existsByTelNumber(userDTO.getPhone_number());

    if (isEmailExist || isTelNumberExist || !userWithUsername.isEmpty()) {
      HashMap<String, String> errors = new HashMap<>();
      if (isEmailExist) {
        errors.put("email", "Email already exists");
      }
      if (isTelNumberExist) {
        errors.put("phone_number", "Phone number already exists");
      }
      if (!userWithUsername.isEmpty()) {
        errors.put("username", "Username already exists");
      }
      throw new RegistrationException(HttpStatus.CONFLICT, errors);
    }

    User user = new User();
    user.setNom(userDTO.getLast_name());
    user.setEmail(userDTO.getEmail());
    user.setPassword(userDTO.getPassword());
    user.setRole(userDTO.getRole());
    user.setTelNumber(userDTO.getPhone_number());
    user.setPrenom(userDTO.getFirst_name());
    user.setUsername(userDTO.getUsername());
    user.setGenre(userDTO.getGender());

    user = create(user);

    // Envoyer notification de bienvenue
    try {
      notificationService.sendNotification(
          NotificationFactory.createUserRegisteredEvent(user));
    } catch (Exception e) {
      log.warn("Erreur lors de l'envoi de la notification d'inscription: {}", e.getMessage());
    }

    UserResponseCreatedDTO userResponse = UserResponseCreatedDTO.fromUser(user);
    return userResponse;
  }

  public User update(User user) {
    return userRepository.save(user);
  }

  public void delete(UUID id) {
    userRepository.deleteById(id);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = this.userRepository.findByUsername(username).get(0);
    UserDetails userDetails = user;
    return userDetails;
  }

    public AgenceVoyage createAgenceVoyage(AgenceVoyageDTO agenceDTO) {
        User user = userRepository.findById(agenceDTO.getUser_id())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with ID " + agenceDTO.getUser_id() + " not found"
                ));

        // Check for duplicate agency names
        boolean is_long_name_used = agenceVoyageRepository.existsByLongName(agenceDTO.getLong_name());
        boolean is_short_name_used = agenceVoyageRepository.existsByShortName(agenceDTO.getShort_name());

        if (is_short_name_used || is_long_name_used) {
            HashMap<String, String> errors = new HashMap<>();
            if (is_long_name_used) {
                errors.put("long_name", "An agency with long name " + agenceDTO.getLong_name() + " already exists");
            }
            if (is_short_name_used) {
                errors.put("short_name", "An agency with short name " + agenceDTO.getShort_name() + " already exists");
            }
            throw new RegistrationException(HttpStatus.CONFLICT, errors);
        }

        // Create new agency entity
        AgenceVoyage agence = new AgenceVoyage();
        agence.setAgencyId(UUID.randomUUID());
        agence.setOrganisationId(agenceDTO.getOrganisation_id());
        agence.setUserId(agenceDTO.getUser_id());
        agence.setLongName(agenceDTO.getLong_name());
        agence.setShortName(agenceDTO.getShort_name());
        agence.setLocation(agenceDTO.getLocation());
        agence.setVille(agenceDTO.getVille());
        agence.setStatutValidation(StatutValidation.EN_ATTENTE);
        agence.setSocialNetwork(agenceDTO.getSocial_network());
        agence.setDescription(agenceDTO.getDescription());
        agence.setGreetingMessage(agenceDTO.getGreeting_message());

        // Update user roles if needed
        if (!user.getRole().contains(RoleType.AGENCE_VOYAGE)) {
            List<RoleType> updated_roles = new ArrayList<>(user.getRole());
            updated_roles.add(RoleType.AGENCE_VOYAGE);
            updated_roles.add(RoleType.ORGANISATION);
            user.setRole(updated_roles);
            userRepository.save(user);
        }

        // Send agency creation notification
        try {
            notificationService.sendNotification(
                    NotificationFactory.createAgencyCreatedEvent(agence, user)
            );
        } catch (Exception e) {
            log.warn("Error sending agency creation notification: {}", e.getMessage());
        }

        return agenceVoyageRepository.save(agence);
    }

    public AgenceVoyage updateAgenceVoyage(UUID agency_id, AgenceVoyageDTO agenceDTO) {
        AgenceVoyage agence = agenceVoyageRepository.findById(agency_id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Agency not found"
                ));

        // Check long name uniqueness (exclude current agency)
        List<AgenceVoyage> agencies_with_long_name = agenceVoyageRepository.findByLongName(agenceDTO.getLong_name());
        if (!agencies_with_long_name.isEmpty() &&
                !agencies_with_long_name.get(0).getAgencyId().equals(agency_id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An agency with long name " + agenceDTO.getLong_name() + " already exists"
            );
        }

        // Check short name uniqueness (exclude current agency)
        List<AgenceVoyage> agencies_with_short_name = agenceVoyageRepository.findByShortName(agenceDTO.getShort_name());
        if (!agencies_with_short_name.isEmpty() &&
                !agencies_with_short_name.get(0).getAgencyId().equals(agency_id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An agency with short name " + agenceDTO.getShort_name() + " already exists"
            );
        }

        // Update agency fields
        agence.setOrganisationId(agenceDTO.getOrganisation_id());
        agence.setUserId(agenceDTO.getUser_id());
        agence.setLongName(agenceDTO.getLong_name());
        agence.setShortName(agenceDTO.getShort_name());
        agence.setLocation(agenceDTO.getLocation());
        agence.setVille(agenceDTO.getVille()); // ✅ FIX: Add ville field
        agence.setSocialNetwork(agenceDTO.getSocial_network());
        agence.setDescription(agenceDTO.getDescription());
        agence.setGreetingMessage(agenceDTO.getGreeting_message());

        return agenceVoyageRepository.save(agence);
    }

  public void deleteAgenceVoyage(UUID agencyId) {
    if (!agenceVoyageRepository.existsById(agencyId)) {
      throw new RuntimeException("Agency not found");
    }
    agenceVoyageRepository.deleteById(agencyId);
  }

  public UserResponseCreatedDTO createChauffeurAgenceVoyage(ChauffeurRequestDTO chauffeurRequestDTO) {
    User user = null;
    AgenceVoyage agence = agenceVoyageRepository.findById(chauffeurRequestDTO.getAgenceVoyageId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "L'agence de voyage n'existe pas"));
    if (!chauffeurRequestDTO.isUserExist()) {
      UserResponseCreatedDTO userCreated = this.createFromDTO(chauffeurRequestDTO);
      user = this.findById(UUID.fromString(userCreated.getId()));
    } else {
      if (this.userRepository.existsByEmail(chauffeurRequestDTO.getEmail())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
      }
      user = this.userRepository.findByEmail(chauffeurRequestDTO.getEmail()).get(0);
    }

    List<RoleType> updatedRoles = new ArrayList<>(user.getRole());
    updatedRoles.add(RoleType.EMPLOYE);
    user.setRole(updatedRoles);
    this.userRepository.save(user);

    ChauffeurAgenceVoyage chauffeur = new ChauffeurAgenceVoyage();
    chauffeur.setChauffeurId(UUID.randomUUID());
    chauffeur.setAgenceVoyageId(chauffeurRequestDTO.getAgenceVoyageId());
    chauffeur.setUserId(user.getUserId());
    chauffeur.setStatusChauffeur(StatutChauffeur.LIBRE);
    chauffeurAgenceVoyageRepository.save(chauffeur);

    // Créer également un enregistrement employé pour le chauffeur
    // Un chauffeur est aussi un employé avec le poste "Chauffeur"
    EmployeAgenceVoyage employe = new EmployeAgenceVoyage();
    employe.setEmployeId(UUID.randomUUID());
    employe.setAgenceVoyageId(chauffeurRequestDTO.getAgenceVoyageId());
    employe.setUserId(user.getUserId());
    employe.setPoste("Chauffeur");
    employe.setDepartement("Transport");
    employe.setDateEmbauche(LocalDateTime.now());
    employe.setStatutEmploye(StatutEmploye.ACTIF);
    employe.setSalaire(0.0);
    employeAgenceVoyageRepository.save(employe);

    // Envoyer notification d'ajout d'employé
    try {
      notificationService.sendNotification(
          NotificationFactory.createEmployeeAddedEvent(employe, user, agence));
    } catch (Exception e) {
      log.warn("Erreur lors de l'envoi de la notification d'ajout de chauffeur: {}", e.getMessage());
    }

    return UserResponseCreatedDTO.fromUser(user);
  }

  public List<UserResponseDTO> getChauffeursByAgenceId(UUID agenceId) {
    List<ChauffeurAgenceVoyage> chauffeurs = chauffeurAgenceVoyageRepository.findByAgenceVoyageId(agenceId);
    List<UserResponseDTO> chauffeursResponse = new ArrayList<>();
    for (ChauffeurAgenceVoyage chauffeur : chauffeurs) {
      chauffeursResponse.add(UserResponseDTO.fromUser(this.userRepository.findById(chauffeur.getUserId()).get()));
    }
    return chauffeursResponse;
  }

  public UserResponseCreatedDTO updateChauffeurAgenceVoyage(UUID chauffeurId, ChauffeurRequestDTO chauffeurRequestDTO) {
    ChauffeurAgenceVoyage chauffeurAgenceVoyage = chauffeurAgenceVoyageRepository.findById(chauffeurId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le Chauffeur n'existe pas"));

    User currentUser = this.findById(chauffeurAgenceVoyage.getUserId());
    if (currentUser == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utilisateur associé au chauffeur n'existe pas");
    }

    // Vérifier les conflits pour les nouvelles données
    HashMap<String, String> errors = new HashMap<>();

    // Vérifier l'email si différent
    if (!currentUser.getEmail().equals(chauffeurRequestDTO.getEmail())) {
      if (userRepository.existsByEmail(chauffeurRequestDTO.getEmail())) {
        errors.put("email", "Email already exists");
      }
    }

    // Vérifier le téléphone si différent
    if (!currentUser.getTelNumber().equals(chauffeurRequestDTO.getPhone_number())) {
      if (userRepository.existsByTelNumber(chauffeurRequestDTO.getPhone_number())) {
        errors.put("phone_number", "Phone number already exists");
      }
    }

    // Vérifier le username si différent
    if (!currentUser.getUsername().equals(chauffeurRequestDTO.getUsername())) {
      List<User> userWithUsername = userRepository.findByUsername(chauffeurRequestDTO.getUsername());
      if (!userWithUsername.isEmpty()) {
        errors.put("username", "Username already exists");
      }
    }

    if (!errors.isEmpty()) {
      throw new RegistrationException(HttpStatus.CONFLICT, errors);
    }

    // Mettre à jour les informations de l'utilisateur
    currentUser.setNom(chauffeurRequestDTO.getLast_name());
    currentUser.setPrenom(chauffeurRequestDTO.getFirst_name());
    currentUser.setEmail(chauffeurRequestDTO.getEmail());
    currentUser.setTelNumber(chauffeurRequestDTO.getPhone_number());
    currentUser.setUsername(chauffeurRequestDTO.getUsername());
    currentUser.setGenre(chauffeurRequestDTO.getGender());

    // Mettre à jour le mot de passe si fourni
    if (chauffeurRequestDTO.getPassword() != null && !chauffeurRequestDTO.getPassword().isEmpty()) {
      currentUser.setPassword(this.passwordEncoder.encode(chauffeurRequestDTO.getPassword()));
    }

    // S'assurer que l'utilisateur a le rôle EMPLOYE
    List<RoleType> updatedRoles = new ArrayList<>(currentUser.getRole());
    if (!updatedRoles.contains(RoleType.EMPLOYE)) {
      updatedRoles.add(RoleType.EMPLOYE);
      currentUser.setRole(updatedRoles);
    }

    // Mettre à jour l'agence du chauffeur
    chauffeurAgenceVoyage.setAgenceVoyageId(chauffeurRequestDTO.getAgenceVoyageId());

    // Sauvegarder les modifications
    userRepository.save(currentUser);
    chauffeurAgenceVoyageRepository.save(chauffeurAgenceVoyage);

    return UserResponseCreatedDTO.fromUser(currentUser);
  }

  // Nouvelle méthode deleteChauffeurAgenceVoyage
  public void deleteChauffeurAgenceVoyage(UUID chauffeurId) {
    ChauffeurAgenceVoyage chauffeur = chauffeurAgenceVoyageRepository.findById(chauffeurId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le Chauffeur n'existe pas"));

    UUID userId = chauffeur.getUserId();

    // Supprimer le chauffeur
    chauffeurAgenceVoyageRepository.deleteById(chauffeurId);

    // Supprimer aussi l'enregistrement employé associé au chauffeur
    // Rechercher l'employé avec le même userId et agenceVoyageId
    List<EmployeAgenceVoyage> employesAssocies = employeAgenceVoyageRepository
        .findByAgenceVoyageId(chauffeur.getAgenceVoyageId())
        .stream()
        .filter(emp -> emp.getUserId().equals(userId) && "Chauffeur".equals(emp.getPoste()))
        .toList();

    for (EmployeAgenceVoyage employe : employesAssocies) {
      employeAgenceVoyageRepository.deleteById(employe.getEmployeId());
    }

    // Supprimer l'utilisateur associé
    if (userRepository.existsById(userId)) {
      userRepository.deleteById(userId);
    }
  }

  public UserResponseCreatedDTO createEmployeAgenceVoyage(EmployeRequestDTO employeRequestDTO) {
    User user = null;

    // Créer ou récupérer l'utilisateur
    if (!employeRequestDTO.isUserExist()) {
      UserResponseCreatedDTO userCreated = this.createFromDTO(employeRequestDTO);
      user = this.findById(UUID.fromString(userCreated.getId()));
    } else {
      if (this.userRepository.existsByEmail(employeRequestDTO.getEmail())) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
      }
      user = this.userRepository.findByEmail(employeRequestDTO.getEmail()).get(0);
    }

    // S'assurer que l'utilisateur a le rôle EMPLOYE
    List<RoleType> updatedRoles = new ArrayList<>(user.getRole());
    if (!updatedRoles.contains(RoleType.EMPLOYE)) {
      updatedRoles.add(RoleType.EMPLOYE);
      user.setRole(updatedRoles);
      this.userRepository.save(user);
    }

    // Vérifier que l'agence existe
    AgenceVoyage agence = agenceVoyageRepository.findById(employeRequestDTO.getAgenceVoyageId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agence non trouvée"));

    // Créer l'enregistrement employé
    EmployeAgenceVoyage employe = new EmployeAgenceVoyage();
    employe.setEmployeId(UUID.randomUUID());
    employe.setAgenceVoyageId(employeRequestDTO.getAgenceVoyageId());
    employe.setUserId(user.getUserId());
    employe.setPoste(employeRequestDTO.getPoste());
    employe.setDepartement(employeRequestDTO.getDepartement());
    employe.setSalaire(employeRequestDTO.getSalaire());
    employe.setManagerId(employeRequestDTO.getManagerId());
    employe.setDateEmbauche(LocalDateTime.now());
    employe.setStatutEmploye(StatutEmploye.ACTIF);

    employeAgenceVoyageRepository.save(employe);

    // Récupérer le nom du manager si présent
    String nomManager = null;
    if (employe.getManagerId() != null) {
      EmployeAgenceVoyage manager = employeAgenceVoyageRepository.findById(employe.getManagerId()).orElse(null);
      if (manager != null) {
        User managerUser = userRepository.findById(manager.getUserId()).orElse(null);
        if (managerUser != null) {
          nomManager = managerUser.getPrenom() + " " + managerUser.getNom();
        }
      }
    }

    // Envoyer notification d'ajout d'employé
    try {
      notificationService.sendNotification(
          NotificationFactory.createEmployeeAddedEvent(employe, user, agence));
    } catch (Exception e) {
      log.warn("Erreur lors de l'envoi de la notification d'ajout d'employé: {}", e.getMessage());
    }

    // return EmployeResponseDTO.fromEntities(employe, user, agence, nomManager);
    return UserResponseCreatedDTO.fromUser(user);
  }

  public List<EmployeResponseDTO> getEmployesByAgenceId(UUID agenceId) {
    List<EmployeAgenceVoyage> employes = employeAgenceVoyageRepository.findByAgenceVoyageId(agenceId);
    List<EmployeResponseDTO> employesResponse = new ArrayList<>();

    AgenceVoyage agence = agenceVoyageRepository.findById(agenceId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agence non trouvée"));

    for (EmployeAgenceVoyage employe : employes) {
      User user = userRepository.findById(employe.getUserId()).orElse(null);
      if (user != null) {
        // Récupérer le nom du manager
        String nomManager = null;
        if (employe.getManagerId() != null) {
          EmployeAgenceVoyage manager = employeAgenceVoyageRepository.findById(employe.getManagerId()).orElse(null);
          if (manager != null) {
            User managerUser = userRepository.findById(manager.getUserId()).orElse(null);
            if (managerUser != null) {
              nomManager = managerUser.getPrenom() + " " + managerUser.getNom();
            }
          }
        }

        employesResponse.add(EmployeResponseDTO.fromEntities(employe, user, agence, nomManager));
      }
    }

    return employesResponse;
  }

  /**
   * Met à jour un employé
   */
  public UserResponseCreatedDTO updateEmployeAgenceVoyage(UUID employeId, EmployeRequestDTO employeRequestDTO) {
    EmployeAgenceVoyage employe = employeAgenceVoyageRepository.findById(employeId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé non trouvé"));

    User user = userRepository.findById(employe.getUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));

    // Vérifier les conflits pour les nouvelles données
    HashMap<String, String> errors = new HashMap<>();

    // Vérifier l'email si différent
    if (!user.getEmail().equals(employeRequestDTO.getEmail())) {
      if (userRepository.existsByEmail(employeRequestDTO.getEmail())) {
        errors.put("email", "Email already exists");
      }
    }

    // Vérifier le téléphone si différent
    if (!user.getTelNumber().equals(employeRequestDTO.getPhone_number())) {
      if (userRepository.existsByTelNumber(employeRequestDTO.getPhone_number())) {
        errors.put("phone_number", "Phone number already exists");
      }
    }

    // Vérifier le username si différent
    if (!user.getUsername().equals(employeRequestDTO.getUsername())) {
      List<User> userWithUsername = userRepository.findByUsername(employeRequestDTO.getUsername());
      if (!userWithUsername.isEmpty()) {
        errors.put("username", "Username already exists");
      }
    }

    if (!errors.isEmpty()) {
      throw new RegistrationException(HttpStatus.CONFLICT, errors);
    }

    // Mettre à jour les informations de l'utilisateur
    user.setNom(employeRequestDTO.getLast_name());
    user.setPrenom(employeRequestDTO.getFirst_name());
    user.setEmail(employeRequestDTO.getEmail());
    user.setTelNumber(employeRequestDTO.getPhone_number());
    user.setUsername(employeRequestDTO.getUsername());
    user.setGenre(employeRequestDTO.getGender());

    // Mettre à jour le mot de passe si fourni
    if (employeRequestDTO.getPassword() != null && !employeRequestDTO.getPassword().isEmpty()) {
      user.setPassword(this.passwordEncoder.encode(employeRequestDTO.getPassword()));
    }

    // S'assurer que l'utilisateur a le rôle EMPLOYE
    List<RoleType> updatedRoles = new ArrayList<>(user.getRole());
    if (!updatedRoles.contains(RoleType.EMPLOYE)) {
      updatedRoles.add(RoleType.EMPLOYE);
      user.setRole(updatedRoles);
    }

    // Mettre à jour les données employé
    employe.setAgenceVoyageId(employeRequestDTO.getAgenceVoyageId());
    employe.setPoste(employeRequestDTO.getPoste());
    employe.setDepartement(employeRequestDTO.getDepartement());
    employe.setSalaire(employeRequestDTO.getSalaire());
    employe.setManagerId(employeRequestDTO.getManagerId());

    userRepository.save(user);
    employeAgenceVoyageRepository.save(employe);

    AgenceVoyage agence = agenceVoyageRepository.findById(employe.getAgenceVoyageId()).orElse(null);

    // Récupérer le nom du manager
    String nomManager = null;
    if (employe.getManagerId() != null) {
      EmployeAgenceVoyage manager = employeAgenceVoyageRepository.findById(employe.getManagerId()).orElse(null);
      if (manager != null) {
        User managerUser = userRepository.findById(manager.getUserId()).orElse(null);
        if (managerUser != null) {
          nomManager = managerUser.getPrenom() + " " + managerUser.getNom();
        }
      }
    }

    // return EmployeResponseDTO.fromEntities(employe, user, agence, nomManager);
    return UserResponseCreatedDTO.fromUser(user);
  }

  public void deleteEmployeAgenceVoyage(UUID employeId) {
    EmployeAgenceVoyage employe = employeAgenceVoyageRepository.findById(employeId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé non trouvé"));

    // Option 1: Supprimer complètement l'employé et l'utilisateur
    // UUID userId = employe.getUserId();
    // employeAgenceVoyageRepository.deleteById(employeId);
    // userRepository.deleteById(userId);

    // Option 2: Marquer comme inactif (recommandé pour garder l'historique)
    employe.setStatutEmploye(StatutEmploye.DEMISSIONNE);
    employe.setDateFinContrat(LocalDateTime.now());
    employeAgenceVoyageRepository.save(employe);
  }

  /**
   * Récupère tous les employés d'une organisation (via ses agences)
   */
  public List<EmployeResponseDTO> getEmployesByOrganisationId(UUID organisationId) {
    // Récupérer toutes les agences de l'organisation
    List<AgenceVoyage> agences = agenceVoyageRepository.findByOrganisationId(organisationId);
    List<EmployeResponseDTO> allEmployes = new ArrayList<>();

    for (AgenceVoyage agence : agences) {
      List<EmployeResponseDTO> employesAgence = getEmployesByAgenceId(agence.getAgencyId());
      allEmployes.addAll(employesAgence);
    }

    return allEmployes;
  }

}
