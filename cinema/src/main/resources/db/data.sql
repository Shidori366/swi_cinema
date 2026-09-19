INSERT
OR IGNORE INTO movie (id, name, director, duration) VALUES
   (1, 'The Shawshank Redemption', 'Frank Darabont', 142),
   (2, 'The Godfather', 'Francis Ford Coppola', 175),
   (3, 'The Dark Knight', 'Christopher Nolan', 152),
   (4, 'Inception', 'Christopher Nolan', 148),
   (5, 'Pulp Fiction', 'Quentin Tarantino', 154),
   (6, 'Schindler''s List', 'Steven Spielberg', 195),
   (7, 'The Lord of the Rings: The Return of the King', 'Peter Jackson', 201),
   (8, 'Fight Club', 'David Fincher', 139),
   (9, 'Forrest Gump', 'Robert Zemeckis', 142),
   (10, 'The Matrix', 'Lana Wachowski, Lilly Wachowski', 136);

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

INSERT INTO screening (id, time, movie_id, room_id) VALUES
    (101, datetime('now', '+' || (abs(random()) % 30) || ' days', '+' || (abs(random()) % 24) || ' hours'), 1, 1),
    (102, datetime('now', '+' || (abs(random()) % 30) || ' days', '+' || (abs(random()) % 24) || ' hours'), 2, 2),
    (103, datetime('now', '+' || (abs(random()) % 30) || ' days', '+' || (abs(random()) % 24) || ' hours'), 3, 3),
    (104, datetime('now', '+' || (abs(random()) % 30) || ' days', '+' || (abs(random()) % 24) || ' hours'), 1, 4),
    (105, datetime('now', '+' || (abs(random()) % 30) || ' days', '+' || (abs(random()) % 24) || ' hours'), 2, 5);

INSERT INTO reservation (id, screening_id, reservation_status, created_at, contact_email) VALUES
    (1, 101, 'RESERVED', datetime('now'), 'example@gmail.com'),
    (2, 102, 'PENDING', datetime('now'), 'example2@gmail.com'),
    (3, 101, 'RESERVED', datetime('now'), 'example3@gmail.com');

INSERT INTO payment (id, reservation_id, amount, currency, payment_method, payment_status, created_at) VALUES
    (1, 1, 350.00, 'CZK', 'CREDIT_CARD', 'COMPLETED', datetime('now')),
    (2, 2, 500.00, 'CZK', 'CREDIT_CARD', 'PENDING', datetime('now'));

INSERT INTO seat_reservation (reservation_id, seat_id) VALUES
    (1, 1),
    (1, 2),
    (2, 10),
    (3, 3);