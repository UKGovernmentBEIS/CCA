package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataFacilityDataUploadRequestMetadata extends RequestMetadata {

    private TargetPeriodType targetPeriodType;

    private PerformanceDataReportType reportType;

    private LocalDateTime submittedDate;
}
