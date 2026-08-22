CREATE TABLE face(
    face_code VARCHAR(30) UNIQUE NOT NULL,
    face_order INTEGER NOT NULL
);


INSERT INTO face VALUES('APPLY', '1'),
                        ('UNDER_REVIEW', '2'),
                        ('INTERVIEW', '3'),
                        ('PENDING_APPROVAL', '4'),
                        ('APPROVE', '5'),
                        ('REJECTED', '0');

CREATE TABLE application(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
candidate_id BIGINT NOT NULL,
job_id BIGINT NOT NULL,
recruiter_id BIGINT DEFAULT NULL,
face_code varchar(30) DEFAULT "APPLY",
feedback text,
score FLOAT,
created_by varchar(250) NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_by varchar(250) NOT NULL,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_application_face
         FOREIGN KEY(face_code) REFERENCES face(face_code)

);


CREATE TABLE candidate_face(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT,
    face_code VARCHAR(30),
    updated_by varchar(250) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_candidate_face_application
     FOREIGN KEY(application_id) REFERENCES application(id),

    CONSTRAINT fk_candidate_face_face
      FOREIGN KEY(face_code) REFERENCES face(face_code)
);
