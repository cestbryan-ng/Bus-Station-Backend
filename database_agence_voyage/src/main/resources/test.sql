--
-- PostgreSQL database dump
--

\restrict lgTefoQqWSdfnwe33ylTOMlAvSrdm8b837cv4xaX76FEJ94kh2BsSRXM1mMdckA

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

-- Started on 2026-01-17 19:16:29

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
-- TOC entry 5202 (class 0 OID 0)
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
-- TOC entry 5176 (class 0 OID 17274)
-- Dependencies: 221
-- Data for Name: agencevoyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.agencevoyage (date_validation, agencyid, bsm_validator_id, organisationid, userid, description, greetingmessage, location, longname, motif_rejet, shortname, socialnetwork, statut_validation, ville) VALUES ('2025-12-19 21:22:51.994496', '66666664-bbbb-6666-6666-666666666666', '80cd2063-e648-4ab5-aa75-c801faf5142e', '66666662-aaaa-6666-6666-666666666666', 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, NULL, 'Bonaberi', 'TRSA Douala Bonaberi', 'Documentation incomplète.', 'TRSA DLA', '@trsa_douala', 'REJETEE', 'Douala');
INSERT INTO public.agencevoyage (date_validation, agencyid, bsm_validator_id, organisationid, userid, description, greetingmessage, location, longname, motif_rejet, shortname, socialnetwork, statut_validation, ville) VALUES ('2025-12-19 21:22:51.994496', '66666661-bbbb-6666-6666-666666666666', '80cd2063-e648-4ab5-aa75-c801faf5142e', '9c68dfc5-f459-4866-905c-15647b9b6ece', 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, NULL, 'Mvan', 'EVC Yaoundé Centre', NULL, 'EVC YDE', '@evc_yaounde', 'VALIDEE', 'Yaoundé');
INSERT INTO public.agencevoyage (date_validation, agencyid, bsm_validator_id, organisationid, userid, description, greetingmessage, location, longname, motif_rejet, shortname, socialnetwork, statut_validation, ville) VALUES (NULL, '66666662-bbbb-6666-6666-666666666666', NULL, '9c68dfc5-f459-4866-905c-15647b9b6ece', 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, NULL, 'Akwa', 'EVC Douala Akwa', NULL, 'EVC DLA', '@evc_douala', 'EN_ATTENTE', 'Douala');
INSERT INTO public.agencevoyage (date_validation, agencyid, bsm_validator_id, organisationid, userid, description, greetingmessage, location, longname, motif_rejet, shortname, socialnetwork, statut_validation, ville) VALUES ('2025-12-19 21:22:51.994496', '66666663-bbbb-6666-6666-666666666666', '80cd2063-e648-4ab5-aa75-c801faf5142e', 'e23ae754-c8b2-4b3c-8377-f1818354ac3d', 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, NULL, 'Centre-ville', 'TRSA Bafoussam', NULL, 'TRSA BFM', '@trsa_bafoussam', 'VALIDEE', 'Bafoussam');


--
-- TOC entry 5177 (class 0 OID 17283)
-- Dependencies: 222
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.app_user (businessactortype, genre, idcoordonneegps, userid, address, email, nom, password, prenom, role, telnumber, username) VALUES (1, 0, NULL, '634126dd-d18b-440b-8713-4cececdc6d9a', NULL, 'ngoupeyoubryan9@gmail.com', 'Bryan', '$2a$10$p/fClXdQ1fhiKOXLEU.Bwe.VJiAVtGpvc5.SzGFm8PexRbBQwnV2C', 'Ngoupeyou', 'USAGER', '655121010', 'cestbryan');
INSERT INTO public.app_user (businessactortype, genre, idcoordonneegps, userid, address, email, nom, password, prenom, role, telnumber, username) VALUES (0, 0, NULL, 'a9abc859-2337-479d-ac4d-2c01594eaee3', NULL, 'robert@gmail.com', 'Roméo', '$2a$10$KiHOCys9QNMhx/fI7nwMYeD3aq57gqMvqw7yAsxnd5aUMy/JYIDFW', 'Robert', 'ORGANISATION', '655121012', 'orga');
INSERT INTO public.app_user (businessactortype, genre, idcoordonneegps, userid, address, email, nom, password, prenom, role, telnumber, username) VALUES (0, 0, NULL, 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, 'julio@gmail.com', 'Julio', '$2a$10$8gG1UKR4LFGWnueyrmsroOO7yKRF0PMS8pK50R9yKIlSCdMJ/RzBe', 'Hubert', 'AGENCE_VOYAGE', '655121013', 'chef');
INSERT INTO public.app_user (businessactortype, genre, idcoordonneegps, userid, address, email, nom, password, prenom, role, telnumber, username) VALUES (2, 0, NULL, '80cd2063-e648-4ab5-aa75-c801faf5142e', 'Yaoundé', 'talla@gmail.com', 'Féderic', '$2a$10$LqFXnO0K9EpIT3qCOuIIX.18U9I2agWz4sBH6uDtamqV5dTe7NSOG', 'Talla', 'BSM', '655121011', 'bsm');
INSERT INTO public.app_user (businessactortype, genre, idcoordonneegps, userid, address, email, nom, password, prenom, role, telnumber, username) VALUES (1, 0, NULL, 'd25d8e70-737b-4ccf-9ec2-f179aa8faa73', NULL, 'robertnelson@gmail.com', 'Robert', '$2a$10$P/QuZvPRUS7g0KN7fOtZQOcEo.T6XvK/oRavbF/0S6H2w9FGX4MOq', 'Nelson', 'USAGER', '655121018', 'rob');


