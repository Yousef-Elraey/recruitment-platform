CREATE TABLE candidate(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
user_id BIGINT UNIQUE NOT NULL,
phone varchar(20) NOT NULL,
address varchar(100) NOT NULL,
summary varchar(2000) NOT NULL,
status varchar(20) NOT NULL DEFAULT "IS_EXISTED",
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
 );

CREATE TABLE experience(
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
    FOREIGN KEY(candidate_id) REFERENCES candidate(id)
);

CREATE TABLE skill(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
skill_name varchar(100) UNIQUE NOT NULL,
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE candidate_skill(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
candidate_id BIGINT NOT NULL,
skill_id BIGINT NOT NULL,
years_of_experience INTEGER NOT NULL,
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

 CONSTRAINT FK_candidate_candidate_skill
    FOREIGN KEY(candidate_id) REFERENCES candidate(id),
 CONSTRAINT FK_skill_candidate_skill
    FOREIGN KEY(skill_id) REFERENCES skill(id)
);

CREATE TABLE education(
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
    FOREIGN KEY(candidate_id) REFERENCES candidate(id)
);
