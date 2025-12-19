--
-- PostgreSQL database dump
--

\restrict bONQAxKU5ALv2jQZRhdxjVKVUEqh2Np7jDiUbieq43xGQRv3ZNKduh81KXtGW5M

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

-- Started on 2025-12-19 21:07:18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: pg_database_owner
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO pg_database_owner;

--
-- TOC entry 5181 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 911 (class 1247 OID 16429)
-- Name: tauxperiode; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.tauxperiode AS (
	datedebut timestamp without time zone,
	datefin timestamp without time zone,
	taux double precision,
	compensation double precision
);


ALTER TYPE public.tauxperiode OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 221 (class 1259 OID 17274)
-- Name: agencevoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.agencevoyage (
    date_validation timestamp(6) without time zone,
    agencyid uuid NOT NULL,
    bsm_validator_id uuid,
    organisationid uuid,
    userid uuid,
    description character varying(255),
    greetingmessage character varying(255),
    location character varying(255),
    longname character varying(255),
    motif_rejet character varying(255),
    shortname character varying(255),
    socialnetwork character varying(255),
    statut_validation character varying(255),
    ville character varying(255),
    CONSTRAINT agencevoyage_statut_validation_check CHECK (((statut_validation)::text = ANY ((ARRAY['EN_ATTENTE'::character varying, 'VALIDEE'::character varying, 'REJETEE'::character varying, 'SUSPENDUE'::character varying])::text[])))
);


ALTER TABLE public.agencevoyage OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 17283)
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user (
    businessactortype smallint,
    genre smallint,
    idcoordonneegps uuid,
    userid uuid NOT NULL,
    address character varying(255),
    email character varying(255),
    nom character varying(255),
    password character varying(255),
    prenom character varying(255),
    role character varying(255),
    telnumber character varying(255),
    username character varying(255) NOT NULL,
    CONSTRAINT app_user_businessactortype_check CHECK (((businessactortype >= 0) AND (businessactortype <= 2))),
    CONSTRAINT app_user_genre_check CHECK (((genre >= 0) AND (genre <= 1)))
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 17296)
-- Name: baggage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.baggage (
    idbaggage uuid NOT NULL,
    idpassager uuid,
    nbrebaggage character varying(255)
);


ALTER TABLE public.baggage OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 17302)
-- Name: chauffeuragencevoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chauffeuragencevoyage (
    agencevoyageid uuid,
    chauffeurid uuid NOT NULL,
    userid uuid,
    statuschauffeur character varying(255),
    CONSTRAINT chauffeuragencevoyage_statuschauffeur_check CHECK (((statuschauffeur)::text = ANY ((ARRAY['OCCUPE'::character varying, 'LIBRE'::character varying, 'REPOS'::character varying])::text[])))
);


ALTER TABLE public.chauffeuragencevoyage OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 17309)
-- Name: classvoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.classvoyage (
    prix double precision,
    tauxannulation double precision,
    idagencevoyage uuid,
    idclassvoyage uuid NOT NULL,
    nom character varying(255)
);


ALTER TABLE public.classvoyage OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 17315)
-- Name: coordonnee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.coordonnee (
    idcoordonnee uuid NOT NULL,
    altitude character varying(255),
    latitude character varying(255),
    longitude character varying(255)
);


ALTER TABLE public.coordonnee OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 17323)
-- Name: coupon; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.coupon (
    valeur double precision,
    datedebut timestamp(6) without time zone,
    datefin timestamp(6) without time zone,
    idcoupon uuid NOT NULL,
    idhistorique uuid,
    idsoldeindemnisation uuid,
    statuscoupon character varying(255),
    CONSTRAINT coupon_statuscoupon_check CHECK (((statuscoupon)::text = ANY ((ARRAY['VALIDE'::character varying, 'EXPIRER'::character varying, 'UTILISER'::character varying])::text[])))
);


