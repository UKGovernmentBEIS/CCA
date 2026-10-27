package uk.gov.cca.api.common.utils;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringSafetyUtils {

    private static final int MAX_ERROR_LENGTH = 1000;
    private static final String UNKNOWN_ERROR_MESSAGE = "An unexpected error occurred";

    public String safeErrorMessage(Exception e) {
        return e.getMessage() != null ? StringUtils.truncate(e.getMessage(), MAX_ERROR_LENGTH)
                : UNKNOWN_ERROR_MESSAGE;
    }

}
