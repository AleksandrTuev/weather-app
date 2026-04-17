--liquibase formatted sql

--changeset Alext_v:create-table-sessions
CREATE TABLE sessions (
    id UUID NOT NULL,
    user_id INT NOT NULL,
    expires_at TIMESTAMP NOT NULL
);