--
-- TOC entry 5178 (class 0 OID 17296)
-- Dependencies: 223
-- Data for Name: baggage; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5179 (class 0 OID 17302)
-- Dependencies: 224
-- Data for Name: chauffeuragencevoyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.chauffeuragencevoyage (agencevoyageid, chauffeurid, userid, statuschauffeur) VALUES ('66666661-bbbb-6666-6666-666666666666', '66666661-dddd-6666-6666-666666666666', 'd25d8e70-737b-4ccf-9ec2-f179aa8faa73', 'LIBRE');
INSERT INTO public.chauffeuragencevoyage (agencevoyageid, chauffeurid, userid, statuschauffeur) VALUES ('66666663-bbbb-6666-6666-666666666666', '66666662-dddd-6666-6666-666666666666', 'd25d8e70-737b-4ccf-9ec2-f179aa8faa73', 'LIBRE');


--
-- TOC entry 5180 (class 0 OID 17309)
-- Dependencies: 225
-- Data for Name: classvoyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.classvoyage (prix, tauxannulation, idagencevoyage, idclassvoyage, nom) VALUES (15000, 0.8, '66666661-bbbb-6666-6666-666666666666', '66666661-ffff-6666-6666-666666666666', 'VIP');
INSERT INTO public.classvoyage (prix, tauxannulation, idagencevoyage, idclassvoyage, nom) VALUES (8000, 0.5, '66666661-bbbb-6666-6666-666666666666', '66666662-ffff-6666-6666-666666666666', 'ÉCONOMIQUE');
INSERT INTO public.classvoyage (prix, tauxannulation, idagencevoyage, idclassvoyage, nom) VALUES (12000, 0.8, '66666663-bbbb-6666-6666-666666666666', '66666663-ffff-6666-6666-666666666666', 'VIP');


--
-- TOC entry 5181 (class 0 OID 17315)
-- Dependencies: 226
-- Data for Name: coordonnee; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5182 (class 0 OID 17323)
-- Dependencies: 227
-- Data for Name: coupon; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5183 (class 0 OID 17330)
-- Dependencies: 228
-- Data for Name: employeagencevoyage; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5184 (class 0 OID 17341)
-- Dependencies: 229
-- Data for Name: historique; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.historique (compensation, tauxannulation, dateannulation, dateconfirmation, datereservation, idhistorique, idreservation, causeannulation, origineannulation, statushistorique) VALUES (0, 0, NULL, NULL, '2025-12-19 23:42:18.024', '4b1ca620-2366-43a3-a886-5909445744c2', '29bb2dfe-114f-42b0-9f00-c5a0a0d48b07', NULL, NULL, 'VALIDER');
INSERT INTO public.historique (compensation, tauxannulation, dateannulation, dateconfirmation, datereservation, idhistorique, idreservation, causeannulation, origineannulation, statushistorique) VALUES (0, 0, NULL, NULL, '2025-12-20 00:12:59.399', '0855c0d6-44e7-46a4-b0b2-ce2e9f35d9ea', 'b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d', NULL, NULL, 'VALIDER');
INSERT INTO public.historique (compensation, tauxannulation, dateannulation, dateconfirmation, datereservation, idhistorique, idreservation, causeannulation, origineannulation, statushistorique) VALUES (0, 0, NULL, '2026-01-16 18:36:55.468', '2025-12-24 13:54:59.786', 'd06c6e52-b23c-476c-a01f-c5740f06ccfe', '4c9d19ab-6cd2-410d-b252-e93b33c17013', NULL, NULL, 'VALIDER');


--
-- TOC entry 5185 (class 0 OID 17354)
-- Dependencies: 230
-- Data for Name: ligne_voyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.ligne_voyage (idagencevoyage, idchauffeur, idclassvoyage, idlignevoyage, idvehicule, idvoyage) VALUES ('66666661-bbbb-6666-6666-666666666666', '66666661-dddd-6666-6666-666666666666', '66666661-ffff-6666-6666-666666666666', '66666661-ef01-6666-6666-666666666666', '66666661-eeee-6666-6666-666666666666', '66666661-abcd-6666-6666-666666666666');
INSERT INTO public.ligne_voyage (idagencevoyage, idchauffeur, idclassvoyage, idlignevoyage, idvehicule, idvoyage) VALUES ('66666661-bbbb-6666-6666-666666666666', '66666661-dddd-6666-6666-666666666666', '66666662-ffff-6666-6666-666666666666', '66666662-ef01-6666-6666-666666666666', '66666661-eeee-6666-6666-666666666666', '66666662-abcd-6666-6666-666666666666');
INSERT INTO public.ligne_voyage (idagencevoyage, idchauffeur, idclassvoyage, idlignevoyage, idvehicule, idvoyage) VALUES ('66666663-bbbb-6666-6666-666666666666', '66666662-dddd-6666-6666-666666666666', '66666663-ffff-6666-6666-666666666666', '66666663-ef01-6666-6666-666666666666', '66666662-eeee-6666-6666-666666666666', '66666663-abcd-6666-6666-666666666666');


