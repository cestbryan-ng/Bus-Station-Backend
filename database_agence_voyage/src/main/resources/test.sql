--
-- PostgreSQL database dump
--

\restrict YZ2if472ecm38gjn7DqWl94fXEAtkM7yuwTkhNN9zbxo8V15aGkiGPPTnK1acHI

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

-- Started on 2025-12-23 13:34:01

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
-- TOC entry 5165 (class 0 OID 17274)
-- Dependencies: 221
-- Data for Name: agencevoyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.agencevoyage (date_validation, agencyid, bsm_validator_id, organisationid, userid, description, greetingmessage, location, longname, motif_rejet, shortname, socialnetwork, statut_validation, ville) FROM stdin;
\N	66666662-bbbb-6666-6666-666666666666	\N	66666661-aaaa-6666-6666-666666666666	bf27732f-faa4-4409-9e82-0e862a053749	\N	\N	Akwa	EVC Douala Akwa	\N	EVC DLA	@evc_douala	EN_ATTENTE	Douala
2025-12-19 21:22:51.994496	66666661-bbbb-6666-6666-666666666666	80cd2063-e648-4ab5-aa75-c801faf5142e	66666661-aaaa-6666-6666-666666666666	bf27732f-faa4-4409-9e82-0e862a053749	\N	\N	Mvan	EVC Yaoundé Centre	\N	EVC YDE	@evc_yaounde	VALIDEE	Yaoundé
2025-12-19 21:22:51.994496	66666663-bbbb-6666-6666-666666666666	80cd2063-e648-4ab5-aa75-c801faf5142e	66666662-aaaa-6666-6666-666666666666	bf27732f-faa4-4409-9e82-0e862a053749	\N	\N	Centre-ville	TRSA Bafoussam	\N	TRSA BFM	@trsa_bafoussam	VALIDEE	Bafoussam
2025-12-19 21:22:51.994496	66666664-bbbb-6666-6666-666666666666	80cd2063-e648-4ab5-aa75-c801faf5142e	66666662-aaaa-6666-6666-666666666666	bf27732f-faa4-4409-9e82-0e862a053749	\N	\N	Bonaberi	TRSA Douala Bonaberi	Documentation incomplète.	TRSA DLA	@trsa_douala	REJETEE	Douala
\.


--
-- TOC entry 5166 (class 0 OID 17283)
-- Dependencies: 222
-- Data for Name: app_user; Type: TABLE DATA; Schema: public; Owner: postgres
-- Mot de passe : 1234
--

COPY public.app_user (businessactortype, genre, idcoordonneegps, userid, address, email, nom, password, prenom, role, telnumber, username) FROM stdin;
1	0	\N	634126dd-d18b-440b-8713-4cececdc6d9a	\N	ngoupeyoubryan9@gmail.com	Bryan	$2a$10$p/fClXdQ1fhiKOXLEU.Bwe.VJiAVtGpvc5.SzGFm8PexRbBQwnV2C	Ngoupeyou	USAGER	655121010	cestbryan
0	0	\N	a9abc859-2337-479d-ac4d-2c01594eaee3	\N	robert@gmail.com	Roméo	$2a$10$KiHOCys9QNMhx/fI7nwMYeD3aq57gqMvqw7yAsxnd5aUMy/JYIDFW	Robert	ORGANISATION	655121012	orga
0	0	\N	bf27732f-faa4-4409-9e82-0e862a053749	\N	julio@gmail.com	Julio	$2a$10$8gG1UKR4LFGWnueyrmsroOO7yKRF0PMS8pK50R9yKIlSCdMJ/RzBe	Hubert	AGENCE_VOYAGE	655121013	chef
2	0	\N	80cd2063-e648-4ab5-aa75-c801faf5142e	Yaoundé	talla@gmail.com	Féderic	$2a$10$LqFXnO0K9EpIT3qCOuIIX.18U9I2agWz4sBH6uDtamqV5dTe7NSOG	Talla	BSM	655121011	bsm
\.


--
-- TOC entry 5167 (class 0 OID 17296)
-- Dependencies: 223
-- Data for Name: baggage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.baggage (idbaggage, idpassager, nbrebaggage) FROM stdin;
\.


