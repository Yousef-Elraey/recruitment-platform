package com.job_service.job.specification;

import com.job_service.entity.EmploymentType;
import com.job_service.entity.Job;
import com.job_service.entity.Status;
import com.job_service.entity.WorkMode;
import org.springframework.data.jpa.domain.Specification;

public class JobSpecification {
    public static Specification<Job> hasTitle(String title) {
        return (root, query, cb) ->
                title == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%");
    }
    public static Specification<Job> hasDescription(String description) {
        return (root, query, cb) ->
                description == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%");
    }
    public static Specification<Job> hasCompanyName(String companyName) {
        return (root, query, cb) ->
                companyName == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("companyName")),
                        "%" + companyName.toLowerCase() + "%");
    }
    public static Specification<Job> hasLocation(String location) {
        return (root, query, cb) ->
                location == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("location")),
                        "%" + location.toLowerCase() + "%");
    }
    public static Specification<Job> hasEmploymentType(EmploymentType type) {
        return (root, query, cb) ->
                type == null
                        ? null
                        : cb.equal(root.get("employmentType"), type);
    }
    public static Specification<Job> hasWorkMode(WorkMode workMode) {
        return (root, query, cb) ->
                workMode == null
                        ? null
                        : cb.equal(root.get("workMode"), workMode);
    }
    public static Specification<Job> hasExperienceLevel(Integer experienceLevel) {
        return (root, query, cb) ->
                experienceLevel == null
                        ? null
                        : cb.equal(root.get("experienceLevel"), experienceLevel);
    }
    public static Specification<Job> hasSalaryMin(Long salaryMin) {
        return (root, query, cb) ->
                salaryMin == null
                        ? null
                        : cb.equal(root.get("salaryMin"), salaryMin);
    }
    public static Specification<Job> hasSalaryMax(Long salaryMax) {
        return (root, query, cb) ->
                salaryMax == null
                        ? null
                        : cb.equal(root.get("salaryMax"), salaryMax);
    }
    public static Specification<Job> hasStatus(Status status) {
        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(root.get("status"), status);
    }

}
