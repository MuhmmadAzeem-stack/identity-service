package com.omnicore.identity.permission;

import java.util.Set;

import org.springframework.data.domain.Sort;

public final class PermissionSearchConstants {

  public static final String DEFAULT_SORT_BY = "name";
  public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;
  public static final String DEFAULT_SORT_DIRECTION_VALUE = "ASC";

  public static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("id", "name", "module", "action", "active", "system", "createdAt", "updatedAt");

  private PermissionSearchConstants() {}
}
