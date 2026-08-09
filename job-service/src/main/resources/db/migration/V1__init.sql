CREATE TABLE job(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title varchar(20) NOT NULL,
    description varchar(250) NOT NULL,
    company_name varchar(20) NOT NULL,
    location varchar(50) NOT NULL,
    employment_type varchar(10) NOT NULL,
    work_mode varchar(10) NOT NULL,
    experience_level INTEGER NOT NULL,
    salary_min BIGINT NOT NULL,
    salary_max BIGINT NOT NULL,
    status varchar(15) NOT NULL DEFAULT "OPEN",
    created_by varchar(250) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by varchar(250) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
