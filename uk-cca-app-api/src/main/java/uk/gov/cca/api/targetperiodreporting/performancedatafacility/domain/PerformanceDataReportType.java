package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PerformanceDataReportType {

    INTERIM("Interim"),
    FINAL("Final");

    private final String description;
}
