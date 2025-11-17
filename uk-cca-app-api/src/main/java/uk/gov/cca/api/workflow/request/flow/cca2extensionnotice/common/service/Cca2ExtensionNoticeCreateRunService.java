package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.validation.Cca2ExtensionNoticeViolation;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeCreateRunService {

    private final Cca2ExtensionNoticeCreateValidator cca2ExtensionNoticeCreateValidator;
    private final Cca2ExtensionNoticeAccountProcessingCreateValidator cca2ExtensionNoticeAccountProcessingCreateValidator;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;
    private final StartProcessRequestService startProcessRequestService;

    public void createRun(Set<String> providedAccounts) {
        final RequestCreateValidationResult validationResult = cca2ExtensionNoticeCreateValidator
                .validateAction(CompetentAuthorityEnum.ENGLAND, RequestCreateActionEmptyPayload.builder().build());

        if(!validationResult.isValid()) {
            throw new BusinessException(CcaErrorCode.CCA2_EXTENSION_NOTICE_RUN_EXIST);
        }

        List<TargetUnitAccountBusinessInfoDTO> activeAccounts = providedAccounts.isEmpty()
                ? targetUnitAccountQueryService.getActiveAccounts()
                : targetUnitAccountQueryService.getActiveAccountsByBusinessIds(providedAccounts);

        Map<Long, Cca2ExtensionNoticeAccountState> accountStates = activeAccounts.stream()
                .collect(Collectors.toMap(
                        TargetUnitAccountBusinessInfoDTO::getAccountId,
                        this::transformToAccountState));

        startRun(accountStates);
    }

    private void startRun(Map<Long, Cca2ExtensionNoticeAccountState> accountStates) {
        // Filter out accounts with validation error
        Set<Long> accountIds = accountStates.entrySet().stream()
                .filter(state -> state.getValue().getErrors().isEmpty())
                .map(Map.Entry::getKey).collect(Collectors.toSet());

        // Add a default signatory for document generation
        String defaultSignatory = regulatorAuthorityResourceService.findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND)
                .getFirst();

        // Create process
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.CCA2_EXTENSION_NOTICE_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestPayload(Cca2ExtensionNoticeRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA2_EXTENSION_NOTICE_RUN_REQUEST_PAYLOAD)
                        .defaultSignatory(defaultSignatory)
                        .accountStates(accountStates)
                        .build())
                .requestMetadata(Cca2ExtensionNoticeRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_EXTENSION_NOTICE_RUN)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(accountIds),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        // Start process
        startProcessRequestService.startProcess(requestParams);
    }

    private Cca2ExtensionNoticeAccountState transformToAccountState(TargetUnitAccountBusinessInfoDTO account) {
        // Validate account
        List<String> errors = new ArrayList<>();
        RequestCreateValidationResult result = cca2ExtensionNoticeAccountProcessingCreateValidator
                .validateAction(account.getAccountId());
        if(!result.isValid()) {
            errors.add(Cca2ExtensionNoticeViolation.Cca2ExtensionNoticeViolationMessage.ACCOUNT_NOT_ELIGIBLE.getMessage() +
                    ": " + result.getReportedRequestTypes());
        }

        return Cca2ExtensionNoticeAccountState.builder()
                .accountId(account.getAccountId())
                .accountBusinessId(account.getBusinessId())
                .errors(errors)
                .build();
    }
}
