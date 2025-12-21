-- ============================================================================
-- DONNÉES DE TEST - INSERT UNIQUEMENT
-- ============================================================================

-- UTILISATEURS, mais pour ça vous devez les créer depuis le swagger ou postman, puis adapter les id respectifs
INSERT INTO app_user (userid, username, password, nom, prenom, email, telnumber, role, genre, businessactortype)
VALUES 
('66666666-6666-6666-6666-666666666666', 'bsm', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 
 'Manager', 'BSM', 'bsm@busstation.cm', '+237677000001', 'BSM', 1, 2),

('66666661-6666-6666-6666-666666666666', 'dg1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
 'Dupont', 'Jean', 'dg1@transport.cm', '+237677000002', 'ORGANISATION', 1, 0),

('66666662-6666-6666-6666-666666666666', 'dg2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
 'Kamga', 'Paul', 'dg2@transport.cm', '+237677000003', 'ORGANISATION', 1, 0),

('66666661-cccc-6666-6666-666666666666', 'chef1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
 'Nkoa', 'Martin', 'chef1@evc.cm', '+237677100001', 'AGENCE_VOYAGE', 1, 0),

('66666662-cccc-6666-6666-666666666666', 'chef2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO',
 'Tchounke', 'Joseph', 'chef2@trsa.cm', '+237677100002', 'AGENCE_VOYAGE', 1, 0);

-- ORGANISATIONS
INSERT INTO organization (id, organizationid, createdby, createdat, longname, shortname, email, socialnetwork, isactive, isindividualbusiness, status)
VALUES 
(gen_random_uuid(), '66666661-aaaa-6666-6666-666666666666', '66666661-6666-6666-6666-666666666666', NOW(), 'Express Voyage Cameroun', 'EVC', 'contact@expressvoyage.cm', '@expressvoyage', true, false, 'ACTIVE'),
(gen_random_uuid(), '66666662-aaaa-6666-6666-666666666666', '66666662-6666-6666-6666-666666666666', NOW(), 'Transport Rapide SA', 'TRSA', 'info@transportrapide.cm', '@transportrapide', true, false, 'ACTIVE');

-- AGENCES
INSERT INTO agencevoyage (agencyid, organisationid, userid, longname, shortname, ville, location, socialnetwork, statut_validation, bsm_validator_id, date_validation)
VALUES 
('66666661-bbbb-6666-6666-666666666666', '66666661-aaaa-6666-6666-666666666666', '66666661-6666-6666-6666-666666666666', 'EVC Yaoundé Centre', 'EVC YDE', 'Yaoundé', 'Mvan', '@evc_yaounde', 'VALIDEE', '66666666-6666-6666-6666-666666666666', NOW()),
('66666663-bbbb-6666-6666-666666666666', '66666662-aaaa-6666-6666-666666666666', '66666662-6666-6666-6666-666666666666', 'TRSA Bafoussam', 'TRSA BFM', 'Bafoussam', 'Centre-ville', '@trsa_bafoussam', 'VALIDEE', '66666666-6666-6666-6666-666666666666', NOW());

INSERT INTO agencevoyage (agencyid, organisationid, userid, longname, shortname, ville, location, socialnetwork, statut_validation)
VALUES 
('66666662-bbbb-6666-6666-666666666666', '66666661-aaaa-6666-6666-666666666666', '66666661-6666-6666-6666-666666666666', 'EVC Douala Akwa', 'EVC DLA', 'Douala', 'Akwa', '@evc_douala', 'EN_ATTENTE');

INSERT INTO agencevoyage (agencyid, organisationid, userid, longname, shortname, ville, location, socialnetwork, statut_validation, bsm_validator_id, date_validation, motif_rejet)
VALUES 
('66666664-bbbb-6666-6666-666666666666', '66666662-aaaa-6666-6666-666666666666', '66666662-6666-6666-6666-666666666666', 'TRSA Douala Bonaberi', 'TRSA DLA', 'Douala', 'Bonaberi', '@trsa_douala', 'REJETEE', '66666666-6666-6666-6666-666666666666', NOW(), 'Documentation incomplète.');

-- VÉHICULES
INSERT INTO vehicule (idvehicule, nom, modele, description, nbrplaces, plaquematricule, lienphoto, idagencevoyage)
VALUES 
('66666661-eeee-6666-6666-666666666666', 'Bus Express 1', 'Mercedes 2023', 'Bus climatisé WiFi', 40, 'LT1234AB', 'https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=', '66666661-bbbb-6666-6666-666666666666'),
('66666662-eeee-6666-6666-666666666666', 'Bus Rapide 1', 'Isuzu 2022', 'Bus confortable', 50, 'LT5678CD', 'https://media.istockphoto.com/id/1161674685/photo/two-white-buses-traveling-on-the-asphalt-road-in-rural-landscape-at-sunset-with-dramatic.jpg?s=1024x1024&w=is&k=20&c=MfOEF5o2as5hiKtaVJUO94Xqn3JoU9rY-MgGjLe3pz0=', '66666663-bbbb-6666-6666-666666666666');

