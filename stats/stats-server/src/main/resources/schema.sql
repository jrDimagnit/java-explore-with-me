DROP TABLE IF EXISTS stat;
CREATE TABLE IF NOT EXISTS stat
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       VARCHAR(255)                            NOT NULL,
    uri       VARCHAR(512)                            NOT NULL,
    ip        VARCHAR(512)                            NOT NULL,
    timeStamp TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_stat PRIMARY KEY (id)
    );