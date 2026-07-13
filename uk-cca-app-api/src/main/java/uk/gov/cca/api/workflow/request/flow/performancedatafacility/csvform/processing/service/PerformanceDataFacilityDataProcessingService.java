package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.util.PerformanceDataFacilityUtil;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataProcessingService {

    private final RequestService requestService;
    private final TargetPeriodService targetPeriodService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Transactional
    public void setAdditionalRequestPayloadData(final String requestId, final Map<Long, FacilityUploadReport> facilityReports){
        final Request request = requestService.findRequestById(requestId);
        PerformanceDataFacilityDataProcessingRequestPayload requestPayload =
                (PerformanceDataFacilityDataProcessingRequestPayload) request.getPayload();
        final LocalDateTime submissionDate = requestPayload.getSubmissionDate();

        // Get target periods data
        final List<TargetPeriodDetailsDTO> targetPeriods = targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(
                Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
        final TargetPeriodDetailsDTO targetPeriod = targetPeriods.stream()
                .filter(tp -> tp.getBusinessId().equals(requestPayload.getTargetPeriodType()))
                .findFirst().orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        final TargetPeriodYear targetPeriodYear = PerformanceDataFacilityUtil
                .getTargetPeriodYearBySubmissionDate(targetPeriod, submissionDate.toLocalDate())
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));

        // Find submission type
        PerformanceDataSubmissionType submissionType = PerformanceDataFacilityUtil
                .getSubmissionTypeBySubmissionDate(targetPeriod, requestPayload.getReportType(), submissionDate.toLocalDate())
                .orElse(null);

        // Get UNAs data
        Set<Long> accountIds = facilityReports.values().stream()
                .map(FacilityUploadReport::getAccountId).collect(Collectors.toSet());
        final Map<Long, UnderlyingAgreementContainer> underlyingAgreementAccountMap = underlyingAgreementQueryService
                .getUnderlyingAgreementContainersByAccounts(accountIds);

        // Set to payload
        requestPayload.setSubmissionType(submissionType);
        requestPayload.setTargetPeriodYear(targetPeriodYear);
        requestPayload.setTargetPeriods(targetPeriods);
        requestPayload.setUnderlyingAgreementAccountMap(underlyingAgreementAccountMap);
    }
}
