package uk.gov.cca.api.workflow.request.flow.rfi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.cca.api.workflow.request.core.service.RequestService;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestPayload;

@Service
@RequiredArgsConstructor
public class RfiCancelledService {

    private final RequestService requestService;

    public void cancel(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final RequestPayload requestPayload = request.getPayload();
        final String regulatorAssignee = requestPayload.getRegulatorAssignee();

        requestService.addActionToRequest(request,
            null,
            RequestActionType.RFI_CANCELLED,
            regulatorAssignee);
    }
}
