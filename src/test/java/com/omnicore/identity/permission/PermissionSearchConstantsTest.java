package com.omnicore.identity.permission;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PermissionSearchConstantsTest {

  @Test
  void shouldAllowValidPermissionSortFields() {
    assertTrue(PermissionSearchConstants.ALLOWED_SORT_FIELDS.contains("name"));
    assertTrue(PermissionSearchConstants.ALLOWED_SORT_FIELDS.contains("createdAt"));
  }

  @Test
  void shouldRejectInvalidPermissionSortField() {
    assertFalse(PermissionSearchConstants.ALLOWED_SORT_FIELDS.contains("password"));
  }
}
