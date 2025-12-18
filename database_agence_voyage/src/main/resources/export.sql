--
-- PostgreSQL database dump
--

\restrict P7v7Sl8NFYSl51CdfCLuZbsBuFAkkdhfAiiIFPG3Qlh2lK4vU4DinQR8iDSVbgu

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

-- Started on 2025-12-18 10:32:52

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
-- TOC entry 5198 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: pg_database_owner
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- TOC entry 912 (class 1247 OID 16429)
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
-- TOC entry 235 (class 1259 OID 16554)
-- Name: agencevoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.agencevoyage (
    agencyid uuid DEFAULT gen_random_uuid() NOT NULL,
    organisationid uuid,
    userid uuid,
    longname character varying(255),
    shortname character varying(255),
    location character varying(255),
    socialnetwork character varying(255),
    description character varying(255),
    greetingmessage character varying(255),
    ville character varying(255),
    statut_validation character varying(50) DEFAULT 'EN_ATTENTE'::character varying,
    bsm_validator_id uuid,
    date_validation timestamp without time zone,
    motif_rejet text
);


ALTER TABLE public.agencevoyage OWNER TO postgres;

--
-- TOC entry 5199 (class 0 OID 0)
-- Dependencies: 235
-- Name: COLUMN agencevoyage.statut_validation; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.agencevoyage.statut_validation IS 'Validation status: EN_ATTENTE, VALIDEE, REJETEE, SUSPENDUE';


--
-- TOC entry 5200 (class 0 OID 0)
-- Dependencies: 235
-- Name: COLUMN agencevoyage.bsm_validator_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.agencevoyage.bsm_validator_id IS 'BSM who validated or rejected the agency';


--
-- TOC entry 5201 (class 0 OID 0)
-- Dependencies: 235
-- Name: COLUMN agencevoyage.date_validation; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.agencevoyage.date_validation IS 'Date of validation or rejection by BSM';


--
-- TOC entry 5202 (class 0 OID 0)
-- Dependencies: 235
-- Name: COLUMN agencevoyage.motif_rejet; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.agencevoyage.motif_rejet IS 'Rejection reason if agency was rejected';


--
-- TOC entry 232 (class 1259 OID 16527)
-- Name: app_user; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_user (
    userid uuid DEFAULT gen_random_uuid() NOT NULL,
    nom character varying(255),
    prenom character varying(255),
    email character varying(255),
    password character varying(255),
    telnumber character varying(255),
    role character varying(255),
    address character varying(255),
    idcoordonneegps uuid,
    username character varying(255),
    genre smallint,
    businessactortype smallint
);


ALTER TABLE public.app_user OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16430)
-- Name: baggage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.baggage (
    idbaggage uuid DEFAULT gen_random_uuid() NOT NULL,
    nbrebaggage character varying(255),
    idpassager uuid
);


ALTER TABLE public.baggage OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 16572)
-- Name: chauffeuragencevoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chauffeuragencevoyage (
    chauffeurid uuid DEFAULT gen_random_uuid() NOT NULL,
    agencevoyageid uuid,
    userid uuid,
    statuschauffeur character varying(255)
);


ALTER TABLE public.chauffeuragencevoyage OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16439)
-- Name: classvoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.classvoyage (
    idclassvoyage uuid DEFAULT gen_random_uuid() NOT NULL,
    nom character varying(255),
    prix double precision,
    tauxannulation double precision,
    idagencevoyage uuid
);


ALTER TABLE public.classvoyage OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16448)
-- Name: coordonnee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.coordonnee (
    idcoordonnee uuid DEFAULT gen_random_uuid() NOT NULL,
    latitude character varying(255),
    longitude character varying(255),
    altitude character varying(255)
);


ALTER TABLE public.coordonnee OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16457)
-- Name: coupon; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.coupon (
    idcoupon uuid DEFAULT gen_random_uuid() NOT NULL,
    datedebut timestamp without time zone,
    datefin timestamp without time zone,
    statuscoupon character varying(255),
    valeur double precision,
    idhistorique uuid,
    idsoldeindemnisation uuid
);


ALTER TABLE public.coupon OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 16581)
-- Name: employeagencevoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employeagencevoyage (
    employeid uuid DEFAULT gen_random_uuid() NOT NULL,
    agencevoyageid uuid,
    userid uuid,
    poste character varying(255),
    dateembauche timestamp without time zone,
    datefincontrat timestamp without time zone,
    statutemploye character varying(255),
    salaire double precision,
    departement character varying(255),
    managerid uuid
);


