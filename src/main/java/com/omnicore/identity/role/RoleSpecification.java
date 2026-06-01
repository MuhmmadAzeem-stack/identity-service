package com.omnicore.identity.role;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class RoleSpecification {

  private RoleSpecification() {}

  public static Specification<Role> withFilters(String keyword, Boolean active, Boolean system) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(notDeleted(root, criteriaBuilder));

      boolean activeValue = active != null ? active : true;
      predicates.add(criteriaBuilder.equal(root.get("active"), activeValue));

      if (system != null) {
        predicates.add(criteriaBuilder.equal(root.get("system"), system));
      }

      if (StringUtils.hasText(keyword)) {
        String pattern = "%" + keyword.toLowerCase() + "%";
        predicates.add(
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)));
      }

      return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    };
  }

  private static Predicate notDeleted(Root<Role> root, CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.isNull(root.get("deletedAt"));
  }
}
