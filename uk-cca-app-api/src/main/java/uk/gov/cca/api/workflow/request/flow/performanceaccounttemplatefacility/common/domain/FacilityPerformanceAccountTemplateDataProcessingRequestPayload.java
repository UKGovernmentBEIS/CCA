package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacilityPerformanceAccountTemplateDataProcessingRequestPayload extends CcaRequestPayload {

    private String parentRequestId;

    private SectorAssociationInfo sectorAssociationInfo;

    private TargetPeriodType targetPeriodType;

    private PerformanceDataReportType reportType;

    private PerformanceDataSubmissionType submissionType;

    private LocalDateTime submissionDate;

    private Year targetYear;

    @Builder.Default
    private Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = new HashMap<>();
}
