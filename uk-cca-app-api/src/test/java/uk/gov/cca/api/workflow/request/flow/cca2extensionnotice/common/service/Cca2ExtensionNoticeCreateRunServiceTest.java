package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.validation.Cca2ExtensionNoticeAccountProcessingCreateValidator;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.validation.Cca2ExtensionNoticeCreateValidator;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeCreateRunServiceTest {

    @InjectMocks
    private Cca2ExtensionNoticeCreateRunService cca2ExtensionNoticeCreateRunService;

    @Mock
    private Cca2ExtensionNoticeCreateValidator cca2ExtensionNoticeCreateValidator;

    @Mock
    private Cca2ExtensionNoticeAccountProcessingCreateValidator cca2ExtensionNoticeAccountProcessingCreateValidator;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createRun() {
        final Set<String> providedAccounts = Set.of();
        final RequestCreateActionEmptyPayload requestCreateActionEmptyPayload = RequestCreateActionEmptyPayload.builder().build();

        final Map<Long, Cca2ExtensionNoticeAccountState> accountStates = Map.of(
                1L, Cca2ExtensionNoticeAccountState.builder()
                        .accountId(1L)
                        .accountBusinessId("account1")
                        .build(),
                2L, Cca2ExtensionNoticeAccountState.builder()
                        .accountId(2L)
                        .accountBusinessId("account2")
                        .errors(List.of("Target unit account not eligible for cca2 extension notice: [request]")).build()
        );
        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.CCA2_EXTENSION_NOTICE_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestPayload(Cca2ExtensionNoticeRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA2_EXTENSION_NOTICE_RUN_REQUEST_PAYLOAD)
                        .defaultSignatory("regulator1")
                        .accountStates(accountStates)
                        .build())
                .requestMetadata(Cca2ExtensionNoticeRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_EXTENSION_NOTICE_RUN)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, Set.of(1L),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(cca2ExtensionNoticeCreateValidator.validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(targetUnitAccountQueryService.getActiveAccounts())
                .thenReturn(List.of(
                        TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("account1").build(),
                        TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("account2").build()
                ));
        when(cca2ExtensionNoticeAccountProcessingCreateValidator.validateAction(1L))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(cca2ExtensionNoticeAccountProcessingCreateValidator.validateAction(2L))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).reportedRequestTypes(Set.of("request")).build());
        when(regulatorAuthorityResourceService.findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND))
                .thenReturn(List.of("regulator1", "regulator2"));

        // Invoke
        cca2ExtensionNoticeCreateRunService.createRun(providedAccounts);

        // Verify
        verify(cca2ExtensionNoticeCreateValidator, times(1))
                .validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload);
        verify(targetUnitAccountQueryService, times(1)).getActiveAccounts();
        verify(cca2ExtensionNoticeAccountProcessingCreateValidator, times(2)).validateAction(anyLong());
        verify(regulatorAuthorityResourceService, times(1))
                .findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verifyNoMoreInteractions(targetUnitAccountQueryService);
    }

    @Test
    void createRun_with_account_ids() {
        final Set<String> providedAccounts = Set.of("account1", "account2");
        final RequestCreateActionEmptyPayload requestCreateActionEmptyPayload = RequestCreateActionEmptyPayload.builder().build();

        final Map<Long, Cca2ExtensionNoticeAccountState> accountStates = Map.of(
                1L, Cca2ExtensionNoticeAccountState.builder().accountId(1L).accountBusinessId("account1").build(),
                2L, Cca2ExtensionNoticeAccountState.builder().accountId(2L).accountBusinessId("account2").build()
        );
        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.CCA2_EXTENSION_NOTICE_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestPayload(Cca2ExtensionNoticeRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA2_EXTENSION_NOTICE_RUN_REQUEST_PAYLOAD)
                        .defaultSignatory("regulator1")
                        .accountStates(accountStates)
                        .build())
                .requestMetadata(Cca2ExtensionNoticeRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_EXTENSION_NOTICE_RUN)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, Set.of(1L, 2L),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        when(cca2ExtensionNoticeCreateValidator.validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(cca2ExtensionNoticeAccountProcessingCreateValidator.validateAction(1L))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(cca2ExtensionNoticeAccountProcessingCreateValidator.validateAction(2L))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(targetUnitAccountQueryService.getActiveAccountsByBusinessIds(providedAccounts))
                .thenReturn(List.of(
                        TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("account1").build(),
                        TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("account2").build()
                ));
        when(regulatorAuthorityResourceService.findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND))
                .thenReturn(List.of("regulator1", "regulator2"));

        // Invoke
        cca2ExtensionNoticeCreateRunService.createRun(providedAccounts);

        // Verify
        verify(cca2ExtensionNoticeCreateValidator, times(1))
                .validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload);
        verify(targetUnitAccountQueryService, times(1)).getActiveAccountsByBusinessIds(providedAccounts);
        verify(cca2ExtensionNoticeAccountProcessingCreateValidator, times(2)).validateAction(anyLong());
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(regulatorAuthorityResourceService, times(1))
                .findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND);
        verifyNoMoreInteractions(targetUnitAccountQueryService);
    }

    @Test
    void createRun_in_progress() {
        final RequestCreateActionEmptyPayload requestCreateActionEmptyPayload = RequestCreateActionEmptyPayload.builder().build();

        when(cca2ExtensionNoticeCreateValidator.validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> cca2ExtensionNoticeCreateRunService.createRun(Set.of()));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.CCA2_EXTENSION_NOTICE_RUN_EXIST);
        verify(cca2ExtensionNoticeCreateValidator, times(1))
                .validateAction(CompetentAuthorityEnum.ENGLAND, requestCreateActionEmptyPayload);
        verifyNoInteractions(targetUnitAccountQueryService, cca2ExtensionNoticeAccountProcessingCreateValidator,
                regulatorAuthorityResourceService, startProcessRequestService);
    }
}
