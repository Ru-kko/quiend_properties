CREATE TABLE City (
    cityId UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO City (cityId, name) VALUES
    ('b81e8a80-4375-4b8a-b5cf-12e5699f4cd3', 'Medellin'),
    ('a4b2c9d7-258e-4f2f-a1ad-1c7f5f2a9d75', 'Bogota'),
    ('c21d6f5e-7b58-4d81-9fc8-91e7c69d6e9a', 'Cali'),
    ('e9c1a570-dbe4-4a2e-a71e-2fd5a7b7f123', 'Cartagena');

CREATE TYPE "ROLE" AS ENUM ('GUEST', 'ADMIN', 'USER');

CREATE TABLE "User" (
    userId UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255),
    age INTEGER NOT NULL,
    password VARCHAR(255) NOT NULL,
    role "ROLE" NOT NULL
);

CREATE TABLE Property (
    propertyId UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    cityId UUID NOT NULL,
    img VARCHAR(255) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    available BOOLEAN NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    dateCreated DATE NOT NULL,
    FOREIGN KEY (cityId) REFERENCES City(cityId)
);

CREATE TABLE Rent (
    rentId UUID PRIMARY KEY,
    userId UUID NOT NULL,
    propertyId UUID NOT NULL,
    rentDate DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (userId) REFERENCES "User"(userId),
    FOREIGN KEY (propertyId) REFERENCES Property(propertyId)
);