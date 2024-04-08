package uk.gov.cca.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerates the various decision outcomes
 */
@Getter
@AllArgsConstructor
public enum Decision {

    APPROVED("Approved"),
    REJECTED("Rejected")
    ;

    /**
     * The name.
     */
    private final String name;
}
