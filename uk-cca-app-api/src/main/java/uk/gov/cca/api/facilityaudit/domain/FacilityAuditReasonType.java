package uk.gov.cca.api.facilityaudit.domain;

import lombok.Getter;

@Getter
public enum FacilityAuditReasonType {

    ELIGIBILITY("Eligibility"),
    SEVENTY_RULE_EVALUATION("70% evaluation"),
    BASE_YEAR_DATA("Base year data"),
    REPORTING_DATA("Reporting data"),
    NON_COMPLIANCE("Non-compliance"),
    OTHER("Other");

    private final String description;

    FacilityAuditReasonType(String description) {
        this.description = description;
    }
}
