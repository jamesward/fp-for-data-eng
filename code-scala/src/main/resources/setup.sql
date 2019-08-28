CREATE TABLE Person (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    favorite_color TEXT NOT NULL
);

CREATE TABLE Contact (
    id SERIAL PRIMARY KEY,
    email TEXT,
    person_id INTEGER REFERENCES Person(id)
);


INSERT INTO Person (name, favorite_color) VALUES ('James Ward', 'blue');
INSERT INTO Person (name, favorite_color) VALUES ('Josie Ward', 'purple');
INSERT INTO Person (name, favorite_color) VALUES ('Jonah Ward', 'blue');

INSERT INTO Contact (email, person_id) VALUES ('james@jamesward.com', (SELECT id FROM Person WHERE name = 'James Ward'));
INSERT INTO Contact (person_id) VALUES (SELECT id FROM Person WHERE name = 'Josie Ward');
INSERT INTO Contact (person_id) VALUES (SELECT id FROM Person WHERE name = 'Jonah Ward');
