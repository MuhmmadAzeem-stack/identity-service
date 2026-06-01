package com.omnicore.identity.permission;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PermissionValidatorTest {

  private final PermissionValidator validator = new PermissionValidator();

  @Test
  void shouldRejectPermissionNameWithSpacesBeforeNormalization() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validatePermissionNameFormat("LIST USER", "LIST", "USER"));

    assertEquals("Permission name must not contain spaces", exception.getMessage());
  }

  @Test
  void shouldRejectActionWithSpacesBeforeNormalization() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validatePermissionNameFormat("LIST_USER", "LI ST", "USER"));

    assertEquals("Permission action must not contain spaces", exception.getMessage());
  }

  @Test
  void shouldRejectModuleWithSpacesBeforeNormalization() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validatePermissionNameFormat("LIST_USER", "LIST", "US ER"));

    assertEquals("Permission module must not contain spaces", exception.getMessage());
  }

  @Test
  void shouldAcceptValidPermissionNameFormat() {
    assertDoesNotThrow(() -> validator.validatePermissionNameFormat("LIST_USER", "LIST", "USER"));
  }
}