-- CHAUFFEURS
INSERT INTO chauffeuragencevoyage (chauffeurid, agencevoyageid, userid, statuschauffeur)
VALUES 
('66666661-dddd-6666-6666-666666666666', '66666661-bbbb-6666-6666-666666666666', '66666661-cccc-6666-6666-666666666666', 'LIBRE'),
('66666662-dddd-6666-6666-666666666666', '66666663-bbbb-6666-6666-666666666666', '66666662-cccc-6666-6666-666666666666', 'LIBRE');

-- CLASSES VOYAGE
INSERT INTO classvoyage (idclassvoyage, nom, prix, tauxannulation, idagencevoyage)
VALUES 
('66666661-ffff-6666-6666-666666666666', 'VIP', 15000.0, 0.8, '66666661-bbbb-6666-6666-666666666666'),
('66666662-ffff-6666-6666-666666666666', 'ÉCONOMIQUE', 8000.0, 0.5, '66666661-bbbb-6666-6666-666666666666'),
('66666663-ffff-6666-6666-666666666666', 'VIP', 12000.0, 0.8, '66666663-bbbb-6666-6666-666666666666');

-- VOYAGES
INSERT INTO voyage (idvoyage, titre, description, datedepartprev, lieudepart, lieuarrive, pointdedepart, pointarrivee, heurearrive, dureevoyage, nbrplacereservable, nbrplacereserve, nbrplaceconfirm, nbrplacerestante, datepublication, datelimitereservation, datelimiteconfirmation, statusvoyage, smallimage, bigimage, amenities)
VALUES 
('66666661-abcd-6666-6666-666666666666', 'Yaoundé → Douala', 'Voyage direct climatisé WiFi', NOW() + INTERVAL '3 days', 'Yaoundé', 'Douala', 'Mvan', 'Akwa', NOW() + INTERVAL '3 days' + INTERVAL '4 hours', 14400000000000, 40, 0, 0, 40, NOW(), NOW() + INTERVAL '2 days', NOW() + INTERVAL '2 days 22 hours', 'PUBLIE', 'https://st.depositphotos.com/1019192/4338/i/950/depositphotos_43389909-stock-photo-tourist-bus-traveling-on-road.jpg', 'https://media.istockphoto.com/id/2171315718/photo/car-for-traveling-with-a-mountain-road.jpg?s=1024x1024&w=is&k=20&c=y5XqIYLzxfb4kDTZpQgElyeiIGL34YzJrvHxbgp4Ud0=', 'WIFI,AC'),
('66666662-abcd-6666-6666-666666666666', 'Douala → Yaoundé', 'Retour rapide capitale', NOW() + INTERVAL '4 days', 'Douala', 'Yaoundé', 'Akwa', 'Mvan', NOW() + INTERVAL '4 days' + INTERVAL '4 hours', 14400000000000, 40, 0, 0, 40, NOW(), NOW() + INTERVAL '3 days', NOW() + INTERVAL '3 days 22 hours', 'PUBLIE', 'https://bougna.net/wp-content/uploads/2018/08/Bus-de-transport-de-Finex-Voyages-Mini-696x461.jpg', 'https://media.istockphoto.com/id/1161674685/photo/two-white-buses-traveling-on-the-asphalt-road-in-rural-landscape-at-sunset-with-dramatic.jpg?s=1024x1024&w=is&k=20&c=MfOEF5o2as5hiKtaVJUO94Xqn3JoU9rY-MgGjLe3pz0=', 'WIFI,AC'),
('66666663-abcd-6666-6666-666666666666', 'Bafoussam → Yaoundé', 'Confort premium', NOW() + INTERVAL '5 days', 'Bafoussam', 'Yaoundé', 'Centre-ville', 'Odza', NOW() + INTERVAL '5 days' + INTERVAL '6 hours', 21600000000000, 50, 0, 0, 50, NOW(), NOW() + INTERVAL '4 days', NOW() + INTERVAL '4 days 22 hours', 'PUBLIE', 'https://c.wallhere.com/photos/d8/b5/travel_sunset_sea_italy_public_night_landscape_dawn-751857.jpg!d', 'https://media.istockphoto.com/id/157526603/photo/white-bus-crossing-the-alpes.jpg?s=1024x1024&w=is&k=20&c=AOCRwt95N_M2HgHzSAXkdYCqjca4-p2H3XYrGFgYkDU=', 'WIFI,AC,SNACKS');

-- LIGNES VOYAGE
INSERT INTO ligne_voyage (idlignevoyage, idvoyage, idvehicule, idclassvoyage, idchauffeur, idagencevoyage)
VALUES 
('66666661-ef01-6666-6666-666666666666', '66666661-abcd-6666-6666-666666666666', '66666661-eeee-6666-6666-666666666666', '66666661-ffff-6666-6666-666666666666', '66666661-dddd-6666-6666-666666666666', '66666661-bbbb-6666-6666-666666666666'),
('66666662-ef01-6666-6666-666666666666', '66666662-abcd-6666-6666-666666666666', '66666661-eeee-6666-6666-666666666666', '66666662-ffff-6666-6666-666666666666', '66666661-dddd-6666-6666-666666666666', '66666661-bbbb-6666-6666-666666666666'),
('66666663-ef01-6666-6666-666666666666', '66666663-abcd-6666-6666-666666666666', '66666662-eeee-6666-6666-666666666666', '66666663-ffff-6666-6666-666666666666', '66666662-dddd-6666-6666-666666666666', '66666663-bbbb-6666-6666-666666666666');