ALTER TABLE public.coupon OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 17330)
-- Name: employeagencevoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employeagencevoyage (
    salaire double precision,
    dateembauche timestamp(6) without time zone,
    datefincontrat timestamp(6) without time zone,
    agencevoyageid uuid NOT NULL,
    employeid uuid NOT NULL,
    managerid uuid,
    userid uuid NOT NULL,
    departement character varying(255),
    poste character varying(255),
    statutemploye character varying(255),
    CONSTRAINT employeagencevoyage_statutemploye_check CHECK (((statutemploye)::text = ANY ((ARRAY['ACTIF'::character varying, 'INACTIF'::character varying, 'SUSPENDU'::character varying, 'EN_CONGE'::character varying, 'DEMISSIONNE'::character varying, 'LICENCIE'::character varying])::text[])))
);


ALTER TABLE public.employeagencevoyage OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 17341)
-- Name: historique; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.historique (
    compensation double precision NOT NULL,
    tauxannulation double precision NOT NULL,
    dateannulation timestamp(6) without time zone,
    dateconfirmation timestamp(6) without time zone,
    datereservation timestamp(6) without time zone,
    idhistorique uuid NOT NULL,
    idreservation uuid NOT NULL,
    causeannulation character varying(255),
    origineannulation character varying(255),
    statushistorique character varying(255) NOT NULL,
    CONSTRAINT historique_statushistorique_check CHECK (((statushistorique)::text = ANY ((ARRAY['ANNULER_PAR_AGENCE_APRES_RESERVATION'::character varying, 'ANNULER_PAR_USAGER_APRES_RESERVATION'::character varying, 'ANNULER_PAR_AGENCE_APRES_CONFIRMATION'::character varying, 'ANNULER_PAR_USAGER_APRES_CONFIRMATION'::character varying, 'VALIDER'::character varying])::text[])))
);


ALTER TABLE public.historique OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 17354)
-- Name: ligne_voyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ligne_voyage (
    idagencevoyage uuid NOT NULL,
    idchauffeur uuid NOT NULL,
    idclassvoyage uuid NOT NULL,
    idlignevoyage uuid NOT NULL,
    idvehicule uuid NOT NULL,
    idvoyage uuid NOT NULL
);


ALTER TABLE public.ligne_voyage OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 17365)
-- Name: organization; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization (
    capitalshare double precision,
    isactive boolean,
    isindividualbusiness boolean,
    createdat timestamp(6) without time zone,
    deletedat timestamp(6) without time zone,
    registrationdate timestamp(6) without time zone,
    updatedat timestamp(6) without time zone,
    yearfounded timestamp(6) without time zone,
    createdby uuid,
    id uuid NOT NULL,
    organizationid uuid,
    updatedby uuid,
    businessregistrationnumber character varying(255),
    ceoname character varying(255),
    description character varying(255),
    email character varying(255),
    legalform character varying(255),
    logourl character varying(255),
    longname character varying(255),
    shortname character varying(255),
    socialnetwork character varying(255),
    status character varying(255),
    taxnumber character varying(255),
    websiteurl character varying(255)
);


ALTER TABLE public.organization OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 17373)
-- Name: organization_business_domains; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization_business_domains (
    business_domain uuid,
    organization_id uuid NOT NULL
);


ALTER TABLE public.organization_business_domains OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 17377)
-- Name: organization_keywords; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization_keywords (
    organization_id uuid NOT NULL,
    keyword character varying(255)
);


ALTER TABLE public.organization_keywords OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 17381)
-- Name: passager; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.passager (
    age integer NOT NULL,
    nbrbaggage integer,
    placechoisis integer,
    id_passager uuid NOT NULL,
    idreservation uuid,
    genre character varying(255),
    nom character varying(255),
    numeropieceidentific character varying(255)
);


ALTER TABLE public.passager OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 17396)
-- Name: politique_annulation_liste_taux_periode; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.politique_annulation_liste_taux_periode (
    compensation double precision,
    taux double precision,
    date_debut timestamp(6) without time zone,
    date_fin timestamp(6) without time zone,
    politique_annulation_id_politique uuid CONSTRAINT politique_annulation_liste__politique_annulation_id_po_not_null NOT NULL
);


ALTER TABLE public.politique_annulation_liste_taux_periode OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 17390)
-- Name: politiqueannulation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.politiqueannulation (
    dureecoupon bigint,
    id_politique uuid NOT NULL,
    idagencevoyage uuid
);


