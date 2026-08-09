
CREATE TABLE user(
    id BIGINT PRIMARY Key AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(250)  NOT NULL,
    full_name VARCHAR(100)  NOT NULL,
    role VARCHAR(15)  NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by varchar(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by varchar(50) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

INSERT INTO user VALUES (
            1,
            "yousef",
            "yousef123@gmail.com",
            "$2a$12$bF2oN8yOshPz5FYyfgv1uOhVgZGFZ90vguBh9Jsxl/UzCOmZk8ATG",
            "Yousef Abdelrahman",
            "ADMIN",
            1,
            "system",
            now(),
            "system",
    		now()
            );