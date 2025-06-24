-- liquibase formatted sql
-- changeset yourname:2

CREATE TABLE product_tech_details
(
    id                     UUID PRIMARY KEY,
    technical_details_json JSONB NOT NULL
);

CREATE TABLE product_descriptions
(
    id               UUID PRIMARY KEY,
    description_json JSONB NOT NULL
);

CREATE TABLE products
(
    id                 UUID PRIMARY KEY,
    creation_timestamp TIMESTAMPTZ  NOT NULL,
    product_name       VARCHAR(255) NOT NULL,
    status             VARCHAR(255) NOT NULL,
    tech_details_id    UUID UNIQUE REFERENCES product_tech_details (id),
    description_id     UUID UNIQUE REFERENCES product_descriptions (id)
);

-- changeset yourname:3

CREATE SEQUENCE revinfo_seq START WITH 1 INCREMENT BY 50;

-- Envers requires a REVINFO table to store revision metadata
CREATE TABLE REVINFO
(
    REV      BIGINT NOT NULL DEFAULT nextval('revinfo_seq'),
    REVTSTMP BIGINT,
    PRIMARY KEY (REV)
);

-- Audit table for the 'products' entity
CREATE TABLE products_AUD
(
    id                 UUID   NOT NULL,
    REV                BIGINT NOT NULL,
    REVTYPE            SMALLINT,
    creation_timestamp TIMESTAMPTZ,
    product_name       VARCHAR(255),
    status             VARCHAR(255),
    tech_details_id    UUID,
    description_id     UUID,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO (REV)
);

-- Audit table for the 'product_tech_details' entity
CREATE TABLE product_tech_details_AUD
(
    id                     UUID NOT NULL,
    REV                    BIGINT NOT NULL,
    REVTYPE                SMALLINT,
    technical_details_json JSONB,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO (REV)
);

-- Audit table for the 'product_descriptions' entity
CREATE TABLE product_descriptions_AUD
(
    id               UUID  NOT NULL,
    REV              BIGINT NOT NULL,
    REVTYPE          SMALLINT,
    description_json JSONB,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES REVINFO (REV)
);