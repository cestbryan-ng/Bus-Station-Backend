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
            User chauffeurUser = userRepository.findById(chauffeur.getUserId()).orElse(null);
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
        // on peut effectuer des actions spécifiques d'abord
        voyage.setIdVoyage(UUID.randomUUID());
        // Set all additional fields
        voyage.setTitre(voyageDto.getTitre());
        voyage.setDescription(voyageDto.getDescription());
        voyage.setDateDepartPrev(voyageDto.getDateDepartPrev());
        voyage.setLieuDepart(voyageDto.getLieuDepart());
        voyage.setLieuArrive(voyageDto.getLieuArrive());
        voyage.setPointDeDepart(voyageDto.getPointDeDepart());
        voyage.setPointArrivee(voyageDto.getPointArrivee());
        voyage.setHeureArrive(voyageDto.getHeureArrive());

        // Calcul de la durée du voyage à partir de la date de départ et d'arrivée
        if (voyageDto.getDateDepartPrev() != null && voyageDto.getHeureArrive() != null) {
            LocalDateTime depart = voyageDto.getDateDepartPrev().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime arrivee = voyageDto.getHeureArrive().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            Duration duree = java.time.Duration.between(depart, arrivee);
            voyage.setDureeVoyage(duree);
        }
        voyage.setHeureDepartEffectif(voyageDto.getHeureDepartEffectif());
        voyage.setNbrPlaceReservable(voyageDto.getNbrPlaceReservable());
        voyage.setNbrPlaceReserve(voyageDto.getNbrPlaceReserve());
        voyage.setNbrPlaceConfirm(voyageDto.getNbrPlaceConfirm());
        voyage.setNbrPlaceRestante(voyageDto.getNbrPlaceRestante());
        voyage.setDatePublication(new Date());
        voyage.setDateLimiteReservation(voyageDto.getDateLimiteReservation());
        voyage.setDateLimiteConfirmation(voyageDto.getDateLimiteConfirmation());
        voyage.setStatusVoyage(voyageDto.getStatusVoyage());
        voyage.setSmallImage(
                "https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=");
        voyage.setBigImage(
                "https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=");
        voyage.setAmenities(VoyageMapper.amenitiesListToString(voyageDto.getAmenities())); // Set amenities as a list

        // Création de la ligne de voyage associé
        LigneVoyage ligneVoyage = new LigneVoyage();
        if (agenceVoyageProxies.isAgenceVoyageExist(voyageDto.getAgenceVoyageId())) {
            ligneVoyage.setIdAgenceVoyage(voyageDto.getAgenceVoyageId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'agence voyage n'existe pas.");
        }
        ClassVoyage classVoyage = this.classVoyageRepository.findById(voyageDto.getClassVoyageId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La classe de voyage n'existe pas."));
        ligneVoyage.setIdClassVoyage(classVoyage.getIdClassVoyage());

        Vehicule vehicule = this.vehiculeService.findById(voyageDto.getVehiculeId());
        if (vehicule != null) {
            ligneVoyage.setIdVehicule(voyageDto.getVehiculeId());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le vehicule n'existe pas.");
        }

        // On verifie si l'utilisateur chauffeur existe et s'il appartient bien à
        // l'agence de voyage qui cree le voyage
        ChauffeurAgenceVoyage chauffeur = this.chauffeurAgenceVoyageRepository.findByUserId(voyageDto.getChauffeurId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le chauffeur n'existe pas."));
        User chauffUser = this.userRepository.findById(chauffeur.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le chauffeur n'existe pas."));
        if (!chauffeur.getAgenceVoyageId().equals(voyageDto.getAgenceVoyageId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le chauffeur n'appartient pas à l'agence de voyage.");
        }
        AgenceVoyage agenceVoyage = agenceVoyageRepository.findById(voyageDto.getAgenceVoyageId())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "L'agence de voyage n'existe pas."));
        ligneVoyage.setIdChauffeur(chauffeur.getChauffeurId());

        ligneVoyage.setIdLigneVoyage(UUID.randomUUID());
        ligneVoyage.setIdVoyage(voyage.getIdVoyage());

        voyage = voyageRepository.save(voyage);
        ligneVoyageRepository.save(ligneVoyage);

        // Envoyer notification de création de voyage
        try {
            notificationService.sendNotification(
                    NotificationFactory.createVoyageCreatedEvent(voyage, agenceVoyage.getUserId()));
            // Notifier le chauffeur assigné
            notificationService.sendNotification(
                    NotificationFactory.createDriverAssignedEvent(chauffeur.getUserId(), voyage));
        } catch (Exception e) {
            log.warn("Erreur lors de l'envoi des notifications de création de voyage: {}", e.getMessage());
        }

        VoyageDetailsDTO voyageDetailsDTO = voyageMapper.tovoyageDetailsDTO(voyage,
                agenceVoyageRepository.findById(ligneVoyage.getIdAgenceVoyage()).orElse(null), classVoyage,
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
                User chauffeurUser = userRepository.findById(chauffeur.getUserId()).orElse(null);
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
     * @param ville_depart Departure city (required)
     * @param ville_arrive Arrival city (required)
     * @param zone_depart Departure zone (optional)
     * @param zone_arrive Arrival zone (optional)
     * @param date_depart Departure date (optional)
     * @param page Page number
     * @param size Page size
     * @return Page of VoyagePreviewDTO matching filters
     */
    public Page<VoyagePreviewDTO> searchVoyages(
            String ville_depart,
            String ville_arrive,
            String zone_depart,
            String zone_arrive,
            Date date_depart,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Voyage> voyagesPage = voyageRepository.searchVoyages(
                ville_depart,
                ville_arrive,
                zone_depart,
                zone_arrive,
                date_depart,
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