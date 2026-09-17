INSERT
OR IGNORE INTO movie (id, name, director) VALUES
   (1, 'The Shawshank Redemption', 'Frank Darabont'),
   (2, 'The Godfather', 'Francis Ford Coppola'),
   (3, 'The Dark Knight', 'Christopher Nolan'),
   (4, 'Inception', 'Christopher Nolan'),
   (5, 'Pulp Fiction', 'Quentin Tarantino'),
   (6, 'Schindler''s List', 'Steven Spielberg'),
   (7, 'The Lord of the Rings: The Return of the King', 'Peter Jackson'),
   (8, 'Fight Club', 'David Fincher'),
   (9, 'Forrest Gump', 'Robert Zemeckis'),
   (10, 'The Matrix', 'Lana Wachowski, Lilly Wachowski');

INSERT
OR IGNORE INTO room (id)
VALUES (1),
       (2),
       (3),
       (4),
       (5);

INSERT
OR IGNORE INTO seat (id, room_id)
VALUES
-- Room 1
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
-- Room 2
(6, 2),
(7, 2),
(8, 2),
(9, 2),
(10, 2),
-- Room 3
(11, 3),
(12, 3),
(13, 3),
(14, 3),
(15, 3),
-- Room 4
(16, 4),
(17, 4),
(18, 4),
(19, 4),
(20, 4),
-- Room 5
(21, 5),
(22, 5),
(23, 5),
(24, 5),
(25, 5);