ALTER TABLE public.employeagencevoyage OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16466)
-- Name: historique; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.historique (
    idhistorique uuid DEFAULT gen_random_uuid() NOT NULL,
    statushistorique character varying(255),
    datereservation timestamp without time zone,
    dateconfirmation timestamp without time zone,
    dateannulation timestamp without time zone,
    causeannulation character varying(255),
    origineannulation character varying(255),
    tauxannulation double precision,
    compensation double precision,
    idreservation uuid
);


ALTER TABLE public.historique OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 16745)
-- Name: ligne_voyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ligne_voyage (
    idlignevoyage uuid NOT NULL,
    idagencevoyage uuid NOT NULL,
    idchauffeur uuid NOT NULL,
    idclassvoyage uuid NOT NULL,
    idvehicule uuid NOT NULL,
    idvoyage uuid NOT NULL
);


ALTER TABLE public.ligne_voyage OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16475)
-- Name: lignevoyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lignevoyage (
    idlignevoyage uuid DEFAULT gen_random_uuid() NOT NULL,
    idclassvoyage uuid,
    idvehicule uuid,
    idvoyage uuid,
    idagencevoyage uuid,
    idchauffeur uuid
);


ALTER TABLE public.lignevoyage OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 16563)
-- Name: organization; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    createdat timestamp without time zone,
    updatedat timestamp without time zone,
    deletedat timestamp without time zone,
    createdby uuid,
    updatedby uuid,
    businessdomains uuid[],
    email character varying(255),
    shortname character varying(255),
    longname character varying(255),
    description character varying(255),
    logourl character varying(255),
    isindividualbusiness boolean,
    legalform character varying(255),
    isactive boolean,
    websiteurl character varying(255),
    socialnetwork character varying(255),
    businessregistrationnumber character varying(255),
    taxnumber character varying(255),
    capitalshare double precision,
    registrationdate timestamp without time zone,
    ceoname character varying(255),
    yearfounded timestamp without time zone,
    keywords text[],
    status character varying(255),
    organizationid uuid,
    agenceid uuid
);


ALTER TABLE public.organization OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 16828)
-- Name: organization_business_domains; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization_business_domains (
    organization_id uuid NOT NULL,
    business_domain uuid
);


ALTER TABLE public.organization_business_domains OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 16832)
-- Name: organization_keywords; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.organization_keywords (
    organization_id uuid NOT NULL,
    keyword character varying(255)
);


ALTER TABLE public.organization_keywords OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16482)
-- Name: passager; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.passager (
    idpassager uuid DEFAULT gen_random_uuid() NOT NULL,
    numeropieceidentific character varying(255),
    nom character varying(255),
    genre character varying(255),
    age integer,
    nbrbaggage integer,
    idreservation uuid,
    placechoisis integer,
    id_passager uuid NOT NULL
);


ALTER TABLE public.passager OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 16856)
-- Name: politique_annulation_liste_taux_periode; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.politique_annulation_liste_taux_periode (
    politique_annulation_id_politique uuid CONSTRAINT politique_annulation_liste__politique_annulation_id_po_not_null NOT NULL,
    compensation double precision,
    date_debut timestamp(6) without time zone,
    date_fin timestamp(6) without time zone,
    taux double precision
);


ALTER TABLE public.politique_annulation_liste_taux_periode OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16491)
-- Name: politiqueannulation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.politiqueannulation (
    idpolitique uuid DEFAULT gen_random_uuid() NOT NULL,
    listetauxperiode public.tauxperiode[],
    dureecoupon bigint,
    idagencevoyage uuid,
    id_politique uuid NOT NULL
);


