package com.omnicore.identity.role;

import java.util.Set;

import org.springframework.data.domain.Sort;

public final class RoleSearchConstants {

  public static final String DEFAULT_SORT_BY = "name";
  public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;
  public static final String DEFAULT_SORT_DIRECTION_VALUE = "ASC";

  public static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("id", "name", "active", "system", "createdAt", "updatedAt");

  private RoleSearchConstants() {}
}
