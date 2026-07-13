package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceDataFacilityProcessingRequestPayload extends CcaRequestPayload {

    private String parentRequestId;

    private SectorAssociationInfo sectorAssociationInfo;

    private TargetPeriodType targetPeriodType;

    private PerformanceDataReportType reportType;

    private PerformanceDataSubmissionType submissionType;

    private LocalDateTime submissionDate;

    private TargetPeriodYear targetPeriodYear;

    private List<TargetPeriodDetailsDTO> targetPeriods;

    private FacilityDTO facility;

    private PerformanceDataFacilityBaselineAndTargets baselineAndTargets;
}
