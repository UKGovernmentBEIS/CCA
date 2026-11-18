package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataInfo;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.validation.FacilityCertificationCreateValidator;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class FacilityCertificationRunInitiateService {

    private final FacilityCertificationCreateValidator facilityCertificationCreateValidator;
    private final CertificationPeriodService certificationPeriodService;
    private final StartProcessRequestService startProcessRequestService;
    private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    public boolean isValidForFacilityCertificationRun() {
        LocalDate currentDate = LocalDate.now();
        Set<LocalDate> certificationBatchTriggerDates = certificationPeriodService.getAllCertificationPeriods().stream()
                .map(CertificationPeriodDTO::getCertificationBatchTriggerDate).collect(Collectors.toSet());

        return certificationBatchTriggerDates.contains(currentDate);
    }

    public void createFacilityCertificationRun(@NotEmpty List<String> providedAccountIds, @NotNull CertificationPeriodType type) {
        // Validate
        validateCreation();

        Set<Long> accountIds = providedAccountIds.stream().map(Long::parseLong).collect(Collectors.toSet());
        CertificationPeriodDTO certificationPeriodDetails = certificationPeriodService.getCertificationPeriodByType(type);

        Map<Long, FacilityCertificationAccountState> facilityCertificationAccountStates = accountPerformanceDataStatusQueryService
                .findAccountsWithPerformanceDataForTargetPeriod(certificationPeriodDetails.getTargetPeriodType(), accountIds).stream()
                .collect(Collectors.toMap(
                        AccountPerformanceDataInfo::getAccountId,
                        acc -> FacilityCertificationAccountState.builder()
                                .accountId(acc.getAccountId())
                                .accountBusinessId(acc.getAccountBusinessId())
                                .lastPerformanceDataId(acc.getLastPerformanceDataId())
                                .build()
                ));

        // Validate if accounts exists
        Set<Long> diffs = SetUtils.disjunction(accountIds, facilityCertificationAccountStates.keySet());
        if(!diffs.isEmpty()) {
            throw new BusinessException(CcaErrorCode.INVALID_PROVIDED_ACCOUNTS, diffs);
        }

        startFacilityCertificationRun(facilityCertificationAccountStates, certificationPeriodDetails);
    }

    public void createFacilityCertificationRun() {
        // Validate
        validateCreation();

        CertificationPeriodDTO certificationPeriodDetails = certificationPeriodService.getCertificationPeriodByTriggerDate(LocalDate.now());

        Map<Long, FacilityCertificationAccountState> facilityCertificationAccountStates = accountPerformanceDataStatusQueryService
                .findAccountsWithPerformanceDataForTargetPeriod(certificationPeriodDetails.getTargetPeriodType()).stream()
                .collect(Collectors.toMap(
                        AccountPerformanceDataInfo::getAccountId,
                        acc -> FacilityCertificationAccountState.builder()
                                .accountId(acc.getAccountId())
                                .accountBusinessId(acc.getAccountBusinessId())
                                .lastPerformanceDataId(acc.getLastPerformanceDataId())
                                .build()
                ));

        startFacilityCertificationRun(facilityCertificationAccountStates, certificationPeriodDetails);
    }

    private void validateCreation() {
        final RequestCreateValidationResult validationResult = facilityCertificationCreateValidator
                .validateAction(CompetentAuthorityEnum.ENGLAND, RequestCreateActionEmptyPayload.builder().build());

        if(!validationResult.isValid()) {
            throw new BusinessException(CcaErrorCode.FACILITY_CERTIFICATION_RUN_EXIST);
        }
    }

    private void startFacilityCertificationRun(Map<Long, FacilityCertificationAccountState> facilityCertificationAccountStates,
                                               CertificationPeriodDTO certificationPeriodDetails) {
        // Create process
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_CERTIFICATION_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestPayload(FacilityCertificationRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_CERTIFICATION_RUN_REQUEST_PAYLOAD)
                        .certificationPeriodDetails(certificationPeriodDetails)
                        .facilityCertificationAccountStates(facilityCertificationAccountStates)
                        .build())
                .requestMetadata(FacilityCertificationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.FACILITY_CERTIFICATION_RUN)
                        .certificationPeriodType(certificationPeriodDetails.getCertificationPeriodType())
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(facilityCertificationAccountStates.keySet()),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        // Start process
        startProcessRequestService.startProcess(requestParams);
    }
}
