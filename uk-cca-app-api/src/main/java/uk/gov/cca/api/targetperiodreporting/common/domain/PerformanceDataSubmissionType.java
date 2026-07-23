package uk.gov.cca.api.targetperiodreporting.common.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerformanceDataSubmissionType {
    PRIMARY("Primary"), SECONDARY("Secondary");

    private final String description;
}