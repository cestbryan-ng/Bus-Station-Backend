package com.enspy26.gi.annulation_reservation.controllers;

import com.enspy26.gi.database_agence_voyage.models.Coupon;
import com.enspy26.gi.annulation_reservation.services.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "Obtenir tous les coupons", description = "Récupère la liste de tous les coupons.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class))))
    })
    @GetMapping
    public Mono<ResponseEntity<List<Coupon>>> getAllCoupons() {
        return Mono.fromCallable(() -> couponService.findAll())
                .subscribeOn(Schedulers.boundedElastic())
                .map(coupons -> new ResponseEntity<>(coupons, HttpStatus.OK));
    }

    @Operation(summary = "Obtenir un coupon par ID", description = "Récupère un coupon spécifique par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon trouvé", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Coupon.class))),
            @ApiResponse(responseCode = "404", description = "Coupon non trouvé")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Coupon>> getCouponById(@PathVariable UUID id) {
        return Mono.fromCallable(() -> couponService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(coupon -> {
                    if (coupon == null) {
                        return new ResponseEntity<Coupon>(HttpStatus.NOT_FOUND);
                    }
                    return new ResponseEntity<>(coupon, HttpStatus.OK);
                });
    }

    @Operation(summary = "Créer un coupon", description = "Ajoute un nouveau coupon.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon créé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Coupon.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public Mono<ResponseEntity<Coupon>> createCoupon(@RequestBody Coupon coupon) {
        return Mono.fromCallable(() -> couponService.create(coupon))
                .subscribeOn(Schedulers.boundedElastic())
                .map(createdCoupon -> new ResponseEntity<>(createdCoupon, HttpStatus.CREATED));
    }

    @Operation(summary = "Mettre à jour un coupon", description = "Modifie un coupon existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon mis à jour avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Coupon.class))),
            @ApiResponse(responseCode = "404", description = "Coupon non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Coupon>> updateCoupon(@PathVariable UUID id, @RequestBody Coupon coupon) {
        return Mono.fromCallable(() -> couponService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(existingCoupon -> {
                    if (existingCoupon == null) {
                        return Mono.just(new ResponseEntity<Coupon>(HttpStatus.NOT_FOUND));
                    }
                    coupon.setIdCoupon(id);
                    return Mono.fromCallable(() -> couponService.update(coupon))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(updatedCoupon -> new ResponseEntity<>(updatedCoupon, HttpStatus.OK));
                });
    }

    @Operation(summary = "Supprimer un coupon", description = "Supprime un coupon par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Coupon supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Coupon non trouvé")
    })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCoupon(@PathVariable UUID id) {
        return Mono.fromCallable(() -> couponService.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(existingCoupon -> {
                    if (existingCoupon == null) {
                        return Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
                    }
                    return Mono.fromRunnable(() -> couponService.delete(id))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
                });
    }

    @Operation(summary = "Obtenir tous les coupons d'un utilisateur", description = "Récupère la liste de tous les coupons d'un utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Coupon.class))))
    })
    @GetMapping("/user/{userId}")
    public Mono<ResponseEntity<List<Coupon>>> getCouponsByUserId(@PathVariable(name = "userId") UUID userId) {
        return Mono.fromCallable(() -> couponService.findByUserId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(coupons -> new ResponseEntity<>(coupons, HttpStatus.OK));
    }
}
