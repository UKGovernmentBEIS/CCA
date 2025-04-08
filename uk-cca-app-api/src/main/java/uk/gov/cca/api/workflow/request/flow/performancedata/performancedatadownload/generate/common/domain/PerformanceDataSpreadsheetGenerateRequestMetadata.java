package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceDataSpreadsheetGenerateRequestMetadata extends RequestMetadata {
    private String parentRequestId;
    private String accountBusinessId;
    private TargetPeriodDocumentTemplate targetPeriodDocument;
    private FileDTO template;
    private SectorAssociationInfo sectorAssociationInfo;
    private PerformanceDataTargetPeriodType targetPeriodType;
    private UnderlyingAgreementDTO underlyingAgreement;
    private TargetUnitAccountDetailsDTO targetUnitAccountDetails;
    private PerformanceDataSubmissionType submissionType;
    private Integer reportVersion;
    private PerformanceDataContainer lastUploadedReport;
}
