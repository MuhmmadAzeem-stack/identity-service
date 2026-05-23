package com.omnicore.identity.permission;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PermissionValidator {

    public String normalizeName(String name) {
        return normalizeToken(name);
    }

    public String normalizeModule(String module) {
        return normalizeToken(module);
    }

    public String normalizeAction(String action) {
        return normalizeToken(action);
    }

    public void validatePermissionNameFormat(String name, String action, String module) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException(PermissionValidationMessages.NAME_BLANK);
        }
        if (!StringUtils.hasText(action)) {
            throw new IllegalArgumentException(PermissionValidationMessages.ACTION_BLANK);
        }
        if (!StringUtils.hasText(module)) {
            throw new IllegalArgumentException(PermissionValidationMessages.MODULE_BLANK);
        }

        String normalizedName = normalizeName(name);
        String normalizedAction = normalizeAction(action);
        String normalizedModule = normalizeModule(module);
        String expectedName = normalizedAction + "_" + normalizedModule;

        if (!normalizedName.equals(expectedName)) {
            throw new IllegalArgumentException(
                PermissionValidationMessages.NAME_FORMAT_MISMATCH_PREFIX + expectedName);
        }

        if (normalizedName.contains(" ")) {
            throw new IllegalArgumentException(PermissionValidationMessages.NAME_CONTAINS_SPACES);
        }
    }

    public void validateDescriptionLength(String description) {
        if (description != null && description.length() > PermissionConstants.DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException(
                PermissionValidationMessages.DESCRIPTION_TOO_LONG_PREFIX
                    + PermissionConstants.DESCRIPTION_MAX_LENGTH
                    + PermissionValidationMessages.DESCRIPTION_TOO_LONG_SUFFIX);
        }
    }

    private String normalizeToken(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().replace(' ', '_').toUpperCase();
    }
}
