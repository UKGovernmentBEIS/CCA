package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service.TargetUnitAccountCreationService;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.CcaRequestCreateActionHandler;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationSubmitApplicationCreateActionPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountPayload;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.transform.TargetUnitAccountPayloadMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType.TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestType.TARGET_UNIT_ACCOUNT_CREATION;

@Component
@RequiredArgsConstructor
public class TargetUnitAccountCreationSubmitApplicationCreateActionHandler
        implements CcaRequestCreateActionHandler<TargetUnitAccountCreationSubmitApplicationCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final TargetUnitAccountPayloadMapper targetUnitAccountPayloadMapper;
    private final TargetUnitAccountCreationService targetUnitAccountCreationService;
    private final RequestService requestService;

    @Override
    public String process(final Long sectorAssociationId,
                          final Long accountId,
                          final String type,
                          final TargetUnitAccountCreationSubmitApplicationCreateActionPayload createActionPayload,
                          final AppUser appUser) {

        final TargetUnitAccountPayload targetUnitAccountPayload = createActionPayload.getPayload();
        TargetUnitAccountDTO accountDTO = targetUnitAccountPayloadMapper
                .toTargetUnitAccountDTO(targetUnitAccountPayload, sectorAssociationId, appUser.getUserId());

        // Create account
        accountDTO = targetUnitAccountCreationService.createAccount(accountDTO);

        // Create request and start flow
        Request request = startProcessRequestService.startProcess(
                RequestParams.builder()
                        .type(TARGET_UNIT_ACCOUNT_CREATION)
                        .accountId(accountDTO.getId())
                        .requestPayload(targetUnitAccountPayloadMapper.toTargetUnitAccountCreationRequestPayload(
                                targetUnitAccountPayload, accountDTO.getBusinessId(), sectorAssociationId))
                        .processVars(Map.of(BpmnProcessConstants.ACCOUNT_ID, accountDTO.getId()))
                        .build()
        );

        // Set request's submission date
        request.setSubmissionDate(request.getCreationDate());

        // Create request action
        requestService.addActionToRequest(
                request,
                targetUnitAccountPayloadMapper.toTargetUnitAccountCreationSubmittedRequestActionPayload(
                        targetUnitAccountPayload, accountDTO.getBusinessId()),
                TARGET_UNIT_ACCOUNT_CREATION_SUBMITTED,
                appUser.getUserId());

        return request.getId();
    }

    @Override
    public String getRequestType() {
        return TARGET_UNIT_ACCOUNT_CREATION;
    }
}
