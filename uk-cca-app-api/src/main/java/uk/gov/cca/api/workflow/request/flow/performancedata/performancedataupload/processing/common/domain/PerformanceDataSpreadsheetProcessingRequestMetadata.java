package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataSpreadsheetProcessingRequestMetadata extends RequestMetadata {
    private String parentRequestId;
    private String accountBusinessId;
    private SectorAssociationInfo sectorAssociationInfo;
    private PerformanceDataTargetPeriodType performanceDataTargetPeriodType;
    private TargetPeriodDTO targetPeriodDetails;
    private int reportVersion;
    private PerformanceDataSubmissionType submissionType;
    private LocalDate uploadedDate;
}
