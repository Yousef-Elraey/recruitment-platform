CREATE TABLE candidates(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT NOT NULL,
phone varchar(20) NOT NULL,
address varchar(100) NOT NULL,
summary varchar(2000) NOT NULL,
status varchar(20) DEFAULT "ACTIVE",
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 );

CREATE TABLE experiences(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
candidate_id BIGINT NOT NULL,
company_name varchar(50) NOT NULL,
job_title varchar(50) NOT Null,
start_date Date NOT Null,
end_date Date,
current Boolean NOT NULL DEFAULT TRUE,
description varchar(2000),
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

 CONSTRAINT FK_experience_candidate
    FOREIGN KEY(candidate_id) REFERENCES candidates(id)
);

CREATE TABLE skills(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
skill_name varchar(100) UNIQUE NOT NULL,
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE candidate_skills(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
candidate_id BIGINT NOT NULL,
skill_id BIGINT NOT NULL,
years_of_experience INTEGER NOT NULL,
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

 CONSTRAINT FK_candidate_candidate_skill
    FOREIGN KEY(candidate_id) REFERENCES candidates(id),
 CONSTRAINT FK_skill_candidate_skill
    FOREIGN KEY(skill_id) REFERENCES skills(id)
);

CREATE TABLE educations(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
candidate_id BIGINT NOT NULL,
institution varchar(100) NOT NULL,
degree varchar(100) NOT NULL,
field_of_study varchar(100) NOT NULL,
start_date Date NOT NULL,
end_date Date,
current Boolean NOT NULL DEFAULT TRUE,
description varchar(2000),
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,


 CONSTRAINT FK_candidate_candidate_education
    FOREIGN KEY(candidate_id) REFERENCES candidates(id)
);
CREATE TABLE cvs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_data LONGBLOB NOT NULL
);

CREATE TABLE cv_file_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    candidate_id BIGINT NOT NULL,
    cv_id BIGINT UNIQUE NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL,
    retry_counter BIGINT DEFAULT 0,
    version VARCHAR(30) DEFAULT "LATEST",


    CONSTRAINT fk_cv_info_candidate
        FOREIGN KEY (candidate_id) REFERENCES candidates(id),

    CONSTRAINT fk_cv_info_cv
        FOREIGN KEY (cv_id) REFERENCES cvs(id)
);

CREATE TABLE cv_parsing_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cv_id BIGINT UNIQUE NOT NULL,
    personal_info JSON,
    summary text,
    skills JSON,
    experience JSON,
    education JSON,
    certifications JSON,
    projects JSON,
    languages JSON,
    parsed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_cv_parsing_data_cv
        FOREIGN KEY (cv_id)
        REFERENCES cvs(id)

);