package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityReferenceData;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityInputData;

import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityDigitalFormRequestPayload extends CcaRequestPayload {

    private TargetPeriodType targetPeriodType;

    private PerformanceDataReportType reportType;

    private Year targetPeriodYear;

    private FacilityBaseInfoDTO facility;

    private Integer reportVersion;

    private PerformanceDataSubmissionType submissionType;

    private PerformanceDataFacilityReferenceData referenceData;

    private PerformanceDataFacilityInputData performanceData;
}