ALTER TABLE public.politiqueannulation OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 17400)
-- Name: reservation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reservation (
    montant_paye double precision,
    nbr_passager integer NOT NULL,
    prix_total double precision NOT NULL,
    date_confirmation timestamp(6) without time zone,
    date_reservation timestamp(6) without time zone NOT NULL,
    id_reservation uuid NOT NULL,
    id_user uuid NOT NULL,
    id_voyage uuid NOT NULL,
    statut_payement character varying(255),
    statut_reservation character varying(255) NOT NULL,
    transaction_code character varying(255),
    CONSTRAINT reservation_statut_payement_check CHECK (((statut_payement)::text = ANY ((ARRAY['PENDING'::character varying, 'NO_PAYMENT'::character varying, 'PAID'::character varying, 'FAILED'::character varying])::text[]))),
    CONSTRAINT reservation_statut_reservation_check CHECK (((statut_reservation)::text = ANY ((ARRAY['RESERVER'::character varying, 'CONFIRMER'::character varying, 'ANNULER'::character varying, 'VALIDER'::character varying])::text[])))
);


ALTER TABLE public.reservation OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 17416)
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    id uuid NOT NULL,
    libelle character varying(255) NOT NULL,
    CONSTRAINT role_libelle_check CHECK (((libelle)::text = ANY ((ARRAY['USAGER'::character varying, 'EMPLOYE'::character varying, 'AGENCE_VOYAGE'::character varying, 'ORGANISATION'::character varying, 'BSM'::character varying])::text[])))
);


ALTER TABLE public.role OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 17424)
-- Name: soldeindemnisation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.soldeindemnisation (
    solde double precision NOT NULL,
    id_agence_voyage uuid NOT NULL,
    id_solde uuid NOT NULL,
    id_user uuid NOT NULL,
    type character varying(255) NOT NULL
);


ALTER TABLE public.soldeindemnisation OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 17434)
-- Name: vehicule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehicule (
    nbrplaces integer NOT NULL,
    idagencevoyage uuid,
    idvehicule uuid NOT NULL,
    description character varying(255),
    lienphoto character varying(255),
    modele character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    plaquematricule character varying(255)
);


ALTER TABLE public.vehicule OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 17445)
-- Name: voyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.voyage (
    nbrplaceconfirm integer NOT NULL,
    nbrplacereservable integer NOT NULL,
    nbrplacereserve integer NOT NULL,
    nbrplacerestante integer NOT NULL,
    datearriveeffectif timestamp(6) without time zone,
    datedeparteffectif timestamp(6) without time zone,
    datedepartprev timestamp(6) without time zone,
    datelimiteconfirmation timestamp(6) without time zone,
    datelimitereservation timestamp(6) without time zone,
    datepublication timestamp(6) without time zone,
    dureevoyage bigint,
    heurearrive timestamp(6) without time zone,
    heuredeparteffectif timestamp(6) without time zone,
    idvoyage uuid NOT NULL,
    amenities text,
    bigimage character varying(255),
    description character varying(255),
    lieuarrive character varying(255),
    lieudepart character varying(255),
    pointarrivee character varying(255),
    pointdedepart character varying(255),
    smallimage character varying(255),
    statusvoyage character varying(255),
    titre character varying(255) NOT NULL,
    CONSTRAINT voyage_statusvoyage_check CHECK (((statusvoyage)::text = ANY ((ARRAY['EN_ATTENTE'::character varying, 'PUBLIE'::character varying, 'EN_COURS'::character varying, 'TERMINE'::character varying, 'ANNULE'::character varying])::text[])))
);


ALTER TABLE public.voyage OWNER TO postgres;

--
-- TOC entry 4989 (class 2606 OID 17282)
-- Name: agencevoyage agencevoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agencevoyage
    ADD CONSTRAINT agencevoyage_pkey PRIMARY KEY (agencyid);


--
-- TOC entry 4991 (class 2606 OID 17293)
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (userid);


--
-- TOC entry 4993 (class 2606 OID 17295)
-- Name: app_user app_user_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_username_key UNIQUE (username);


--
-- TOC entry 4995 (class 2606 OID 17301)
-- Name: baggage baggage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.baggage
    ADD CONSTRAINT baggage_pkey PRIMARY KEY (idbaggage);