--
-- TOC entry 5168 (class 0 OID 17302)
-- Dependencies: 224
-- Data for Name: chauffeuragencevoyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.chauffeuragencevoyage (agencevoyageid, chauffeurid, userid, statuschauffeur) FROM stdin;
66666661-bbbb-6666-6666-666666666666	66666661-dddd-6666-6666-666666666666	66666661-cccc-6666-6666-666666666666	LIBRE
66666663-bbbb-6666-6666-666666666666	66666662-dddd-6666-6666-666666666666	66666662-cccc-6666-6666-666666666666	LIBRE
\.


--
-- TOC entry 5169 (class 0 OID 17309)
-- Dependencies: 225
-- Data for Name: classvoyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.classvoyage (prix, tauxannulation, idagencevoyage, idclassvoyage, nom) FROM stdin;
15000	0.8	66666661-bbbb-6666-6666-666666666666	66666661-ffff-6666-6666-666666666666	VIP
8000	0.5	66666661-bbbb-6666-6666-666666666666	66666662-ffff-6666-6666-666666666666	ÉCONOMIQUE
12000	0.8	66666663-bbbb-6666-6666-666666666666	66666663-ffff-6666-6666-666666666666	VIP
\.


--
-- TOC entry 5170 (class 0 OID 17315)
-- Dependencies: 226
-- Data for Name: coordonnee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.coordonnee (idcoordonnee, altitude, latitude, longitude) FROM stdin;
\.


--
-- TOC entry 5171 (class 0 OID 17323)
-- Dependencies: 227
-- Data for Name: coupon; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.coupon (valeur, datedebut, datefin, idcoupon, idhistorique, idsoldeindemnisation, statuscoupon) FROM stdin;
\.


--
-- TOC entry 5172 (class 0 OID 17330)
-- Dependencies: 228
-- Data for Name: employeagencevoyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employeagencevoyage (salaire, dateembauche, datefincontrat, agencevoyageid, employeid, managerid, userid, departement, poste, statutemploye) FROM stdin;
\.


--
-- TOC entry 5173 (class 0 OID 17341)
-- Dependencies: 229
-- Data for Name: historique; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.historique (compensation, tauxannulation, dateannulation, dateconfirmation, datereservation, idhistorique, idreservation, causeannulation, origineannulation, statushistorique) FROM stdin;
0	0	\N	\N	2025-12-19 23:42:18.024	4b1ca620-2366-43a3-a886-5909445744c2	29bb2dfe-114f-42b0-9f00-c5a0a0d48b07	\N	\N	VALIDER
0	0	\N	\N	2025-12-20 00:12:59.399	0855c0d6-44e7-46a4-b0b2-ce2e9f35d9ea	b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d	\N	\N	VALIDER
\.


--
-- TOC entry 5174 (class 0 OID 17354)
-- Dependencies: 230
-- Data for Name: ligne_voyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.ligne_voyage (idagencevoyage, idchauffeur, idclassvoyage, idlignevoyage, idvehicule, idvoyage) FROM stdin;
66666661-bbbb-6666-6666-666666666666	66666661-dddd-6666-6666-666666666666	66666661-ffff-6666-6666-666666666666	66666661-ef01-6666-6666-666666666666	66666661-eeee-6666-6666-666666666666	66666661-abcd-6666-6666-666666666666
66666661-bbbb-6666-6666-666666666666	66666661-dddd-6666-6666-666666666666	66666662-ffff-6666-6666-666666666666	66666662-ef01-6666-6666-666666666666	66666661-eeee-6666-6666-666666666666	66666662-abcd-6666-6666-666666666666
66666663-bbbb-6666-6666-666666666666	66666662-dddd-6666-6666-666666666666	66666663-ffff-6666-6666-666666666666	66666663-ef01-6666-6666-666666666666	66666662-eeee-6666-6666-666666666666	66666663-abcd-6666-6666-666666666666
\.


