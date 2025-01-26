CREATE DATABASE trano;

\c trano;

CREATE EXTENSION postgis;

CREATE TABLE type_caracteristique(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL
);

INSERT INTO type_caracteristique(nom) VALUES('Rindrina') , ('Tafo');

CREATE TABLE caracteristique(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    id_type_caracteristique INTEGER NOT NULL REFERENCES type_caracteristique(id),
    coefficient NUMERIC NOT NULL CHECK(coefficient > 0)
);

INSERT INTO caracteristique (nom,id_type_caracteristique,coefficient) VALUES 
('Hazo',1,0.8),
('Brique',1,1.1),
('Beton',1,1.2),
('Bozaka',2,0.6),
('Tuile',2,0.8),
('Tole',2,1.1),
('Beton',2,1.4);

CREATE TABLE commune(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100)
);

INSERT INTO commune(nom) VALUES ('CUA') , ('CUB');

CREATE TABLE commune_tarif(
    id SERIAL PRIMARY KEY,
    date TIMESTAMP,
    id_commune int not null references commune(id),
    tarif NUMERIC NOT NULL CHECK(tarif >  0)
);

INSERT INTO commune_tarif(date, tarif, id_commune) VALUES 
('2023-01-01 00:00:00', 100.0, 1),
('2023-01-01 00:00:00', 100.0, 2);

CREATE TABLE proprietaire(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255)
);

INSERT INTO proprietaire (nom) VALUES ('p1');
INSERT INTO proprietaire (nom) VALUES ('p2');
INSERT INTO proprietaire (nom) VALUES ('p3');
INSERT INTO proprietaire (nom) VALUES ('p4');
INSERT INTO proprietaire (nom) VALUES ('p5');
INSERT INTO proprietaire (nom) VALUES ('p6');
INSERT INTO proprietaire (nom) VALUES ('p7');
INSERT INTO proprietaire (nom) VALUES ('p8');
INSERT INTO proprietaire (nom) VALUES ('p9');
INSERT INTO proprietaire (nom) VALUES ('p10');


CREATE TABLE maison(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    id_commune INT NOT NULL REFERENCES commune(id),
    geom GEOMETRY(Point , 4326) NOT NULL
);

CREATE TABLE etat_maison(
    id SERIAL PRIMARY KEY,
    id_proprietaire INT NOT NULL REFERENCES proprietaire(id),
    id_maison INT NOT NULL REFERENCES maison(id),
    longueur NUMERIC NOT NULL CHECK(longueur > 0),
    largeur NUMERIC NOT NULL CHECK(largeur > 0),
    etage INT NOT NULL CHECK(etage >= 0),
    date TIMESTAMP
);



CREATE TABLE maison_caracteristique(
    id SERIAL PRIMARY KEY,
    id_maison INTEGER NOT NULL REFERENCES maison(id),
    id_caracteristique INTEGER NOT NULL REFERENCES caracteristique(id),
    id_type_caracteristique INTEGER NOT NULL REFERENCES type_caracteristique(id),
    date TIMESTAMP
);

INSERT INTO maison (nom, longueur, largeur, etage, geom) 
VALUES 
('Trano1', 400, 200, 2, ST_SetSRID(ST_MakePoint(46.984406, -18.868592), 4326)),
('Trano2', 150, 90, 1, ST_SetSRID(ST_MakePoint(47.308502, -18.807757), 4326)),
('Trano3', 600, 700, 3, ST_SetSRID(ST_MakePoint(47.223358, -18.759544), 4326)),
('Trano4', 300, 150, 1, ST_SetSRID(ST_MakePoint(47.985535, -18.63468), 4326)),
('Trano5', 540, 260, 2, ST_SetSRID(ST_MakePoint(48.002014, -18.755723), 4326)),
('Trano6', 470, 350, 3, ST_SetSRID(ST_MakePoint(47.960815, -18.802319), 4326)),
('Trano7', 220, 100, 1, ST_SetSRID(ST_MakePoint(47.562561, -19.176731), 4326)),
('Trano8', 600, 210, 2, ST_SetSRID(ST_MakePoint(47.643585, -19.235121), 4326)),
('Trano9', 500, 400, 3, ST_SetSRID(ST_MakePoint(47.392273, -19.180624), 4326)),
('Trano10', 250, 300, 4, ST_SetSRID(ST_MakePoint(47.60376, -18.491392), 4326)),
('Trano11', 260, 100, 3, ST_SetSRID(ST_MakePoint(47.584534, -18.535692), 4326)),
('Trano12', 255.5, 200, 2, ST_SetSRID(ST_MakePoint(47.727356, -18.521361), 4326));

