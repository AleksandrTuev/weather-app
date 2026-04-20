--liquibase formatted sql

--changeset Alext_v:create-table-sessions
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT count(*) FROM information_schema.tables WHERE table_name = 'sessions'
CREATE TABLE sessions (
    id UUID NOT NULL,
    user_id INT NOT NULL,
    expires_at TIMESTAMP NOT NULL
);