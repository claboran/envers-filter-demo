-- liquibase formatted sql
-- changeset yourname:4

-- Modify the products table to make tech_details_id and description_id columns nullable
ALTER TABLE products
    ALTER COLUMN tech_details_id DROP NOT NULL,
    ALTER COLUMN description_id DROP NOT NULL;

-- Update the audit table to reflect the same changes
ALTER TABLE products_AUD
    ALTER COLUMN tech_details_id DROP NOT NULL,
    ALTER COLUMN description_id DROP NOT NULL;