package com.user_auth.users.specification;

import com.user_auth.entity.Role;
import com.user_auth.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasId(Long id) {
        return (root, query, cb) ->
                id == null
                        ? null
                        : cb.equal(root.get("id"), id);
    }

    public static Specification<User> hasUserName(String userName) {
        return (root, query, cb) ->
                userName == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("userName")),
                        "%" + userName.toLowerCase() + "%");
    }
    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) ->
                email == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%");
    }
    public static Specification<User> hasFullName(String fullName) {
        return (root, query, cb) ->
                fullName == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("fullName")),
                        "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<User> hasRole(Role role) {
        return (root, query, cb) ->
                role == null
                        ? null
                        : cb.equal(root.get("role"), role);
    }

    public static Specification<User> hasPassword(String password) {
        return (root, query, cb) ->
                password == null
                        ? null
                        : cb.like(
                        cb.lower(root.get("password")),
                        "%" + password.toLowerCase() + "%");
    }
    public static Specification<User> hasActive(Boolean active) {
        return (root, query, cb) ->
                active == null
                        ? null
                        : cb.equal(root.get("active"), active);
    }

}
