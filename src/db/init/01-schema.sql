CREATE TABLE IF NOT EXISTS articles (
    id SERIAL PRIMARY KEY,
    public_id VARCHAR(255) NOT NULL,
    description VARCHAR(200) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    version INTEGER,
    status VARCHAR(255) NOT NULL,
    edited_by VARCHAR(255),
    is_editable BOOLEAN NOT NULL,
    is_submitted BOOLEAN NOT NULL,
    deny_text VARCHAR(255) NULL
    );

CREATE  TABLE IF NOT EXISTS app_user
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL
);
