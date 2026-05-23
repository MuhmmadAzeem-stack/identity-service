package com.omnicore.identity.common.constants;

public final class PaginationDefaults {

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    /** Required for {@code @RequestParam(defaultValue = ...)} annotations. */
    public static final String DEFAULT_PAGE_NUMBER_VALUE = "0";

    /** Required for {@code @RequestParam(defaultValue = ...)} annotations. */
    public static final String DEFAULT_PAGE_SIZE_VALUE = "20";

    private PaginationDefaults() {
    }
}
