package com.enspy26.gi.database_agence_voyage.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organization")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {

    @Id
    private UUID id;

    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @Column(name = "updatedat")
    private LocalDateTime updatedAt;

    @Column(name = "deletedat")
    private LocalDateTime deletedAt;

    @Column(name = "createdby")
    private UUID createdBy;

    @Column(name = "updatedby")
    private UUID updatedBy;

    @Column(name = "organizationid")
    private UUID organizationId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "organization_business_domains", joinColumns = @JoinColumn(name = "organization_id"))
    @Column(name = "business_domain")
    private List<UUID> businessDomains;

    private String email;

    @Column(name = "shortname")
    private String shortName;

    @Column(name = "longname")
    private String longName;

    private String description;

    @Column(name = "logourl")
    private String logoUrl;

    @Column(name = "isindividualbusiness")
    private boolean isIndividualBusiness;

    @Column(name = "legalform")
    private String legalForm;

    @Column(name = "isactive")
    private boolean isActive;

    @Column(name = "websiteurl")
    private String websiteUrl;

    @Column(name = "socialnetwork")
    private String socialNetwork;

    @Column(name = "businessregistrationnumber")
    private String businessRegistrationNumber;

    @Column(name = "taxnumber")
    private String taxNumber;

    @Column(name = "capitalshare")
    private Double capitalShare;

    @Column(name = "registrationdate")
    private LocalDateTime registrationDate;

    @Column(name = "ceoname")
    private String ceoName;

    @Column(name = "yearfounded")
    private LocalDateTime yearFounded;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "organization_keywords", joinColumns = @JoinColumn(name = "organization_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    private String status;
}
