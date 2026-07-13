package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{{'TP7','TP8','TP9'}.contains(#targetPeriodType)}")
public class PerformanceDataFacilityUpload {

    @NotNull
    private TargetPeriodType targetPeriodType;

    @NotNull
    private PerformanceDataReportType reportType;

    @NotEmpty
    @Builder.Default
    private Set<UUID> files = new HashSet<>();
}
