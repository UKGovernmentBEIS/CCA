package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataInfo;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationRunInitiateServiceTest {

    @InjectMocks
    private FacilityCertificationRunInitiateService facilityCertificationRunInitiateService;

    @Mock
    private FacilityCertificationCreateValidator facilityCertificationCreateValidator;

    @Mock
    private CertificationPeriodService certificationPeriodService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Test
    void isValidForFacilityCertificationRun() {
        final List<CertificationPeriodDTO> certifications = List.of(
                CertificationPeriodDTO.builder().certificationBatchTriggerDate(LocalDate.of(2023, 7, 1)).build(),
                CertificationPeriodDTO.builder().certificationBatchTriggerDate(LocalDate.of(2024, 7, 1)).build()
        );

        when(certificationPeriodService.getAllCertificationPeriods()).thenReturn(certifications);

        // Invoke
        boolean result = facilityCertificationRunInitiateService.isValidForFacilityCertificationRun();

        // Verify
        assertThat(result).isFalse();
        verify(certificationPeriodService, times(1)).getAllCertificationPeriods();
    }

    @Test
    void createFacilityCertificationRun_manually() {
        final List<String> providedAccountIds = List.of("1", "2");
        final CertificationPeriodType certificationPeriodType = CertificationPeriodType.CP7;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final RequestCreateActionEmptyPayload requestCreateActionEmptyPayload = RequestCreateActionEmptyPayload.builder().build();
        final Set<Long> accountIds = Set.of(1L, 2L);
        final List<AccountPerformanceDataInfo> accounts = List.of(
                AccountPerformanceDataInfo.builder().accountId(1L).accountBusinessId("accountId1").lastPerformanceDataId(11L).build(),
                AccountPerformanceDataInfo.builder().accountId(2L).accountBusinessId("accountId2").lastPerformanceDataId(22L).build()
        );
        final Map<Long, FacilityCertificationAccountState> accountStates = Map.of(
                1L, FacilityCertificationAccountState.builder().accountId(1L).accountBusinessId("accountId1").lastPerformanceDataId(11L).build(),
                2L, FacilityCertificationAccountState.builder().accountId(2L).accountBusinessId("accountId2").lastPerformanceDataId(22L).build()
        );
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .certificationPeriodType(certificationPeriodType)
                .targetPeriodType(targetPeriodType)
                .build();
        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_CERTIFICATION_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestPayload(FacilityCertificationRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_CERTIFICATION_RUN_REQUEST_PAYLOAD)
                        .certificationPeriodDetails(certificationPeriod)
                        .facilityCertificationAccountStates(accountStates)
                        .build())
                .requestMetadata(FacilityCertificationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.FACILITY_CERTIFICATION_RUN)
                        .certificationPeriodType(certificationPeriodType)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, accountIds,
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(facilityCertificationCreateValidator.validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(accountPerformanceDataStatusQueryService.findAccountsWithPerformanceDataForTargetPeriod(targetPeriodType, accountIds))
                .thenReturn(accounts);
        when(certificationPeriodService.getCertificationPeriodByType(certificationPeriodType))
                .thenReturn(certificationPeriod);

        // Invoke
        facilityCertificationRunInitiateService.createFacilityCertificationRun(providedAccountIds, certificationPeriodType);

        // Verify
        verify(facilityCertificationCreateValidator, times(1))
                .validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .findAccountsWithPerformanceDataForTargetPeriod(targetPeriodType, accountIds);
        verify(certificationPeriodService, times(1)).getCertificationPeriodByType(certificationPeriodType);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void createFacilityCertificationRun_automatically() {
        final CertificationPeriodType certificationPeriodType = CertificationPeriodType.CP7;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final RequestCreateActionEmptyPayload requestCreateActionEmptyPayload = RequestCreateActionEmptyPayload.builder().build();
        final List<AccountPerformanceDataInfo> accounts = List.of(
                AccountPerformanceDataInfo.builder().accountId(1L).accountBusinessId("accountId1").lastPerformanceDataId(11L).build(),
                AccountPerformanceDataInfo.builder().accountId(2L).accountBusinessId("accountId2").lastPerformanceDataId(22L).build()
        );
        final Map<Long, FacilityCertificationAccountState> accountStates = Map.of(
                1L, FacilityCertificationAccountState.builder().accountId(1L).accountBusinessId("accountId1").lastPerformanceDataId(11L).build(),
                2L, FacilityCertificationAccountState.builder().accountId(2L).accountBusinessId("accountId2").lastPerformanceDataId(22L).build()
        );
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .certificationPeriodType(certificationPeriodType)
                .targetPeriodType(targetPeriodType)
                .build();
        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_CERTIFICATION_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestPayload(FacilityCertificationRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_CERTIFICATION_RUN_REQUEST_PAYLOAD)
                        .certificationPeriodDetails(certificationPeriod)
                        .facilityCertificationAccountStates(accountStates)
                        .build())
                .requestMetadata(FacilityCertificationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.FACILITY_CERTIFICATION_RUN)
                        .certificationPeriodType(certificationPeriodType)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, Set.of(1L, 2L),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(facilityCertificationCreateValidator.validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(accountPerformanceDataStatusQueryService.findAccountsWithPerformanceDataForTargetPeriod(targetPeriodType))
                .thenReturn(accounts);
        when(certificationPeriodService.getCertificationPeriodByTriggerDate(any()))
                .thenReturn(certificationPeriod);

        // Invoke
        facilityCertificationRunInitiateService.createFacilityCertificationRun();

        // Verify
        verify(facilityCertificationCreateValidator, times(1))
                .validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload);
        verify(accountPerformanceDataStatusQueryService, times(1)).findAccountsWithPerformanceDataForTargetPeriod(targetPeriodType);
        verify(certificationPeriodService, times(1)).getCertificationPeriodByTriggerDate(any());
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void createFacilityCertificationRun_in_progress() {
        final RequestCreateActionEmptyPayload requestCreateActionEmptyPayload = RequestCreateActionEmptyPayload.builder().build();

        when(facilityCertificationCreateValidator.validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> facilityCertificationRunInitiateService.createFacilityCertificationRun());

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.FACILITY_CERTIFICATION_RUN_EXIST);
        verify(facilityCertificationCreateValidator, times(1))
                .validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload);
        verifyNoInteractions(accountPerformanceDataStatusQueryService, certificationPeriodService, startProcessRequestService);
    }
}
