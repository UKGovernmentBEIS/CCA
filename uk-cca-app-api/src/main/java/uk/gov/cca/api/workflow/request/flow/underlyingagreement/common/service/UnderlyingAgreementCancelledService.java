package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Objects;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementCancelledService {

    private final RequestService requestService;
    private final TargetUnitAccountUpdateService targetUnitAccountUpdateService;

    public void cancel(final String requestId, final String assigneeRoleType) {
        final Request request = requestService.findRequestById(requestId);

        final UnderlyingAgreementRequestPayload payload = (UnderlyingAgreementRequestPayload) request.getPayload();

        final String assignee = Objects.equals(assigneeRoleType, SECTOR_USER)
                ? payload.getSectorUserAssignee()
                : payload.getRegulatorAssignee();

        final Long accountId = request.getAccountId();
        targetUnitAccountUpdateService.handleTargetUnitAccountCancelled(accountId);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_CANCELLED,
                assignee);
    }
}