--
-- TOC entry 5175 (class 0 OID 17365)
-- Dependencies: 231
-- Data for Name: organization; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organization (capitalshare, isactive, isindividualbusiness, createdat, deletedat, registrationdate, updatedat, yearfounded, createdby, id, organizationid, updatedby, businessregistrationnumber, ceoname, description, email, legalform, logourl, longname, shortname, socialnetwork, status, taxnumber, websiteurl) FROM stdin;
\N	t	f	2025-12-19 21:22:51.994496	\N	\N	\N	\N	a9abc859-2337-479d-ac4d-2c01594eaee3	9c68dfc5-f459-4866-905c-15647b9b6ece	66666661-aaaa-6666-6666-666666666666	\N	01234567	\N	\N	contact@expressvoyage.cm	\N	\N	Express Voyage Cameroun	EVC	@expressvoyage	VALIDEE	\N	\N
\N	t	f	2025-12-19 21:22:51.994496	\N	\N	\N	\N	a9abc859-2337-479d-ac4d-2c01594eaee3	e23ae754-c8b2-4b3c-8377-f1818354ac3d	66666662-aaaa-6666-6666-666666666666	\N	SC123456	\N	\N	info@transportrapide.cm	\N	\N	Transport Rapide SA	TRSA	@transportrapide	VALIDEE	\N	\N
\.


--
-- TOC entry 5176 (class 0 OID 17373)
-- Dependencies: 232
-- Data for Name: organization_business_domains; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organization_business_domains (business_domain, organization_id) FROM stdin;
\.


--
-- TOC entry 5177 (class 0 OID 17377)
-- Dependencies: 233
-- Data for Name: organization_keywords; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.organization_keywords (organization_id, keyword) FROM stdin;
\.


--
-- TOC entry 5178 (class 0 OID 17381)
-- Dependencies: 234
-- Data for Name: passager; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.passager (age, nbrbaggage, placechoisis, id_passager, idreservation, genre, nom, numeropieceidentific) FROM stdin;
18	0	2	e3365fe7-a88c-4b02-8892-6f71d901fa3c	29bb2dfe-114f-42b0-9f00-c5a0a0d48b07	MALE	ngoupeyou bryan	kit247
21	0	1	430dc641-2e9d-498e-a100-3d456d403b82	b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d	MALE	Ngoupeyou Bryan Jean-Roland	kit247
\.


--
-- TOC entry 5179 (class 0 OID 17390)
-- Dependencies: 235
-- Data for Name: politiqueannulation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.politiqueannulation (dureecoupon, id_politique, idagencevoyage) FROM stdin;
\.


--
-- TOC entry 5180 (class 0 OID 17396)
-- Dependencies: 236
-- Data for Name: politique_annulation_liste_taux_periode; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.politique_annulation_liste_taux_periode (compensation, taux, date_debut, date_fin, politique_annulation_id_politique) FROM stdin;
\.


--
-- TOC entry 5181 (class 0 OID 17400)
-- Dependencies: 237
-- Data for Name: reservation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.reservation (montant_paye, nbr_passager, prix_total, date_confirmation, date_reservation, id_reservation, id_user, id_voyage, statut_payement, statut_reservation, transaction_code) FROM stdin;
0	1	15000	\N	2025-12-19 23:42:18.024	29bb2dfe-114f-42b0-9f00-c5a0a0d48b07	634126dd-d18b-440b-8713-4cececdc6d9a	66666661-abcd-6666-6666-666666666666	NO_PAYMENT	RESERVER	\N
0	1	15000	\N	2025-12-20 00:12:59.399	b9e26ef0-606c-4eca-aa4c-1fc869c8ee3d	634126dd-d18b-440b-8713-4cececdc6d9a	66666661-abcd-6666-6666-666666666666	NO_PAYMENT	RESERVER	\N
\.


--
-- TOC entry 5182 (class 0 OID 17416)
-- Dependencies: 238
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.role (id, libelle) FROM stdin;
\.


--
-- TOC entry 5183 (class 0 OID 17424)
-- Dependencies: 239
-- Data for Name: soldeindemnisation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.soldeindemnisation (solde, id_agence_voyage, id_solde, id_user, type) FROM stdin;
\.


