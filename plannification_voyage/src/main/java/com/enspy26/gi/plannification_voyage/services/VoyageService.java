package com.enspy26.gi.plannification_voyage.services;

import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageCreateRequestDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyageDetailsDTO;
import com.enspy26.gi.database_agence_voyage.dto.voyage.VoyagePreviewDTO;
import com.enspy26.gi.database_agence_voyage.models.AgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.ChauffeurAgenceVoyage;
import com.enspy26.gi.database_agence_voyage.models.ClassVoyage;
import com.enspy26.gi.database_agence_voyage.models.LigneVoyage;
import com.enspy26.gi.database_agence_voyage.models.Passager;
import com.enspy26.gi.database_agence_voyage.models.Reservation;
import com.enspy26.gi.database_agence_voyage.models.User;
import com.enspy26.gi.database_agence_voyage.models.Vehicule;
import com.enspy26.gi.database_agence_voyage.models.Voyage;
import com.enspy26.gi.database_agence_voyage.repositories.AgenceVoyageRepository;
import com.enspy26.gi.database_agence_voyage.repositories.ChauffeurAgenceVoyageRepository;
import com.enspy26.gi.database_agence_voyage.repositories.ClassVoyageRepository;
import com.enspy26.gi.database_agence_voyage.repositories.LigneVoyageRepository;
import com.enspy26.gi.database_agence_voyage.repositories.PassagerRepository;
import com.enspy26.gi.database_agence_voyage.repositories.ReservationRepository;
import com.enspy26.gi.database_agence_voyage.repositories.UserRepository;
import com.enspy26.gi.database_agence_voyage.repositories.VehiculeRepository;
import com.enspy26.gi.database_agence_voyage.repositories.VoyageRepository;
import com.enspy26.gi.external_api.proxies.AgenceVoyageProxies;
import com.enspy26.gi.notification.factory.NotificationFactory;
import com.enspy26.gi.plannification_voyage.mappers.VoyageMapper;
import com.enspy26.gi.notification.services.NotificationService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class VoyageService {

    private final VoyageRepository voyageRepository;

    private final LigneVoyageRepository ligneVoyageRepository;

    private final VoyageMapper voyageMapper;

    private final UserRepository userRepository;

    private final ChauffeurAgenceVoyageRepository chauffeurAgenceVoyageRepository;

    private final ClassVoyageRepository classVoyageRepository;

    private final VehiculeService vehiculeService;

    private final ReservationRepository reservationRepository;

    private final PassagerRepository passagerRepository;

    private final AgenceVoyageProxies agenceVoyageProxies;

    private final AgenceVoyageRepository agenceVoyageRepository;

    private final NotificationService notificationService;

    private final VehiculeRepository vehiculeRepository;

    public Page<Voyage> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Voyage> slice = voyageRepository.findAll(pageable);
        long total = voyageRepository.count();
        return PaginationUtils.SliceToPage(slice, total);
    }

    public Page<VoyagePreviewDTO> findAllPreview(int page, int size) {
        // Gérer le cas où la table est vide
        long totalVoyages = voyageRepository.count();
        if (totalVoyages == 0) {
            // Retourner une page vide
            return Page.empty(PageRequest.of(page, size));
        }

        // Ajuster la taille de page si nécessaire
        if (totalVoyages < size) {
            size = (int) totalVoyages;
        }

        // S'assurer que size est au minimum 1
        if (size < 1) {
            size = 1;
        }

        Pageable pageable = PageRequest.of(page, size);
        Slice<Voyage> voyagesSlice = voyageRepository.findAll(pageable);

        // Récupère tous les voyages et les traite en un flux
        List<VoyagePreviewDTO> previewVoyageList = voyagesSlice.stream()
                .map(voyage -> {
                    // Récupère la première ligne de voyage associée
                    LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyage.getIdVoyage());

                    ClassVoyage classVoyage = classVoyageRepository.findById(ligneVoyage.getIdClassVoyage())
                            .orElse(null);

                    // Trouve l'agence associée et mappe les informations si présente
                    return agenceVoyageRepository.findById(ligneVoyage.getIdAgenceVoyage())
                            .map(agenceVoyage -> voyageMapper.toVoyagePreviewDTO(voyage, agenceVoyage, classVoyage))
                            .orElse(null); // Retourne null si aucune agence n'est trouvée
                })
                .filter(Objects::nonNull) // Exclut les valeurs null
                .collect(Collectors.toList()); // Convertit en liste

        return PaginationUtils.ContentToPage(previewVoyageList, pageable, totalVoyages);
    }

    public VoyageDetailsDTO findById(UUID id) {

        Voyage voyage = voyageRepository.findById(id).orElse(null);
        if (voyage != null) {
            LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyage.getIdVoyage());
            ClassVoyage classVoyage = classVoyageRepository.findById(ligneVoyage.getIdClassVoyage()).orElse(null);
            AgenceVoyage agenceVoyage = agenceVoyageRepository.findById(ligneVoyage.getIdAgenceVoyage()).orElse(null);
            ChauffeurAgenceVoyage chauffeur = chauffeurAgenceVoyageRepository.findById(ligneVoyage.getIdChauffeur())
                    .orElse(null);
            User chauffeurUser = null;
            if (chauffeur != null) {
                chauffeurUser = userRepository.findById(chauffeur.getUserId()).orElse(null);
            }
            Vehicule vehicule = this.vehiculeService.findById(ligneVoyage.getIdVehicule());
            List<Integer> placesReservees = new ArrayList<>();
            List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());
            for (Reservation reservation : reservations) {
                List<Passager> passagers = passagerRepository.findAllByIdReservation(reservation.getIdReservation());
                for (Passager passager : passagers) {
                    placesReservees.add(passager.getPlaceChoisis());
                }
            }
            return voyageMapper.tovoyageDetailsDTO(voyage, agenceVoyage, classVoyage, vehicule, placesReservees,
                    chauffeurUser);
        }
        return null;
    }

    public VoyageDetailsDTO create(VoyageCreateRequestDTO voyageDto) {
        Voyage voyage = new Voyage();
        voyage.setIdVoyage(UUID.randomUUID());
        voyage.setTitre(voyageDto.getTitre());
        voyage.setDescription(voyageDto.getDescription());

        // Convertir LocalDateTime en Date
        voyage.setDateDepartPrev(voyageDto.getDateDepartPrev() != null
                ? Date.from(voyageDto.getDateDepartPrev().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);

        voyage.setLieuDepart(voyageDto.getLieuDepart());
        voyage.setLieuArrive(voyageDto.getLieuArrive());
        voyage.setPointDeDepart(voyageDto.getPointDeDepart());
        voyage.setPointArrivee(voyageDto.getPointArrivee());

        voyage.setHeureArrive(voyageDto.getHeureArrive() != null
                ? Date.from(voyageDto.getHeureArrive().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);

        // Calcul de la durée du voyage
        if (voyageDto.getDateDepartPrev() != null && voyageDto.getHeureArrive() != null) {
            Duration duree = Duration.between(voyageDto.getDateDepartPrev(), voyageDto.getHeureArrive());
            voyage.setDureeVoyage(duree);
        }

        voyage.setHeureDepartEffectif(voyageDto.getHeureDepartEffectif() != null
                ? Date.from(voyageDto.getHeureDepartEffectif().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);

        voyage.setNbrPlaceReservable(voyageDto.getNbrPlaceReservable());
        voyage.setNbrPlaceReserve(voyageDto.getNbrPlaceReserve());
        voyage.setNbrPlaceConfirm(voyageDto.getNbrPlaceConfirm());
        voyage.setNbrPlaceRestante(voyageDto.getNbrPlaceRestante());
        voyage.setDatePublication(new Date());

        voyage.setDateLimiteReservation(voyageDto.getDateLimiteReservation() != null
                ? Date.from(voyageDto.getDateLimiteReservation().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);

        voyage.setDateLimiteConfirmation(voyageDto.getDateLimiteConfirmation() != null
                ? Date.from(voyageDto.getDateLimiteConfirmation().atZone(java.time.ZoneId.systemDefault()).toInstant())
                : null);

        voyage.setStatusVoyage(voyageDto.getStatusVoyage());
        voyage.setSmallImage(voyageDto.getSmallImage() != null ? voyageDto.getSmallImage() :
                "https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=");
        voyage.setBigImage(voyageDto.getBigImage() != null ? voyageDto.getBigImage() :
                "https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=");
        voyage.setAmenities(VoyageMapper.amenitiesListToString(voyageDto.getAmenities()));

        // Reste du code identique...
        LigneVoyage ligneVoyage = new LigneVoyage();
        if (!agenceVoyageProxies.isAgenceVoyageExist(voyageDto.getAgenceVoyageId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'agence voyage n'existe pas.");
        }
        ligneVoyage.setIdAgenceVoyage(voyageDto.getAgenceVoyageId());

        ClassVoyage classVoyage = this.classVoyageRepository.findById(voyageDto.getClassVoyageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La classe de voyage n'existe pas."));
        ligneVoyage.setIdClassVoyage(classVoyage.getIdClassVoyage());

        Vehicule vehicule = this.vehiculeService.findById(voyageDto.getVehiculeId());
        if (vehicule == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le vehicule n'existe pas.");
        }
        ligneVoyage.setIdVehicule(voyageDto.getVehiculeId());

// Validation du chauffeur
        log.info("Vérification du chauffeur: {}", voyageDto.getChauffeurId());
        List<ChauffeurAgenceVoyage> chauffeurs = this.chauffeurAgenceVoyageRepository
                .findAllByUserId(voyageDto.getChauffeurId());

        if (chauffeurs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le chauffeur n'existe pas.");
        }

        if (chauffeurs.size() > 1) {
            log.warn("Plusieurs chauffeurs trouvés pour userId {}: {} résultats",
                    voyageDto.getChauffeurId(), chauffeurs.size());

            // Filtrer pour prendre celui qui appartient à l'agence
            chauffeurs = chauffeurs.stream()
                    .filter(c -> c.getAgenceVoyageId().equals(voyageDto.getAgenceVoyageId()))
                    .collect(Collectors.toList());

            if (chauffeurs.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aucun chauffeur trouvé pour cette agence avec cet userId.");
            }
        }

        ChauffeurAgenceVoyage chauffeur = chauffeurs.get(0);

        User chauffUser = this.userRepository.findById(chauffeur.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "L'utilisateur chauffeur n'existe pas."));

        if (!chauffeur.getAgenceVoyageId().equals(voyageDto.getAgenceVoyageId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le chauffeur n'appartient pas à l'agence de voyage.");
        }

        AgenceVoyage agenceVoyage = agenceVoyageRepository.findById(voyageDto.getAgenceVoyageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "L'agence de voyage n'existe pas."));
        ligneVoyage.setIdChauffeur(chauffeur.getChauffeurId());

        ligneVoyage.setIdLigneVoyage(UUID.randomUUID());
        ligneVoyage.setIdVoyage(voyage.getIdVoyage());

        voyage = voyageRepository.save(voyage);
        ligneVoyageRepository.save(ligneVoyage);

        // Envoyer notification de création de voyage
        try {
            notificationService.sendNotification(
                    NotificationFactory.createVoyageCreatedEvent(voyage, agenceVoyage.getUserId()));
            notificationService.sendNotification(
                    NotificationFactory.createDriverAssignedEvent(chauffeur.getUserId(), voyage));
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi des notifications de création de voyage: {}", e.getMessage());
        }

        VoyageDetailsDTO voyageDetailsDTO = voyageMapper.tovoyageDetailsDTO(voyage, agenceVoyage, classVoyage,
                vehicule, new ArrayList<>(), chauffUser);
        return voyageDetailsDTO;
    }

    public Voyage update(Voyage voyage) {
        // on peut effectuer des actions specifique d'abord
        return voyageRepository.save(voyage);
    }

    public void delete(UUID id) {
        voyageRepository.deleteById(id);
    }

    public Page<VoyageDetailsDTO> findAllByAgenceId(UUID agenceId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Récupérer toutes les lignes de voyage pour l'agence
        List<LigneVoyage> lignesVoyage = ligneVoyageRepository.findByIdAgenceVoyage(agenceId);

        if (lignesVoyage.isEmpty()) {
            return PaginationUtils.ContentToPage(new ArrayList<>(), pageable, 0);
        }

        // Extraire les IDs des voyages
        List<UUID> voyageIds = lignesVoyage.stream()
                .map(LigneVoyage::getIdVoyage)
                .collect(Collectors.toList());

        // Récupérer tous les voyages correspondants
        List<Voyage> allVoyages = new ArrayList<>();
        for (UUID voyageId : voyageIds) {
            Voyage voyage = voyageRepository.findById(voyageId).orElse(null);
            if (voyage != null) {
                allVoyages.add(voyage);
            }
        }

        // Calculer la pagination
        int start = page * size;
        int end = Math.min(start + size, allVoyages.size());
        List<Voyage> pagedVoyages = allVoyages.subList(start, end);
        List<VoyageDetailsDTO> voyageDetailsList = new ArrayList<>();
        // Mapper les voyages en VoyageDetailsDTO
        for (Voyage voyage : pagedVoyages) {
            if (voyage != null) {
                LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyage.getIdVoyage());
                ClassVoyage classVoyage = classVoyageRepository.findById(ligneVoyage.getIdClassVoyage()).orElse(null);
                AgenceVoyage agenceVoyage = agenceVoyageRepository.findById(ligneVoyage.getIdAgenceVoyage())
                        .orElse(null);
                ChauffeurAgenceVoyage chauffeur = chauffeurAgenceVoyageRepository.findById(ligneVoyage.getIdChauffeur())
                        .orElse(null);
                User chauffeurUser = null;
                if (chauffeur != null) {
                    chauffeurUser = userRepository.findById(chauffeur.getUserId()).orElse(null);
                }
                Vehicule vehicule = this.vehiculeService.findById(ligneVoyage.getIdVehicule());
                List<Integer> placesReservees = new ArrayList<>();
                List<Reservation> reservations = reservationRepository.findByIdVoyage(voyage.getIdVoyage());
                for (Reservation reservation : reservations) {
                    List<Passager> passagers = passagerRepository
                            .findAllByIdReservation(reservation.getIdReservation());
                    for (Passager passager : passagers) {
                        placesReservees.add(passager.getPlaceChoisis());
                    }
                }
                voyageDetailsList.add(voyageMapper.tovoyageDetailsDTO(voyage, agenceVoyage, classVoyage, vehicule,
                        placesReservees, chauffeurUser));
            }
        }

        long total = allVoyages.size();
        return PaginationUtils.ContentToPage(voyageDetailsList, pageable, total);
    }

    /**
     * Search voyages with flexible filters
     * Filters on cities (lieu) and optionally on zones (point) and date
     *
     * @param lieuDepart Departure city (required)
     * @param lieuArrive Arrival city (required)
     * @param pointDeDepart Departure zone (optional)
     * @param pointArrivee Arrival zone (optional)
     * @param dateDepart Departure date (optional)
     * @param page Page number
     * @param size Page size
     * @return Page of VoyagePreviewDTO matching filters
     */
    public Page<VoyagePreviewDTO> searchVoyages(
            String lieuDepart,
            String lieuArrive,
            String pointDeDepart,
            String pointArrivee,
            Date dateDepart,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Voyage> voyagesPage = voyageRepository.searchVoyages(
                lieuDepart,
                lieuArrive,
                pointDeDepart,
                pointArrivee,
                dateDepart,
                pageable
        );

        List<VoyagePreviewDTO> previewVoyageList = voyagesPage.getContent().stream()
                .map(voyage -> {
                    LigneVoyage ligneVoyage = ligneVoyageRepository.findByIdVoyage(voyage.getIdVoyage());
                    ClassVoyage classVoyage = classVoyageRepository.findById(ligneVoyage.getIdClassVoyage())
                            .orElse(null);

                    return agenceVoyageRepository.findById(ligneVoyage.getIdAgenceVoyage())
                            .map(agenceVoyage -> voyageMapper.toVoyagePreviewDTO(voyage, agenceVoyage, classVoyage))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(previewVoyageList, pageable, voyagesPage.getTotalElements());
    }

}

/*
 * 
 * Just to save
 * public Page<VoyagePreviewDTO> findAllPreview(String pagingState, String
 * previousOrNext) {
 * Pageable pageable = null;
 * Slice<Voyage> voyagesSlice = null;
 * if (pagingState == null) {
 * pageable = PageRequest.of(0, PAGE_SIZE);
 * voyagesSlice = voyageRepository.findAll(pageable);
 * } else {
 * System.out.println(pagingState);
 * pageable = PageRequest.of(0, PAGE_SIZE);
 * PagingState pagingState1 = PagingState.fromString(pagingState);
 * CassandraPageRequest cassandraPageRequest = CassandraPageRequest.of(pageable,
 * pagingState1.getRawPagingState());
 * voyagesSlice = voyageRepository.findAll(cassandraPageRequest);
 * }
 * pageable = voyagesSlice.getPageable();
 * /*
 * if (page > 0) {
 * for (int i = 0; i < page; i++) {
 * 
 * pageable = voyagesSlice.nextPageable();
 * voyagesSlice = voyageRepository.findAll(pageable);
 * }
 * }
 * 
 * // Récupère tous les voyages et les traite en un flux
 * List<VoyagePreviewDTO> previewVoyageList = voyagesSlice.stream()
 * .map(voyage -> {
 * // Récupère la première ligne de voyage associée
 * LigneVoyage ligneVoyage =
 * ligneVoyageRepository.findByIdVoyage(voyage.getIdVoyage());
 * 
 * ClassVoyage classVoyage =
 * classVoyageRepository.findById(ligneVoyage.getIdClassVoyage())
 * .orElse(null);
 * 
 * // Trouve l'agence associée et mappe les informations si présente
 * return userRepository.findById(ligneVoyage.getIdAgenceVoyage())
 * .map(agenceVoyage -> voyageMapper.toVoyagePreviewDTO(voyage, agenceVoyage,
 * classVoyage))
 * .orElse(null); // Retourne null si aucune agence n'est trouvée
 * })
 * .filter(Objects::nonNull) // Exclut les valeurs null
 * .collect(Collectors.toList()); // Convertit en liste
 * 
 * long total = voyageRepository.count();
 * 
 * return PaginationUtils.ContentToPage(previewVoyageList, pageable, total);
 * }
 * 
 */