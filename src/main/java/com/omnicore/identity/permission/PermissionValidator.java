package com.omnicore.identity.permission;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PermissionValidator {

    private static final int DESCRIPTION_MAX_LENGTH = 500;

    private static final String NAME_BLANK = "Permission name must not be blank";
    private static final String ACTION_BLANK = "Permission action must not be blank";
    private static final String MODULE_BLANK = "Permission module must not be blank";
    private static final String NAME_FORMAT_MISMATCH_PREFIX =
        "Permission name must match format ACTION_MODULE. Expected: ";
    private static final String NAME_CONTAINS_SPACES = "Permission name must not contain spaces";
    private static final String ACTION_CONTAINS_SPACES = "Permission action must not contain spaces";
    private static final String MODULE_CONTAINS_SPACES = "Permission module must not contain spaces";
    private static final String DESCRIPTION_TOO_LONG_PREFIX = "Permission description must not exceed ";
    private static final String DESCRIPTION_TOO_LONG_SUFFIX = " characters";

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
            throw new IllegalArgumentException(NAME_BLANK);
        }
        if (!StringUtils.hasText(action)) {
            throw new IllegalArgumentException(ACTION_BLANK);
        }
        if (!StringUtils.hasText(module)) {
            throw new IllegalArgumentException(MODULE_BLANK);
        }

        validateNoWhitespace(name, NAME_CONTAINS_SPACES);
        validateNoWhitespace(action, ACTION_CONTAINS_SPACES);
        validateNoWhitespace(module, MODULE_CONTAINS_SPACES);

        String normalizedName = normalizeName(name);
        String normalizedAction = normalizeAction(action);
        String normalizedModule = normalizeModule(module);
        String expectedName = normalizedAction + "_" + normalizedModule;

        if (!normalizedName.equals(expectedName)) {
            throw new IllegalArgumentException(NAME_FORMAT_MISMATCH_PREFIX + expectedName);
        }
    }

    public void validateDescriptionLength(String description) {
        if (description != null && description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException(
                DESCRIPTION_TOO_LONG_PREFIX + DESCRIPTION_MAX_LENGTH + DESCRIPTION_TOO_LONG_SUFFIX);
        }
    }

    private void validateNoWhitespace(String value, String message) {
        if (value.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException(message);
        }
    }

    private String normalizeToken(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().replace(' ', '_').toUpperCase();
    }
}
