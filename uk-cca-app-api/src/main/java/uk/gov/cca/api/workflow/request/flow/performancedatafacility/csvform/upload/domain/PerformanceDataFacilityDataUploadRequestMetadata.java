package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityDataUploadRequestMetadata extends RequestMetadata {

    private TargetPeriodType targetPeriodType;

    private PerformanceDataReportType reportType;
    
    private TargetPeriodYear targetPeriodYear;

    private List<TargetPeriodDetailsDTO> targetPeriods;
    
    private PerformanceDataSubmissionType submissionType;

    private LocalDateTime submittedDate;
}
