CREATE KEYSPACE IF NOT EXISTS reservation_annulation
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

USE reservation_annulation;

CREATE TYPE IF NOT EXISTS tauxperiode (
  datedebut timestamp,
  datefin timestamp,
  taux double,
  compensation double
);

CREATE TABLE IF NOT EXISTS baggage (
  idbaggage UUID PRIMARY KEY,
  nbrebaggage text,
  idpassager UUID
);

CREATE TABLE IF NOT EXISTS classvoyage (
  idclassvoyage UUID PRIMARY KEY,
  nom text,
  prix double,
  tauxannulation double,
  idagencevoyage UUID
);

CREATE TABLE IF NOT EXISTS coordonnee (
  idcoordonnee UUID PRIMARY KEY,
  latitude text,
  longitude text,
  altitude text
);

CREATE TABLE IF NOT EXISTS coupon (
  idcoupon UUID PRIMARY KEY,
  datedebut timestamp,
  datefin timestamp,
  statuscoupon text,
  valeur double,
  idhistorique UUID,
  idsoldeindemnisation UUID
);

CREATE TABLE IF NOT EXISTS historique (
  idhistorique UUID PRIMARY KEY,
  statushistorique text,
  datereservation timestamp,
  dateconfirmation timestamp,
  dateannulation timestamp,
  causeannulation text,
  origineannulation text,
  tauxannulation double,
  compensation double,
  idreservation UUID
);

CREATE TABLE IF NOT EXISTS lignevoyage (
  idlignevoyage UUID PRIMARY KEY,
  idclassvoyage UUID,
  idvehicule UUID,
  idvoyage UUID,
  idagencevoyage UUID,
  idchauffeur UUID
);

CREATE TABLE IF NOT EXISTS passager (
  idpassager UUID PRIMARY KEY,
  numeropieceidentific text,
  nom text,
  genre text,
  age int,
  nbrbaggage int,
  idreservation UUID,
  placechoisis int
);

CREATE TABLE IF NOT EXISTS politiqueannulation (
  idpolitique UUID PRIMARY KEY,
  listetauxperiode frozen<list<frozen<tauxperiode>>>,
  dureecoupon text,
  idagencevoyage UUID
);

CREATE TABLE IF NOT EXISTS reservation (
  idreservation UUID PRIMARY KEY,
  datereservation timestamp,
  dateconfirmation timestamp,
  nbrpassager int,
  prixtotal double,
  statutreservation text,
  iduser UUID,
  idvoyage UUID,
  statutpayement text,
  transactioncode text,
  montantpaye double
);

CREATE TABLE IF NOT EXISTS role (
  id UUID PRIMARY KEY,
  libelle text
);

CREATE TABLE IF NOT EXISTS soldeindemnisation (
  idsolde UUID PRIMARY KEY,
  solde double,
  type text,
  iduser UUID,
  idagencevoyage UUID
);

CREATE TABLE IF NOT EXISTS user (
  userid UUID PRIMARY KEY,
  nom text,
  prenom text,
  email text,
  password text,
  telnumber text,
  role text,
  address text,
  idcoordonneeGPS UUID,
  username text,
  genre text,
  businessActorType text
);

CREATE TABLE IF NOT EXISTS vehicule (
  idvehicule UUID PRIMARY KEY,
  nom text,
  modele text,
  description text,
  nbrplaces int,
  plaquematricule text,
  lienphoto text,
  idagencevoyage UUID
);

CREATE TABLE IF NOT EXISTS voyage (
  idvoyage UUID PRIMARY KEY,
  titre text,
  description text,
  datedepartprev timestamp,
  lieudepart text,
  datedeparteffectif timestamp,
  datearriveeffectif timestamp,
  lieuarrive text,
  heuredeparteffectif timestamp,
  pointdedepart text,
  pointarrivee text,
  dureevoyage text,
  heurearrive timestamp,
  nbrplacereservable int,
  nbrplacereserve int,
  nbrplaceconfirm int,
  nbrplacerestante int,
  datepublication timestamp,
  datelimitereservation timestamp,
  datelimiteconfirmation timestamp,
  statusvoyage text,
  smallimage text,
  bigimage text,
  amenities text
);

CREATE TABLE IF NOT EXISTS agencevoyage (
    agencyid UUID PRIMARY KEY,
    organisationid UUID,
    userid UUID,
    longname text,
    shortname text,
    location text,
    socialnetwork text,
    description text,
    greetingmessage text
);

CREATE TABLE IF NOT EXISTS organization (
    id UUID PRIMARY KEY,
    createdat timestamp,
    updatedat timestamp,
    deletedat timestamp,
    createdby UUID,
    updatedby UUID,
    organizationid UUID,
    businessdomains list<UUID>,
    email text,
    shortname text,
    longname text,
    description text,
    logourl text,
    isindividualbusiness boolean,
    legalform text,
    isactive boolean,
    websiteurl text,
    socialnetwork text,
    businessregistrationnumber text,
    taxnumber text,
    capitalshare double,
    registrationdate timestamp,
    ceoname text,
    yearfounded timestamp,
    keywords list<text>,
    status text
);

CREATE TABLE IF NOT EXISTS chauffeuragencevoyage (
    chauffeurid UUID PRIMARY KEY,
    agencevoyageid UUID,
    userid UUID,
    statuschauffeur text
);

CREATE TABLE IF NOT EXISTS employeagencevoyage (
   employeid UUID PRIMARY KEY,
   agencevoyageid UUID,
   userid UUID,
   poste text,
   dateembauche timestamp,
   datefincontrat timestamp,
   statutemploye text,
   salaire double,
   departement text,
   managerid UUID
);