--
-- TOC entry 5186 (class 0 OID 17365)
-- Dependencies: 231
-- Data for Name: organization; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.organization (capitalshare, isactive, isindividualbusiness, createdat, deletedat, registrationdate, updatedat, yearfounded, createdby, id, organizationid, updatedby, businessregistrationnumber, ceoname, description, email, legalform, logourl, longname, shortname, socialnetwork, status, taxnumber, websiteurl) VALUES (NULL, true, false, '2025-12-19 21:22:51.994496', NULL, NULL, NULL, NULL, 'a9abc859-2337-479d-ac4d-2c01594eaee3', '9c68dfc5-f459-4866-905c-15647b9b6ece', '66666661-aaaa-6666-6666-666666666666', NULL, '01234567', NULL, NULL, 'contact@expressvoyage.cm', NULL, NULL, 'Express Voyage Cameroun', 'EVC', '@expressvoyage', 'VALIDEE', NULL, NULL);
INSERT INTO public.organization (capitalshare, isactive, isindividualbusiness, createdat, deletedat, registrationdate, updatedat, yearfounded, createdby, id, organizationid, updatedby, businessregistrationnumber, ceoname, description, email, legalform, logourl, longname, shortname, socialnetwork, status, taxnumber, websiteurl) VALUES (NULL, true, false, '2025-12-19 21:22:51.994496', NULL, NULL, NULL, NULL, 'a9abc859-2337-479d-ac4d-2c01594eaee3', 'e23ae754-c8b2-4b3c-8377-f1818354ac3d', '66666662-aaaa-6666-6666-666666666666', NULL, 'SC123456', NULL, NULL, 'info@transportrapide.cm', NULL, NULL, 'Transport Rapide SA', 'TRSA', '@transportrapide', 'VALIDEE', NULL, NULL);


--
-- TOC entry 5187 (class 0 OID 17373)
-- Dependencies: 232
-- Data for Name: organization_business_domains; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5188 (class 0 OID 17377)
-- Dependencies: 233
-- Data for Name: organization_keywords; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5189 (class 0 OID 17381)
-- Dependencies: 234
-- Data for Name: passager; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.passager (age, nbrbaggage, placechoisis, id_passager, idreservation, genre, nom, numeropieceidentific) VALUES (18, 0, 2, 'e3365fe7-a88c-4b02-8892-6f71d901fa3c', '29bb2dfe-114f-42b0-9f00-c5a0a0d48b07', 'MALE', 'ngoupeyou bryan', 'kit247');
INSERT INTO public.passager (age, nbrbaggage, placechoisis, id_passager, idreservation, genre, nom, numeropieceidentific) VALUES (21, 0, 1, '430dc641-2e9d-498e-a100-3d456d403b82', 'b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d', 'MALE', 'Ngoupeyou Bryan Jean-Roland', 'kit247');
INSERT INTO public.passager (age, nbrbaggage, placechoisis, id_passager, idreservation, genre, nom, numeropieceidentific) VALUES (19, 0, 22, 'f2532c92-a6a5-4de6-bfcc-869378cdca00', '4c9d19ab-6cd2-410d-b252-e93b33c17013', 'MALE', 'Ngoupeyou Bryan', 'KIT247');


--
-- TOC entry 5191 (class 0 OID 17396)
-- Dependencies: 236
-- Data for Name: politique_annulation_liste_taux_periode; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5190 (class 0 OID 17390)
-- Dependencies: 235
-- Data for Name: politiqueannulation; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5192 (class 0 OID 17400)
-- Dependencies: 237
-- Data for Name: reservation; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.reservation (montant_paye, nbr_passager, prix_total, date_confirmation, date_reservation, id_reservation, id_user, id_voyage, statut_payement, statut_reservation, transaction_code) VALUES (0, 1, 15000, NULL, '2025-12-19 23:42:18.024', '29bb2dfe-114f-42b0-9f00-c5a0a0d48b07', '634126dd-d18b-440b-8713-4cececdc6d9a', '66666661-abcd-6666-6666-666666666666', 'NO_PAYMENT', 'RESERVER', NULL);
INSERT INTO public.reservation (montant_paye, nbr_passager, prix_total, date_confirmation, date_reservation, id_reservation, id_user, id_voyage, statut_payement, statut_reservation, transaction_code) VALUES (0, 1, 15000, NULL, '2025-12-20 00:12:59.399', 'b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d', '634126dd-d18b-440b-8713-4cececdc6d9a', '66666661-abcd-6666-6666-666666666666', 'NO_PAYMENT', 'RESERVER', NULL);
INSERT INTO public.reservation (montant_paye, nbr_passager, prix_total, date_confirmation, date_reservation, id_reservation, id_user, id_voyage, statut_payement, statut_reservation, transaction_code) VALUES (8000, 1, 8000, '2026-01-16 18:36:55.468', '2025-12-24 13:54:59.786', '4c9d19ab-6cd2-410d-b252-e93b33c17013', '634126dd-d18b-440b-8713-4cececdc6d9a', '66666662-abcd-6666-6666-666666666666', 'PAID', 'CONFIRMER', 'SIM-04D98183');


--
-- TOC entry 5193 (class 0 OID 17416)
-- Dependencies: 238
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5194 (class 0 OID 17424)
-- Dependencies: 239
-- Data for Name: soldeindemnisation; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 5195 (class 0 OID 17434)
-- Dependencies: 240
-- Data for Name: vehicule; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.vehicule (nbrplaces, idagencevoyage, idvehicule, description, lienphoto, modele, nom, plaquematricule) VALUES (40, '66666661-bbbb-6666-6666-666666666666', '66666661-eeee-6666-6666-666666666666', 'Bus climatisé WiFi', 'https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=', 'Mercedes 2023', 'Bus Express 1', 'LT1234AB');
INSERT INTO public.vehicule (nbrplaces, idagencevoyage, idvehicule, description, lienphoto, modele, nom, plaquematricule) VALUES (50, '66666663-bbbb-6666-6666-666666666666', '66666662-eeee-6666-6666-666666666666', 'Bus confortable', 'https://media.istockphoto.com/id/1161674685/photo/two-white-buses-traveling-on-the-asphalt-road-in-rural-landscape-at-sunset-with-dramatic.jpg?s=1024x1024&w=is&k=20&c=MfOEF5o2as5hiKtaVJUO94Xqn3JoU9rY-MgGjLe3pz0=', 'Isuzu 2022', 'Bus Rapide 1', 'LT5678CD');


