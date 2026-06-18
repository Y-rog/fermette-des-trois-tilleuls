-- Données de test — Fermette des Trois Tilleuls

INSERT INTO gites (name, description, location, capacity, bedrooms, photo_url, active)
VALUES (
           'Gîte du Fay',
           'Notre nouvelle maison, jusqu''à 6 personnes, vue sur les pâtures et à deux pas des alpagas.',
           'Le Fay',
           6, 3, null, true
       );

INSERT INTO gites (name, description, location, capacity, bedrooms, photo_url, active)
VALUES (
           'Gîte à Gournay',
           'Maison de charme à Gournay, disponible à la location en attendant sa vente.',
           'Gournay',
           5, 2, null, true
       );

INSERT INTO activities (name, description, date, time, active)
VALUES (
           'Balade en alpagas',
           'Une heure de promenade accompagnée à travers les sentiers
            de la fermette en compagnie de nos alpagas.',
           '2026-07-15',
           '10:00:00',
           true
       );

INSERT INTO activities (name, description, date, time, active)
VALUES (
           'Balade en alpagas',
           'Une heure de promenade accompagnée à travers les sentiers
            de la fermette en compagnie de nos alpagas.',
           '2026-07-15',
           '14:00:00',
           true
       );