INSERT INTO maison (nom, id_commune, geom)
VALUES
('Trano1', 1, ST_SetSRID(ST_MakePoint(46.984406, -18.868592), 4326)),
('Trano2', 1, ST_SetSRID(ST_MakePoint(47.308502, -18.807757), 4326)),
('Trano3', 1, ST_SetSRID(ST_MakePoint(47.223358, -18.759544), 4326)),
('Trano4', 1, ST_SetSRID(ST_MakePoint(47.985535, -18.634068), 4326)),
('Trano5', 1, ST_SetSRID(ST_MakePoint(48.002014, -18.755723), 4326)),
('Trano6', 1, ST_SetSRID(ST_MakePoint(47.960815, -18.802319), 4326)),
('Trano7', 1, ST_SetSRID(ST_MakePoint(47.652561, -19.176731), 4326)),
('Trano8', 1, ST_SetSRID(ST_MakePoint(47.643585, -19.235121), 4326)),
('Trano9', 1, ST_SetSRID(ST_MakePoint(47.392273, -19.180624), 4326)),
('Trano10', 1, ST_SetSRID(ST_MakePoint(47.60376, -18.491392), 4326)),
('Trano11', 1, ST_SetSRID(ST_MakePoint(47.584534, -18.553692), 4326)),
('Trano12', 1, ST_SetSRID(ST_MakePoint(47.727356, -18.521361), 4326));

-- Insertion des états des maisons
INSERT INTO etat_maison (id_proprietaire, id_maison, longueur, largeur, etage, date)
VALUES
(1, 1, 400, 200, 2, '2023-01-01'),
(1, 2, 150, 90, 1, '2023-01-01'),
(1, 3, 600, 700, 3, '2023-01-01'),
(1, 4, 300, 150, 1, '2023-01-01'),
(1, 5, 540, 260, 2, '2023-01-01'),
(1, 6, 470, 350, 3, '2023-01-01'),
(1, 7, 220, 100, 1, '2023-01-01'),
(1, 8, 600, 210, 2, '2023-01-01'),
(1, 9, 500, 400, 3, '2023-01-01'),
(1, 10, 250, 300, 4, '2023-01-01'),
(1, 11, 260, 100, 3, '2023-01-01'),
(1, 12, 255.5, 200, 2, '2023-01-01');

