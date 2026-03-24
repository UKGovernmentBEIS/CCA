package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDataProcessingRequestPayload extends CcaRequestPayload {

    private PerformanceDataTargetPeriodType performanceDataTargetPeriodType;

    private SectorAssociationInfo sectorAssociationInfo;

    private TargetPeriodYearDTO targetPeriodDetails;

    private PerformanceDataSubmissionType submissionType;

    @Builder.Default
    private Map<Long, TargetUnitAccountUploadReport> accountReports = new HashMap<>();

    private LocalDate uploadedDate;
}
