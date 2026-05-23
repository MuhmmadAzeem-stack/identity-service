package com.omnicore.identity.rbac;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorityNamesTest {

    @Test
    void shouldBuildAuthorityNameFromActionAndModule() {
        assertEquals(
            "LIST_USER",
            AuthorityNames.authority(RbacAction.LIST, RbacModule.USER)
        );
    }

    @Test
    void listUserAuthorityShouldMatchGeneratedValue() {
        assertEquals(
            AuthorityNames.authority(RbacAction.LIST, RbacModule.USER),
            AuthorityNames.LIST_USER
        );
    }

    @Test
    void resetUserPasswordShouldUseLegacyUserNaming() {
        assertEquals("RESET_USER_PASSWORD", AuthorityNames.userAuthority(RbacAction.RESET_PASSWORD));
    }

    @Test
    void viewUserPermissionShouldUseLegacyUserNaming() {
        assertEquals("VIEW_USER_PERMISSION", AuthorityNames.userAuthority(RbacAction.VIEW_PERMISSION));
    }

    @Test
    void assignPermissionDependencyShouldMatchGeneratedValue() {
        assertEquals(
            AuthorityNames.authority(RbacAction.ASSIGN, RbacModule.PERMISSION_DEPENDENCY),
            AuthorityNames.ASSIGN_PERMISSION_DEPENDENCY
        );
    }
}