--
-- TOC entry 5196 (class 0 OID 17445)
-- Dependencies: 241
-- Data for Name: voyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.voyage (nbrplaceconfirm, nbrplacereservable, nbrplacereserve, nbrplacerestante, datearriveeffectif, datedeparteffectif, datedepartprev, datelimiteconfirmation, datelimitereservation, datepublication, dureevoyage, heurearrive, heuredeparteffectif, idvoyage, amenities, bigimage, description, lieuarrive, lieudepart, pointarrivee, pointdedepart, smallimage, statusvoyage, titre) VALUES (1, 39, 1, 39, NULL, NULL, '2026-12-30 19:22:51.994496', '2026-12-31 19:22:51.994496', '2026-12-31 19:22:51.994496', '2025-12-19 21:22:51.994496', 14400000000000, '2026-01-01 01:22:51.994496', NULL, '66666662-abcd-6666-6666-666666666666', 'WIFI,AC', 'https://media.istockphoto.com/id/1161674685/photo/two-white-buses-traveling-on-the-asphalt-road-in-rural-landscape-at-sunset-with-dramatic.jpg?s=1024x1024&w=is&k=20&c=MfOEF5o2as5hiKtaVJUO94Xqn3JoU9rY-MgGjLe3pz0=', 'Retour rapide capitale', 'Yaoundé', 'Douala', 'Mvan', 'Akwa', 'https://bougna.net/wp-content/uploads/2018/08/Bus-de-transport-de-Finex-Voyages-Mini-696x461.jpg', 'PUBLIE', 'Douala → Yaoundé');
INSERT INTO public.voyage (nbrplaceconfirm, nbrplacereservable, nbrplacereserve, nbrplacerestante, datearriveeffectif, datedeparteffectif, datedepartprev, datelimiteconfirmation, datelimitereservation, datepublication, dureevoyage, heurearrive, heuredeparteffectif, idvoyage, amenities, bigimage, description, lieuarrive, lieudepart, pointarrivee, pointdedepart, smallimage, statusvoyage, titre) VALUES (0, 50, 0, 50, NULL, NULL, '2026-12-30 19:22:51.994496', '2026-12-31 19:22:51.994496', '2026-12-31 19:22:51.994496', '2025-12-19 21:22:51.994496', 21600000000000, '2026-01-01 01:22:51.994496', NULL, '66666663-abcd-6666-6666-666666666666', 'WIFI,AC,SNACKS', 'https://media.istockphoto.com/id/157526603/photo/white-bus-crossing-the-alpes.jpg?s=1024x1024&w=is&k=20&c=AOCRwt95N_M2HgHzSAXkdYCqjca4-p2H3XYrGFgYkDU=', 'Confort premium', 'Yaoundé', 'Bafoussam', 'Odza', 'Centre-ville', 'https://c.wallhere.com/photos/d8/b5/travel_sunset_sea_italy_public_night_landscape_dawn-751857.jpg!d', 'PUBLIE', 'Bafoussam → Yaoundé');
INSERT INTO public.voyage (nbrplaceconfirm, nbrplacereservable, nbrplacereserve, nbrplacerestante, datearriveeffectif, datedeparteffectif, datedepartprev, datelimiteconfirmation, datelimitereservation, datepublication, dureevoyage, heurearrive, heuredeparteffectif, idvoyage, amenities, bigimage, description, lieuarrive, lieudepart, pointarrivee, pointdedepart, smallimage, statusvoyage, titre) VALUES (0, 38, 2, 40, NULL, NULL, '2026-12-30 19:22:51.994496', '2026-12-31 19:22:51.994496', '2026-12-31 19:22:51.994496', '2025-12-19 21:22:51.994496', 14400000000000, '2026-01-01 01:22:51.994496', NULL, '66666661-abcd-6666-6666-666666666666', 'WIFI,AC', 'https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=', 'Voyage direct climatisé WiFi', 'Douala', 'Yaoundé', 'Akwa', 'Mvan', 'https://st.depositphotos.com/1019192/4338/i/950/depositphotos_43389909-stock-photo-tourist-bus-traveling-on-road.jpg', 'PUBLIE', 'Yaoundé → Douala');


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


-- Completed on 2026-01-17 19:16:29

--
-- PostgreSQL database dump complete
--

\unrestrict lgTefoQqWSdfnwe33ylTOMlAvSrdm8b837cv4xaX76FEJ94kh2BsSRXM1mMdckA

----------------------------


-- Script de création des tables et insertion des données
-- User: avnadmin

-- Configuration initiale
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;

-- Création du type personnalisé
CREATE TYPE public.tauxperiode AS (
    datedebut timestamp without time zone,
    datefin timestamp without time zone,
    taux double precision,
    compensation double precision
);

-- Création des tables
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
    CONSTRAINT agencevoyage_pkey PRIMARY KEY (agencyid),
    CONSTRAINT agencevoyage_statut_validation_check CHECK (((statut_validation)::text = ANY ((ARRAY['EN_ATTENTE'::character varying, 'VALIDEE'::character varying, 'REJETEE'::character varying, 'SUSPENDUE'::character varying])::text[])))
);

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
    CONSTRAINT app_user_pkey PRIMARY KEY (userid),
    CONSTRAINT app_user_username_key UNIQUE (username),
    CONSTRAINT app_user_businessactortype_check CHECK (((businessactortype >= 0) AND (businessactortype <= 2))),
    CONSTRAINT app_user_genre_check CHECK (((genre >= 0) AND (genre <= 1)))
);

