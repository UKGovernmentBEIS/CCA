package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload extends RequestTaskPayload {

    private TargetPeriodType targetPeriodType;

    private PerformanceDataReportType reportType;

    private Year targetPeriodYear;

    private FacilityDTO facility;

    private Cca3FacilityBaselineAndTargets originalBaselineData;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}
