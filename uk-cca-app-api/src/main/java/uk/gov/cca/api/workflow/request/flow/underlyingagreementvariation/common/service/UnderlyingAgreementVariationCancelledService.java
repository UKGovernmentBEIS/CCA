package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Objects;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCancelledService {

    private final RequestService requestService;

    public void cancel(final String requestId, final String assigneeRoleType) {
        final Request request = requestService.findRequestById(requestId);

        final UnderlyingAgreementVariationRequestPayload payload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        final String assignee = Objects.equals(assigneeRoleType, SECTOR_USER)
                ? payload.getSectorUserAssignee()
                : payload.getRegulatorAssignee();

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_CANCELLED,
                assignee);
    }
}