ALTER TABLE public.politiqueannulation OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16500)
-- Name: reservation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reservation (
    idreservation uuid DEFAULT gen_random_uuid() NOT NULL,
    datereservation timestamp without time zone,
    dateconfirmation timestamp without time zone,
    nbrpassager integer,
    prixtotal double precision,
    statutreservation text,
    iduser uuid,
    idvoyage uuid,
    statutpayement text,
    transactioncode text,
    montantpaye double precision,
    id_reservation uuid NOT NULL,
    date_confirmation timestamp(6) without time zone,
    date_reservation timestamp(6) without time zone NOT NULL,
    id_user uuid NOT NULL,
    id_voyage uuid NOT NULL,
    montant_paye double precision,
    nbr_passager integer NOT NULL,
    prix_total double precision NOT NULL,
    statut_payement character varying(255),
    statut_reservation character varying(255) NOT NULL,
    transaction_code character varying(255),
    CONSTRAINT reservation_statut_payement_check CHECK (((statut_payement)::text = ANY ((ARRAY['PENDING'::character varying, 'NO_PAYMENT'::character varying, 'PAID'::character varying, 'FAILED'::character varying])::text[]))),
    CONSTRAINT reservation_statut_reservation_check CHECK (((statut_reservation)::text = ANY ((ARRAY['RESERVER'::character varying, 'CONFIRMER'::character varying, 'ANNULER'::character varying, 'VALIDER'::character varying])::text[])))
);


ALTER TABLE public.reservation OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16509)
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    libelle character varying(255)
);


ALTER TABLE public.role OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16518)
-- Name: soldeindemnisation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.soldeindemnisation (
    idsolde uuid DEFAULT gen_random_uuid() NOT NULL,
    solde double precision,
    type character varying(255),
    iduser uuid,
    idagencevoyage uuid,
    id_solde uuid NOT NULL,
    id_agence_voyage uuid NOT NULL,
    id_user uuid NOT NULL
);


ALTER TABLE public.soldeindemnisation OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 16536)
-- Name: vehicule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vehicule (
    idvehicule uuid DEFAULT gen_random_uuid() NOT NULL,
    nom character varying(255),
    modele character varying(255),
    description character varying(255),
    nbrplaces integer,
    plaquematricule character varying(255),
    lienphoto character varying(255),
    idagencevoyage uuid
);


ALTER TABLE public.vehicule OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 16545)
-- Name: voyage; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.voyage (
    idvoyage uuid DEFAULT gen_random_uuid() NOT NULL,
    titre character varying(255),
    description character varying(255),
    datedepartprev timestamp without time zone,
    lieudepart character varying(255),
    datedeparteffectif timestamp without time zone,
    datearriveeffectif timestamp without time zone,
    lieuarrive character varying(255),
    heuredeparteffectif timestamp without time zone,
    pointdedepart character varying(255),
    pointarrivee character varying(255),
    dureevoyage bigint,
    heurearrive timestamp without time zone,
    nbrplacereservable integer,
    nbrplacereserve integer,
    nbrplaceconfirm integer,
    nbrplacerestante integer,
    datepublication timestamp without time zone,
    datelimitereservation timestamp without time zone,
    datelimiteconfirmation timestamp without time zone,
    statusvoyage character varying(255),
    smallimage character varying(255),
    bigimage character varying(255),
    amenities text
);


ALTER TABLE public.voyage OWNER TO postgres;

--
-- TOC entry 5031 (class 2606 OID 16562)
-- Name: agencevoyage agencevoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agencevoyage
    ADD CONSTRAINT agencevoyage_pkey PRIMARY KEY (agencyid);


--
-- TOC entry 5025 (class 2606 OID 16535)
-- Name: app_user app_user_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (userid);


--
-- TOC entry 5003 (class 2606 OID 16438)
-- Name: baggage baggage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.baggage
    ADD CONSTRAINT baggage_pkey PRIMARY KEY (idbaggage);


--
-- TOC entry 5037 (class 2606 OID 16580)
-- Name: chauffeuragencevoyage chauffeuragencevoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chauffeuragencevoyage
    ADD CONSTRAINT chauffeuragencevoyage_pkey PRIMARY KEY (chauffeurid);


--
-- TOC entry 5005 (class 2606 OID 16447)
-- Name: classvoyage classvoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.classvoyage
    ADD CONSTRAINT classvoyage_pkey PRIMARY KEY (idclassvoyage);


--
-- TOC entry 5007 (class 2606 OID 16456)
-- Name: coordonnee coordonnee_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.coordonnee
    ADD CONSTRAINT coordonnee_pkey PRIMARY KEY (idcoordonnee);


--
-- TOC entry 5009 (class 2606 OID 16465)
-- Name: coupon coupon_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.coupon
    ADD CONSTRAINT coupon_pkey PRIMARY KEY (idcoupon);