--
-- TOC entry 4997 (class 2606 OID 17308)
-- Name: chauffeuragencevoyage chauffeuragencevoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chauffeuragencevoyage
    ADD CONSTRAINT chauffeuragencevoyage_pkey PRIMARY KEY (chauffeurid);


--
-- TOC entry 4999 (class 2606 OID 17314)
-- Name: classvoyage classvoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.classvoyage
    ADD CONSTRAINT classvoyage_pkey PRIMARY KEY (idclassvoyage);


--
-- TOC entry 5001 (class 2606 OID 17322)
-- Name: coordonnee coordonnee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.coordonnee
    ADD CONSTRAINT coordonnee_pkey PRIMARY KEY (idcoordonnee);


--
-- TOC entry 5003 (class 2606 OID 17329)
-- Name: coupon coupon_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.coupon
    ADD CONSTRAINT coupon_pkey PRIMARY KEY (idcoupon);


--
-- TOC entry 5005 (class 2606 OID 17340)
-- Name: employeagencevoyage employeagencevoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employeagencevoyage
    ADD CONSTRAINT employeagencevoyage_pkey PRIMARY KEY (employeid);


--
-- TOC entry 5007 (class 2606 OID 17353)
-- Name: historique historique_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historique
    ADD CONSTRAINT historique_pkey PRIMARY KEY (idhistorique);


--
-- TOC entry 5009 (class 2606 OID 17364)
-- Name: ligne_voyage ligne_voyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_voyage
    ADD CONSTRAINT ligne_voyage_pkey PRIMARY KEY (idlignevoyage);


--
-- TOC entry 5011 (class 2606 OID 17372)
-- Name: organization organization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id);


--
-- TOC entry 5013 (class 2606 OID 17389)
-- Name: passager passager_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.passager
    ADD CONSTRAINT passager_pkey PRIMARY KEY (id_passager);


--
-- TOC entry 5015 (class 2606 OID 17395)
-- Name: politiqueannulation politiqueannulation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.politiqueannulation
    ADD CONSTRAINT politiqueannulation_pkey PRIMARY KEY (id_politique);


--
-- TOC entry 5017 (class 2606 OID 17415)
-- Name: reservation reservation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reservation
    ADD CONSTRAINT reservation_pkey PRIMARY KEY (id_reservation);


--
-- TOC entry 5019 (class 2606 OID 17423)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 5021 (class 2606 OID 17433)
-- Name: soldeindemnisation soldeindemnisation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.soldeindemnisation
    ADD CONSTRAINT soldeindemnisation_pkey PRIMARY KEY (id_solde);


--
-- TOC entry 5023 (class 2606 OID 17444)
-- Name: vehicule vehicule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicule
    ADD CONSTRAINT vehicule_pkey PRIMARY KEY (idvehicule);


--
-- TOC entry 5025 (class 2606 OID 17458)
-- Name: voyage voyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.voyage
    ADD CONSTRAINT voyage_pkey PRIMARY KEY (idvoyage);


--
-- TOC entry 5028 (class 2606 OID 17469)
-- Name: politique_annulation_liste_taux_periode fk33y1kxnvx5ged3sidllv2jdv4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.politique_annulation_liste_taux_periode
    ADD CONSTRAINT fk33y1kxnvx5ged3sidllv2jdv4 FOREIGN KEY (politique_annulation_id_politique) REFERENCES public.politiqueannulation(id_politique);


--
-- TOC entry 5027 (class 2606 OID 17464)
-- Name: organization_keywords fkq29f42todjfucy9lo14ax3v0y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_keywords
    ADD CONSTRAINT fkq29f42todjfucy9lo14ax3v0y FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- TOC entry 5026 (class 2606 OID 17459)
-- Name: organization_business_domains fksgeo5qecw4ekjhwff0t5ew3ko; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_business_domains
    ADD CONSTRAINT fksgeo5qecw4ekjhwff0t5ew3ko FOREIGN KEY (organization_id) REFERENCES public.organization(id);


-- Completed on 2025-12-19 21:07:18

--
-- PostgreSQL database dump complete
--

\unrestrict bONQAxKU5ALv2jQZRhdxjVKVUEqh2Np7jDiUbieq43xGQRv3ZNKduh81KXtGW5M

