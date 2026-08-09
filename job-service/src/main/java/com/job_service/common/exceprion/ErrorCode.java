package com.job_service.common.exceprion;

import jakarta.persistence.AssociationOverride;

public enum ErrorCode {

    USER_NOT_FOUND,

    JOB_NOT_FOUND,

    APPLICATION_NOT_FOUND,

    EMAIL_ALREADY_EXISTS,

    INTERNAL_SERVER_ERROR,

    VALIDATION_ERROR,

    SALARY_NOT_VALID,

    ACCESS_DENIED;



}