CREATE TABLE public.baggage (
    idbaggage uuid NOT NULL,
    idpassager uuid,
    nbrebaggage character varying(255),
    CONSTRAINT baggage_pkey PRIMARY KEY (idbaggage)
);

CREATE TABLE public.chauffeuragencevoyage (
    agencevoyageid uuid,
    chauffeurid uuid NOT NULL,
    userid uuid,
    statuschauffeur character varying(255),
    CONSTRAINT chauffeuragencevoyage_pkey PRIMARY KEY (chauffeurid),
    CONSTRAINT chauffeuragencevoyage_statuschauffeur_check CHECK (((statuschauffeur)::text = ANY ((ARRAY['OCCUPE'::character varying, 'LIBRE'::character varying, 'REPOS'::character varying])::text[])))
);

CREATE TABLE public.classvoyage (
    prix double precision,
    tauxannulation double precision,
    idagencevoyage uuid,
    idclassvoyage uuid NOT NULL,
    nom character varying(255),
    CONSTRAINT classvoyage_pkey PRIMARY KEY (idclassvoyage)
);

CREATE TABLE public.coordonnee (
    idcoordonnee uuid NOT NULL,
    altitude character varying(255),
    latitude character varying(255),
    longitude character varying(255),
    CONSTRAINT coordonnee_pkey PRIMARY KEY (idcoordonnee)
);

CREATE TABLE public.coupon (
    valeur double precision,
    datedebut timestamp(6) without time zone,
    datefin timestamp(6) without time zone,
    idcoupon uuid NOT NULL,
    idhistorique uuid,
    idsoldeindemnisation uuid,
    statuscoupon character varying(255),
    CONSTRAINT coupon_pkey PRIMARY KEY (idcoupon),
    CONSTRAINT coupon_statuscoupon_check CHECK (((statuscoupon)::text = ANY ((ARRAY['VALIDE'::character varying, 'EXPIRER'::character varying, 'UTILISER'::character varying])::text[])))
);

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
    CONSTRAINT employeagencevoyage_pkey PRIMARY KEY (employeid),
    CONSTRAINT employeagencevoyage_statutemploye_check CHECK (((statutemploye)::text = ANY ((ARRAY['ACTIF'::character varying, 'INACTIF'::character varying, 'SUSPENDU'::character varying, 'EN_CONGE'::character varying, 'DEMISSIONNE'::character varying, 'LICENCIE'::character varying])::text[])))
);

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
    CONSTRAINT historique_pkey PRIMARY KEY (idhistorique),
    CONSTRAINT historique_statushistorique_check CHECK (((statushistorique)::text = ANY ((ARRAY['ANNULER_PAR_AGENCE_APRES_RESERVATION'::character varying, 'ANNULER_PAR_USAGER_APRES_RESERVATION'::character varying, 'ANNULER_PAR_AGENCE_APRES_CONFIRMATION'::character varying, 'ANNULER_PAR_USAGER_APRES_CONFIRMATION'::character varying, 'VALIDER'::character varying])::text[])))
);

CREATE TABLE public.ligne_voyage (
    idagencevoyage uuid NOT NULL,
    idchauffeur uuid NOT NULL,
    idclassvoyage uuid NOT NULL,
    idlignevoyage uuid NOT NULL,
    idvehicule uuid NOT NULL,
    idvoyage uuid NOT NULL,
    CONSTRAINT ligne_voyage_pkey PRIMARY KEY (idlignevoyage)
);

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
    websiteurl character varying(255),
    CONSTRAINT organization_pkey PRIMARY KEY (id)
);

CREATE TABLE public.organization_business_domains (
    business_domain uuid,
    organization_id uuid NOT NULL
);

CREATE TABLE public.organization_keywords (
    organization_id uuid NOT NULL,
    keyword character varying(255)
);

CREATE TABLE public.passager (
    age integer NOT NULL,
    nbrbaggage integer,
    placechoisis integer,
    id_passager uuid NOT NULL,
    idreservation uuid,
    genre character varying(255),
    nom character varying(255),
    numeropieceidentific character varying(255),
    CONSTRAINT passager_pkey PRIMARY KEY (id_passager)
);

CREATE TABLE public.politiqueannulation (
    dureecoupon bigint,
    id_politique uuid NOT NULL,
    idagencevoyage uuid,
    CONSTRAINT politiqueannulation_pkey PRIMARY KEY (id_politique)
);

CREATE TABLE public.politique_annulation_liste_taux_periode (
    compensation double precision,
    taux double precision,
    date_debut timestamp(6) without time zone,
    date_fin timestamp(6) without time zone,
    politique_annulation_id_politique uuid NOT NULL
);

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
    CONSTRAINT reservation_pkey PRIMARY KEY (id_reservation),
    CONSTRAINT reservation_statut_payement_check CHECK (((statut_payement)::text = ANY ((ARRAY['PENDING'::character varying, 'NO_PAYMENT'::character varying, 'PAID'::character varying, 'FAILED'::character varying])::text[]))),
    CONSTRAINT reservation_statut_reservation_check CHECK (((statut_reservation)::text = ANY ((ARRAY['RESERVER'::character varying, 'CONFIRMER'::character varying, 'ANNULER'::character varying, 'VALIDER'::character varying])::text[])))
);

