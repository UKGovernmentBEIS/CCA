package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataReferenceDetails {

    private String sectorAcronym;
    private TargetUnitAccountDetailsDTO accountDetails;
    private UnderlyingAgreementDTO underlyingAgreement;
    private TargetPeriodDTO targetPeriodDetails;
    private PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics;
    private PerformanceDataSubmissionType submissionType;
    private String fileName;
    private int reportVersion;
    private LocalDate uploadedDate;
    private PerformanceDataContainer lastUploadedReport;
}