--
-- TOC entry 5184 (class 0 OID 17434)
-- Dependencies: 240
-- Data for Name: vehicule; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vehicule (nbrplaces, idagencevoyage, idvehicule, description, lienphoto, modele, nom, plaquematricule) FROM stdin;
40	66666661-bbbb-6666-6666-666666666666	66666661-eeee-6666-6666-666666666666	Bus climatisé WiFi	https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=	Mercedes 2023	Bus Express 1	LT1234AB
50	66666663-bbbb-6666-6666-666666666666	66666662-eeee-6666-6666-666666666666	Bus confortable	https://media.istockphoto.com/id/1161674685/photo/two-white-buses-traveling-on-the-asphalt-road-in-rural-landscape-at-sunset-with-dramatic.jpg?s=1024x1024&w=is&k=20&c=MfOEF5o2as5hiKtaVJUO94Xqn3JoU9rY-MgGjLe3pz0=	Isuzu 2022	Bus Rapide 1	LT5678CD
\.


--
-- TOC entry 5185 (class 0 OID 17445)
-- Dependencies: 241
-- Data for Name: voyage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.voyage (nbrplaceconfirm, nbrplacereservable, nbrplacereserve, nbrplacerestante, datearriveeffectif, datedeparteffectif, datedepartprev, datelimiteconfirmation, datelimitereservation, datepublication, dureevoyage, heurearrive, heuredeparteffectif, idvoyage, amenities, bigimage, description, lieuarrive, lieudepart, pointarrivee, pointdedepart, smallimage, statusvoyage, titre) FROM stdin;
0	40	0	40	\N	\N	2025-12-23 21:22:51.994496	2025-12-23 19:22:51.994496	2025-12-22 21:22:51.994496	2025-12-19 21:22:51.994496	14400000000000	2025-12-24 01:22:51.994496	\N	66666662-abcd-6666-6666-666666666666	WIFI,AC	https://media.istockphoto.com/id/1161674685/photo/two-white-buses-traveling-on-the-asphalt-road-in-rural-landscape-at-sunset-with-dramatic.jpg?s=1024x1024&w=is&k=20&c=MfOEF5o2as5hiKtaVJUO94Xqn3JoU9rY-MgGjLe3pz0=	Retour rapide capitale	Yaoundé	Douala	Mvan	Akwa	https://bougna.net/wp-content/uploads/2018/08/Bus-de-transport-de-Finex-Voyages-Mini-696x461.jpg	PUBLIE	Douala → Yaoundé
0	50	0	50	\N	\N	2025-12-24 21:22:51.994496	2025-12-24 19:22:51.994496	2025-12-23 21:22:51.994496	2025-12-19 21:22:51.994496	21600000000000	2025-12-25 03:22:51.994496	\N	66666663-abcd-6666-6666-666666666666	WIFI,AC,SNACKS	https://media.istockphoto.com/id/157526603/photo/white-bus-crossing-the-alpes.jpg?s=1024x1024&w=is&k=20&c=AOCRwt95N_M2HgHzSAXkdYCqjca4-p2H3XYrGFgYkDU=	Confort premium	Yaoundé	Bafoussam	Odza	Centre-ville	https://c.wallhere.com/photos/d8/b5/travel_sunset_sea_italy_public_night_landscape_dawn-751857.jpg!d	PUBLIE	Bafoussam → Yaoundé
0	38	2	40	\N	\N	2025-12-22 21:22:51.994496	2025-12-22 19:22:51.994496	2025-12-21 21:22:51.994496	2025-12-19 21:22:51.994496	14400000000000	2025-12-23 01:22:51.994496	\N	66666661-abcd-6666-6666-666666666666	WIFI,AC	https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=	Voyage direct climatisé WiFi	Douala	Yaoundé	Akwa	Mvan	https://st.depositphotos.com/1019192/4338/i/950/depositphotos_43389909-stock-photo-tourist-bus-traveling-on-road.jpg	PUBLIE	Yaoundé → Douala
\.


-- Completed on 2025-12-23 13:34:01

--
-- PostgreSQL database dump complete
--

\unrestrict YZ2if472ecm38gjn7DqWl94fXEAtkM7yuwTkhNN9zbxo8V15aGkiGPPTnK1acHI

