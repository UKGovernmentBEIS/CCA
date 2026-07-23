package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDataFacilityDataProcessingRequestPayload extends CcaRequestPayload {

    private String parentRequestId;

    private SectorAssociationInfo sectorAssociationInfo;

    private TargetPeriodType targetPeriodType;

    private PerformanceDataReportType reportType;

    private PerformanceDataSubmissionType submissionType;

    private LocalDateTime submissionDate;

    private TargetPeriodYear targetPeriodYear;

    private List<TargetPeriodDetailsDTO> targetPeriods;

    private Map<Long, UnderlyingAgreementContainer> underlyingAgreementAccountMap;

    @Builder.Default
    private Map<Long, FacilityUploadReport> facilityReports = new HashMap<>();
}
