package com.omnicore.identity.role;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.omnicore.identity.role.error.RoleBusinessException;
import com.omnicore.identity.role.error.RoleErrorCode;

@Component
public class RoleValidator {

  private static final int MAX_NAME_LENGTH = 150;
  private static final int MAX_DESCRIPTION_LENGTH = 500;
  private static final String ROLE_NAME_PATTERN = "^[A-Z][A-Z0-9_]*$";

  public String normalizeName(String name) {
    if (!StringUtils.hasText(name)) {
      throw new IllegalArgumentException("Role name must not be blank");
    }
    String normalized = name.trim().replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
    if (normalized.length() > MAX_NAME_LENGTH || !normalized.matches(ROLE_NAME_PATTERN)) {
      throw new RoleBusinessException(RoleErrorCode.INVALID_ROLE_NAME_FORMAT);
    }
    return normalized;
  }

  public String normalizeDescription(String description) {
    if (description == null) {
      return null;
    }
    String normalized = description.trim();
    if (normalized.length() > MAX_DESCRIPTION_LENGTH) {
      throw new RoleBusinessException(RoleErrorCode.ROLE_DESCRIPTION_TOO_LONG);
    }
    return normalized.isEmpty() ? null : normalized;
  }

  public void validateUniqueIds(List<Long> ids, RoleErrorCode duplicateErrorCode) {
    if (ids == null) {
      return;
    }
    Set<Long> uniqueIds = new LinkedHashSet<>();
    for (Long id : ids) {
      if (id == null) {
        throw new IllegalArgumentException("IDs must not contain null values");
      }
      if (!uniqueIds.add(id)) {
        throw new RoleBusinessException(duplicateErrorCode);
      }
    }
  }
}