--
-- TOC entry 5039 (class 2606 OID 16589)
-- Name: employeagencevoyage employeagencevoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employeagencevoyage
    ADD CONSTRAINT employeagencevoyage_pkey PRIMARY KEY (employeid);


--
-- TOC entry 5011 (class 2606 OID 16474)
-- Name: historique historique_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.historique
    ADD CONSTRAINT historique_pkey PRIMARY KEY (idhistorique);


--
-- TOC entry 5041 (class 2606 OID 16755)
-- Name: ligne_voyage ligne_voyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ligne_voyage
    ADD CONSTRAINT ligne_voyage_pkey PRIMARY KEY (idlignevoyage);


--
-- TOC entry 5013 (class 2606 OID 16481)
-- Name: lignevoyage lignevoyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lignevoyage
    ADD CONSTRAINT lignevoyage_pkey PRIMARY KEY (idlignevoyage);


--
-- TOC entry 5035 (class 2606 OID 16571)
-- Name: organization organization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id);


--
-- TOC entry 5015 (class 2606 OID 16490)
-- Name: passager passager_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.passager
    ADD CONSTRAINT passager_pkey PRIMARY KEY (idpassager);


--
-- TOC entry 5017 (class 2606 OID 16499)
-- Name: politiqueannulation politiqueannulation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.politiqueannulation
    ADD CONSTRAINT politiqueannulation_pkey PRIMARY KEY (idpolitique);


--
-- TOC entry 5019 (class 2606 OID 16508)
-- Name: reservation reservation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reservation
    ADD CONSTRAINT reservation_pkey PRIMARY KEY (idreservation);


--
-- TOC entry 5021 (class 2606 OID 16517)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 5023 (class 2606 OID 16526)
-- Name: soldeindemnisation soldeindemnisation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.soldeindemnisation
    ADD CONSTRAINT soldeindemnisation_pkey PRIMARY KEY (idsolde);


--
-- TOC entry 5027 (class 2606 OID 16544)
-- Name: vehicule vehicule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vehicule
    ADD CONSTRAINT vehicule_pkey PRIMARY KEY (idvehicule);


--
-- TOC entry 5029 (class 2606 OID 16553)
-- Name: voyage voyage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.voyage
    ADD CONSTRAINT voyage_pkey PRIMARY KEY (idvoyage);


--
-- TOC entry 5032 (class 1259 OID 17010)
-- Name: idx_agence_statut_validation; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_agence_statut_validation ON public.agencevoyage USING btree (statut_validation);


--
-- TOC entry 5033 (class 1259 OID 17011)
-- Name: idx_agence_ville_statut; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_agence_ville_statut ON public.agencevoyage USING btree (ville, statut_validation);


--
-- TOC entry 5045 (class 2606 OID 16974)
-- Name: politique_annulation_liste_taux_periode fk33y1kxnvx5ged3sidllv2jdv4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.politique_annulation_liste_taux_periode
    ADD CONSTRAINT fk33y1kxnvx5ged3sidllv2jdv4 FOREIGN KEY (politique_annulation_id_politique) REFERENCES public.politiqueannulation(idpolitique);


--
-- TOC entry 5042 (class 2606 OID 17005)
-- Name: agencevoyage fk_agence_bsm_validator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agencevoyage
    ADD CONSTRAINT fk_agence_bsm_validator FOREIGN KEY (bsm_validator_id) REFERENCES public.app_user(userid);


--
-- TOC entry 5044 (class 2606 OID 16969)
-- Name: organization_keywords fkq29f42todjfucy9lo14ax3v0y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_keywords
    ADD CONSTRAINT fkq29f42todjfucy9lo14ax3v0y FOREIGN KEY (organization_id) REFERENCES public.organization(id);


--
-- TOC entry 5043 (class 2606 OID 16964)
-- Name: organization_business_domains fksgeo5qecw4ekjhwff0t5ew3ko; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.organization_business_domains
    ADD CONSTRAINT fksgeo5qecw4ekjhwff0t5ew3ko FOREIGN KEY (organization_id) REFERENCES public.organization(id);


-- Completed on 2025-12-18 10:32:52

--
-- PostgreSQL database dump complete
--

\unrestrict P7v7Sl8NFYSl51CdfCLuZbsBuFAkkdhfAiiIFPG3Qlh2lK4vU4DinQR8iDSVbgu