CREATE TABLE public.role (
    id uuid NOT NULL,
    libelle character varying(255) NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id),
    CONSTRAINT role_libelle_check CHECK (((libelle)::text = ANY ((ARRAY['USAGER'::character varying, 'EMPLOYE'::character varying, 'AGENCE_VOYAGE'::character varying, 'ORGANISATION'::character varying, 'BSM'::character varying])::text[])))
);

CREATE TABLE public.soldeindemnisation (
    solde double precision NOT NULL,
    id_agence_voyage uuid NOT NULL,
    id_solde uuid NOT NULL,
    id_user uuid NOT NULL,
    type character varying(255) NOT NULL,
    CONSTRAINT soldeindemnisation_pkey PRIMARY KEY (id_solde)
);

CREATE TABLE public.vehicule (
    nbrplaces integer NOT NULL,
    idagencevoyage uuid,
    idvehicule uuid NOT NULL,
    description character varying(255),
    lienphoto character varying(255),
    modele character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    plaquematricule character varying(255),
    CONSTRAINT vehicule_pkey PRIMARY KEY (idvehicule)
);

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
    CONSTRAINT voyage_pkey PRIMARY KEY (idvoyage),
    CONSTRAINT voyage_statusvoyage_check CHECK (((statusvoyage)::text = ANY ((ARRAY['EN_ATTENTE'::character varying, 'PUBLIE'::character varying, 'EN_COURS'::character varying, 'TERMINE'::character varying, 'ANNULE'::character varying])::text[])))
);

-- Ajout des contraintes de clés étrangères
ALTER TABLE public.politique_annulation_liste_taux_periode
    ADD CONSTRAINT fk33y1kxnvx5ged3sidllv2jdv4 FOREIGN KEY (politique_annulation_id_politique) REFERENCES public.politiqueannulation(id_politique);

ALTER TABLE public.organization_keywords
    ADD CONSTRAINT fkq29f42todjfucy9lo14ax3v0y FOREIGN KEY (organization_id) REFERENCES public.organization(id);

ALTER TABLE public.organization_business_domains
    ADD CONSTRAINT fksgeo5qecw4ekjhwff0t5ew3ko FOREIGN KEY (organization_id) REFERENCES public.organization(id);

-- Insertion des données
INSERT INTO public.app_user (businessactortype, genre, idcoordonneegps, userid, address, email, nom, password, prenom, role, telnumber, username) VALUES
(1, 0, NULL, '634126dd-d18b-440b-8713-4cececdc6d9a', NULL, 'ngoupeyoubryan9@gmail.com', 'Bryan', '$2a$10$p/fClXdQ1fhiKOXLEU.Bwe.VJiAVtGpvc5.SzGFm8PexRbBQwnV2C', 'Ngoupeyou', 'USAGER', '655121010', 'cestbryan'),
(0, 0, NULL, 'a9abc859-2337-479d-ac4d-2c01594eaee3', NULL, 'robert@gmail.com', 'Roméo', '$2a$10$KiHOCys9QNMhx/fI7nwMYeD3aq57gqMvqw7yAsxnd5aUMy/JYIDFW', 'Robert', 'ORGANISATION', '655121012', 'orga'),
(0, 0, NULL, 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, 'julio@gmail.com', 'Julio', '$2a$10$8gG1UKR4LFGWnueyrmsroOO7yKRF0PMS8pK50R9yKIlSCdMJ/RzBe', 'Hubert', 'AGENCE_VOYAGE', '655121013', 'chef'),
(2, 0, NULL, '80cd2063-e648-4ab5-aa75-c801faf5142e', 'Yaoundé', 'talla@gmail.com', 'Féderic', '$2a$10$LqFXnO0K9EpIT3qCOuIIX.18U9I2agWz4sBH6uDtamqV5dTe7NSOG', 'Talla', 'BSM', '655121011', 'bsm'),
(1, 0, NULL, 'd25d8e70-737b-4ccf-9ec2-f179aa8faa73', NULL, 'robertnelson@gmail.com', 'Robert', '$2a$10$P/QuZvPRUS7g0KN7fOtZQOcEo.T6XvK/oRavbF/0S6H2w9FGX4MOq', 'Nelson', 'USAGER', '655121018', 'rob');

INSERT INTO public.organization (capitalshare, isactive, isindividualbusiness, createdat, deletedat, registrationdate, updatedat, yearfounded, createdby, id, organizationid, updatedby, businessregistrationnumber, ceoname, description, email, legalform, logourl, longname, shortname, socialnetwork, status, taxnumber, websiteurl) VALUES
(NULL, true, false, '2025-12-19 21:22:51.994496', NULL, NULL, NULL, NULL, 'a9abc859-2337-479d-ac4d-2c01594eaee3', '9c68dfc5-f459-4866-905c-15647b9b6ece', '66666661-aaaa-6666-6666-666666666666', NULL, '01234567', NULL, NULL, 'contact@expressvoyage.cm', NULL, NULL, 'Express Voyage Cameroun', 'EVC', '@expressvoyage', 'VALIDEE', NULL, NULL),
(NULL, true, false, '2025-12-19 21:22:51.994496', NULL, NULL, NULL, NULL, 'a9abc859-2337-479d-ac4d-2c01594eaee3', 'e23ae754-c8b2-4b3c-8377-f1818354ac3d', '66666662-aaaa-6666-6666-666666666666', NULL, 'SC123456', NULL, NULL, 'info@transportrapide.cm', NULL, NULL, 'Transport Rapide SA', 'TRSA', '@transportrapide', 'VALIDEE', NULL, NULL);

