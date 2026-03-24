package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{{'TP7','TP8','TP9'}.contains(#targetPeriodType)}")
public class PerformanceDataFacilityDigitalFormRequestCreateActionPayload extends RequestCreateActionPayload {

    @NotNull
    private TargetPeriodType targetPeriodType;

    @NotNull
    private PerformanceDataReportType reportType;
}
