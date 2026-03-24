package uk.gov.cca.api.workflow.request.flow.performancedata.common.domain;

import java.time.Year;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Getter
@AllArgsConstructor
public enum PerformanceDataTargetPeriodType {
    TP6(TargetPeriodType.TP6, Year.of(2024));

    private final TargetPeriodType referenceTargetPeriod;
    private final Year targetYear;
    
    public static PerformanceDataTargetPeriodType fromTargetPeriodType(String targetPeriodType) {
        return Arrays.stream(PerformanceDataTargetPeriodType.values())
                .filter(type -> type.referenceTargetPeriod.name().equalsIgnoreCase(targetPeriodType))
                .findFirst()
                .orElse(null);
    }
}