INSERT INTO public.agencevoyage (date_validation, agencyid, bsm_validator_id, organisationid, userid, description, greetingmessage, location, longname, motif_rejet, shortname, socialnetwork, statut_validation, ville) VALUES
('2025-12-19 21:22:51.994496', '66666664-bbbb-6666-6666-666666666666', '80cd2063-e648-4ab5-aa75-c801faf5142e', '66666662-aaaa-6666-6666-666666666666', 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, NULL, 'Bonaberi', 'TRSA Douala Bonaberi', 'Documentation incomplète.', 'TRSA DLA', '@trsa_douala', 'REJETEE', 'Douala'),
('2025-12-19 21:22:51.994496', '66666661-bbbb-6666-6666-666666666666', '80cd2063-e648-4ab5-aa75-c801faf5142e', '9c68dfc5-f459-4866-905c-15647b9b6ece', 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, NULL, 'Mvan', 'EVC Yaoundé Centre', NULL, 'EVC YDE', '@evc_yaounde', 'VALIDEE', 'Yaoundé'),
(NULL, '66666662-bbbb-6666-6666-666666666666', NULL, '9c68dfc5-f459-4866-905c-15647b9b6ece', 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, NULL, 'Akwa', 'EVC Douala Akwa', NULL, 'EVC DLA', '@evc_douala', 'EN_ATTENTE', 'Douala'),
('2025-12-19 21:22:51.994496', '66666663-bbbb-6666-6666-666666666666', '80cd2063-e648-4ab5-aa75-c801faf5142e', 'e23ae754-c8b2-4b3c-8377-f1818354ac3d', 'bf27732f-faa4-4409-9e82-0e862a053749', NULL, NULL, 'Centre-ville', 'TRSA Bafoussam', NULL, 'TRSA BFM', '@trsa_bafoussam', 'VALIDEE', 'Bafoussam');

INSERT INTO public.chauffeuragencevoyage (agencevoyageid, chauffeurid, userid, statuschauffeur) VALUES
('66666661-bbbb-6666-6666-666666666666', '66666661-dddd-6666-6666-666666666666', 'd25d8e70-737b-4ccf-9ec2-f179aa8faa73', 'LIBRE'),
('66666663-bbbb-6666-6666-666666666666', '66666662-dddd-6666-6666-666666666666', 'd25d8e70-737b-4ccf-9ec2-f179aa8faa73', 'LIBRE');

INSERT INTO public.classvoyage (prix, tauxannulation, idagencevoyage, idclassvoyage, nom) VALUES
(15000, 0.8, '66666661-bbbb-6666-6666-666666666666', '66666661-ffff-6666-6666-666666666666', 'VIP'),
(8000, 0.5, '66666661-bbbb-6666-6666-666666666666', '66666662-ffff-6666-6666-666666666666', 'ÉCONOMIQUE'),
(12000, 0.8, '66666663-bbbb-6666-6666-666666666666', '66666663-ffff-6666-6666-666666666666', 'VIP');

INSERT INTO public.vehicule (nbrplaces, idagencevoyage, idvehicule, description, lienphoto, modele, nom, plaquematricule) VALUES
(40, '66666661-bbbb-6666-6666-666666666666', '66666661-eeee-6666-6666-666666666666', 'Bus climatisé WiFi', 'https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=', 'Mercedes 2023', 'Bus Express 1', 'LT1234AB'),
(50, '66666663-bbbb-6666-6666-666666666666', '66666662-eeee-6666-6666-666666666666', 'Bus confortable', 'https://media.istockphoto.com/id/1161674685/photo/two-white-buses-traveling-on-the-asphalt-road-in-rural-landscape-at-sunset-with-dramatic.jpg?s=1024x1024&w=is&k=20&c=MfOEF5o2as5hiKtaVJUO94Xqn3JoU9rY-MgGjLe3pz0=', 'Isuzu 2022', 'Bus Rapide 1', 'LT5678CD');

INSERT INTO public.voyage (nbrplaceconfirm, nbrplacereservable, nbrplacereserve, nbrplacerestante, datearriveeffectif, datedeparteffectif, datedepartprev, datelimiteconfirmation, datelimitereservation, datepublication, dureevoyage, heurearrive, heuredeparteffectif, idvoyage, amenities, bigimage, description, lieuarrive, lieudepart, pointarrivee, pointdedepart, smallimage, statusvoyage, titre) VALUES
(0, 38, 2, 40, NULL, NULL, '2026-12-30 19:22:51.994496', '2026-12-31 19:22:51.994496', '2026-12-31 19:22:51.994496', '2025-12-19 21:22:51.994496', 14400000000000, '2026-01-01 01:22:51.994496', NULL, '66666661-abcd-6666-6666-666666666666', 'WIFI,AC', 'https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=', 'Voyage direct climatisé WiFi', 'Douala', 'Yaoundé', 'Akwa', 'Mvan', 'https://st.depositphotos.com/1019192/4338/i/950/depositphotos_43389909-stock-photo-tourist-bus-traveling-on-road.jpg', 'PUBLIE', 'Yaoundé → Douala'),
(1, 39, 1, 39, NULL, NULL, '2026-12-30 19:22:51.994496', '2026-12-31 19:22:51.994496', '2026-12-31 19:22:51.994496', '2025-12-19 21:22:51.994496', 14400000000000, '2026-01-01 01:22:51.994496', NULL, '66666662-abcd-6666-6666-666666666666', 'WIFI,AC', 'https://media.istockphoto.com/id/1161674685/photo/two-white-buses-traveling-on-the-asphalt-road-in-rural-landscape-at-sunset-with-dramatic.jpg?s=1024x1024&w=is&k=20&c=MfOEF5o2as5hiKtaVJUO94Xqn3JoU9rY-MgGjLe3pz0=', 'Retour rapide capitale', 'Yaoundé', 'Douala', 'Mvan', 'Akwa', 'https://bougna.net/wp-content/uploads/2018/08/Bus-de-transport-de-Finex-Voyages-Mini-696x461.jpg', 'PUBLIE', 'Douala → Yaoundé'),
(0, 50, 0, 50, NULL, NULL, '2026-12-30 19:22:51.994496', '2026-12-31 19:22:51.994496', '2026-12-31 19:22:51.994496', '2025-12-19 21:22:51.994496', 21600000000000, '2026-01-01 01:22:51.994496', NULL, '66666663-abcd-6666-6666-666666666666', 'WIFI,AC,SNACKS', 'https://media.istockphoto.com/id/157526603/photo/white-bus-crossing-the-alpes.jpg?s=1024x1024&w=is&k=20&c=AOCRwt95N_M2HgHzSAXkdYCqjca4-p2H3XYrGFgYkDU=', 'Confort premium', 'Yaoundé', 'Bafoussam', 'Odza', 'Centre-ville', 'https://c.wallhere.com/photos/d8/b5/travel_sunset_sea_italy_public_night_landscape_dawn-751857.jpg!d', 'PUBLIE', 'Bafoussam → Yaoundé');

