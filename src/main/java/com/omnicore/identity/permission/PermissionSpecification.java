package com.omnicore.identity.permission;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class PermissionSpecification {

  private PermissionSpecification() {}

  public static Specification<Permission> withFilters(
      String keyword, String module, String action, Boolean active, Boolean system) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      boolean activeValue = active != null ? active : true;
      predicates.add(criteriaBuilder.equal(root.get("active"), activeValue));

      // Soft-deleted permissions appear only when explicitly filtering inactive records.
      if (active == null || active) {
        predicates.add(notDeleted(root, criteriaBuilder));
      }

      if (system != null) {
        predicates.add(criteriaBuilder.equal(root.get("system"), system));
      }

      if (StringUtils.hasText(module)) {
        predicates.add(criteriaBuilder.equal(root.get("module"), module));
      }

      if (StringUtils.hasText(action)) {
        predicates.add(criteriaBuilder.equal(root.get("action"), action));
      }

      if (StringUtils.hasText(keyword)) {
        String pattern = "%" + keyword.toLowerCase() + "%";
        predicates.add(
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("module")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("action")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)));
      }

      return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    };
  }

  private static Predicate notDeleted(Root<Permission> root, CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.isNull(root.get("deletedAt"));
  }
}