INSERT INTO maison_caracteristique (id_maison, id_caracteristique, id_type_caracteristique, date)
VALUES
(1, 3, 1, '2023-01-01'), -- Trano1 : Rindrina Beton
(1, 6, 2, '2023-01-01'), -- Trano1 : Tafo Tole
(2, 2, 1, '2023-01-01'), -- Trano2 : Rindrina Brique
(2, 5, 2, '2023-01-01'), -- Trano2 : Tafo Tuile
(3, 1, 1, '2023-01-01'), -- Trano3 : Rindrina Hazo
(3, 5, 2, '2023-01-01'), -- Trano3 : Tafo Tuile
(4, 3, 1, '2023-01-01'), -- Trano4 : Rindrina Beton
(4, 4, 2, '2023-01-01'), -- Trano4 : Tafo Bozaka
(5, 2, 1, '2023-01-01'), -- Trano5 : Rindrina Brique
(5, 5, 2, '2023-01-01'), -- Trano5 : Tafo Tuile
(6, 1, 1, '2023-01-01'), -- Trano6 : Rindrina Hazo
(6, 7, 2, '2023-01-01'), -- Trano6 : Tafo Beton
(7, 2, 1, '2023-01-01'), -- Trano7 : Rindrina Brique
(7, 6, 2, '2023-01-01'), -- Trano7 : Tafo Tole
(8, 1, 1, '2023-01-01'), -- Trano8 : Rindrina Hazo
(8, 7, 2, '2023-01-01'), -- Trano8 : Tafo Beton
(9, 3, 1, '2023-01-01'), -- Trano9 : Rindrina Beton
(9, 4, 2, '2023-01-01'), -- Trano9 : Tafo Bozaka
(10, 3, 1, '2023-01-01'), -- Trano10 : Rindrina Beton
(10, 5, 2, '2023-01-01'), -- Trano10 : Tafo Tuile
(11, 1, 1, '2023-01-01'), -- Trano11 : Rindrina Hazo
(11, 5, 2, '2023-01-01'), -- Trano11 : Tafo Tuile
(12, 2, 1, '2023-01-01'), -- Trano12 : Rindrina Brique
(12, 6, 2, '2023-01-01'); -- Trano12 : Tafo Tole


CREATE TABLE arrondissement(
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    geom GEOMETRY(Polygon , 4326) NOT NULL
);


CREATE TABLE paiement_impot(
    id SERIAL PRIMARY KEY,
    id_maison INTEGER NOT NULL REFERENCES maison(id),
    annee INTEGER NOT NULL CHECK(annee > 0),
    mois INTEGER NOT NULL CHECK(mois > 0 AND mois < 13),
    UNIQUE(id_maison, annee , mois)
);

-- Insertion de l'arrondissement avec les coordonnées données
INSERT INTO arrondissement (nom, geom)
VALUES (
    '1er arrondissement',
    ST_GeomFromText(
        'POLYGON((47.080536 -18.589103, 
                  46.796265 -18.840271, 
                  46.918488 -18.99235, 
                  47.176666 -19.028725, 
                  47.558441 -18.848073, 
                  47.513123 -18.643793, 
                  47.194519 -18.563054, 
                  47.080536 -18.589103))', 
        4326
    )
);

-- Insertion du 2e arrondissement avec les coordonnées données
INSERT INTO arrondissement (nom, geom)
VALUES (
    '2e arrondissement',
    ST_GeomFromText(
        'POLYGON((47.794647 -18.714083, 
                  47.794647 -18.776539, 
                  47.849579 -18.842872, 
                  47.900391 -18.89228, 
                  48.010254 -18.958568, 
                  48.132477 -18.776539, 
                  48.253326 -18.655511, 
                  48.212128 -18.539607, 
                  48.063812 -18.482278, 
                  47.897644 -18.466639, 
                  47.922363 -18.496612, 
                  47.794647 -18.714083))', 
        4326
    )
);


-- Insertion du 3e arrondissement avec les coordonnées données
INSERT INTO arrondissement (nom, geom)
VALUES (
    '3e arrondissement',
    ST_GeomFromText(
        'POLYGON((47.327728 -19.07807, 
                  47.617493 -19.075472, 
                  47.765808 -19.272739, 
                  47.436218 -19.357025, 
                  47.264557 -19.266254, 
                  47.327728 -19.07807))', 
        4326
    )
);


-- Insertion du 4e arrondissement avec les coordonnées données
INSERT INTO arrondissement (nom, geom)
VALUES (
    '4e arrondissement',
    ST_GeomFromText(
        'POLYGON((47.463684 -18.377986, 
                  47.590027 -18.691951, 
                  47.82486 -18.552627, 
                  47.627106 -18.329732, 
                  47.463684 -18.377986))', 
        4326
    )
);