INSERT INTO public.ligne_voyage (idagencevoyage, idchauffeur, idclassvoyage, idlignevoyage, idvehicule, idvoyage) VALUES
('66666661-bbbb-6666-6666-666666666666', '66666661-dddd-6666-6666-666666666666', '66666661-ffff-6666-6666-666666666666', '66666661-ef01-6666-6666-666666666666', '66666661-eeee-6666-6666-666666666666', '66666661-abcd-6666-6666-666666666666'),
('66666661-bbbb-6666-6666-666666666666', '66666661-dddd-6666-6666-666666666666', '66666662-ffff-6666-6666-666666666666', '66666662-ef01-6666-6666-666666666666', '66666661-eeee-6666-6666-666666666666', '66666662-abcd-6666-6666-666666666666'),
('66666663-bbbb-6666-6666-666666666666', '66666662-dddd-6666-6666-666666666666', '66666663-ffff-6666-6666-666666666666', '66666663-ef01-6666-6666-666666666666', '66666662-eeee-6666-6666-666666666666', '66666663-abcd-6666-6666-666666666666');

INSERT INTO public.reservation (montant_paye, nbr_passager, prix_total, date_confirmation, date_reservation, id_reservation, id_user, id_voyage, statut_payement, statut_reservation, transaction_code) VALUES
(0, 1, 15000, NULL, '2025-12-19 23:42:18.024', '29bb2dfe-114f-42b0-9f00-c5a0a0d48b07', '634126dd-d18b-440b-8713-4cececdc6d9a', '66666661-abcd-6666-6666-666666666666', 'NO_PAYMENT', 'RESERVER', NULL),
(0, 1, 15000, NULL, '2025-12-20 00:12:59.399', 'b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d', '634126dd-d18b-440b-8713-4cececdc6d9a', '66666661-abcd-6666-6666-666666666666', 'NO_PAYMENT', 'RESERVER', NULL),
(8000, 1, 8000, '2026-01-16 18:36:55.468', '2025-12-24 13:54:59.786', '4c9d19ab-6cd2-410d-b252-e93b33c17013', '634126dd-d18b-440b-8713-4cececdc6d9a', '66666662-abcd-6666-6666-666666666666', 'PAID', 'CONFIRMER', 'SIM-04D98183');

INSERT INTO public.passager (age, nbrbaggage, placechoisis, id_passager, idreservation, genre, nom, numeropieceidentific) VALUES
(18, 0, 2, 'e3365fe7-a88c-4b02-8892-6f71d901fa3c', '29bb2dfe-114f-42b0-9f00-c5a0a0d48b07', 'MALE', 'ngoupeyou bryan', 'kit247'),
(21, 0, 1, '430dc641-2e9d-498e-a100-3d456d403b82', 'b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d', 'MALE', 'Ngoupeyou Bryan Jean-Roland', 'kit247'),
(19, 0, 22, 'f2532c92-a6a5-4de6-bfcc-869378cdca00', '4c9d19ab-6cd2-410d-b252-e93b33c17013', 'MALE', 'Ngoupeyou Bryan', 'KIT247');

INSERT INTO public.historique (compensation, tauxannulation, dateannulation, dateconfirmation, datereservation, idhistorique, idreservation, causeannulation, origineannulation, statushistorique) VALUES
(0, 0, NULL, NULL, '2025-12-19 23:42:18.024', '4b1ca620-2366-43a3-a886-5909445744c2', '29bb2dfe-114f-42b0-9f00-c5a0a0d48b07', NULL, NULL, 'VALIDER'),
(0, 0, NULL, NULL, '2025-12-20 00:12:59.399', '0855c0d6-44e7-46a4-b0b2-ce2e9f35d9ea', 'b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d', NULL, NULL, 'VALIDER'),
(0, 0, NULL, '2026-01-16 18:36:55.468', '2025-12-24 13:54:59.786', 'd06c6e52-b23c-476c-a01f-c5740f06ccfe', '4c9d19ab-6cd2-410d-b252-e93b33c17013', NULL, NULL, 'VALIDER');