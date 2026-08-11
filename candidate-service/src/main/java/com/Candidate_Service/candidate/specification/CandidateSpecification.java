package com.Candidate_Service.candidate.specification;

import com.Candidate_Service.entity.Candidate;
import org.springframework.data.jpa.domain.Specification;

public class CandidateSpecification {
    public static Specification<Candidate> hasPhone(String phone) {
        return (root, query, cb) ->
                phone == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("phone")),
                        "%" + phone.toLowerCase() + "%");
    }
    public static Specification<Candidate> hasAddress(String address) {
        return (root, query, cb) ->
                address == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("address")),
                        "%" + address.toLowerCase() + "%");
    }
    public static Specification<Candidate> hasSummary(String summary) {
        return (root, query, cb) ->
                summary == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("summary")),
                        "%" + summary.toLowerCase() + "%");
    }

    public static Specification<Candidate> hasUserId(Long userId) {
        return (root, query, cb) ->
                userId == null
                        ? null
                        : cb.equal(root.get("userId"), userId);
    }

}
