package uk.gov.cca.api.workflow.request.flow.rde.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.service.RequestService;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.cca.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public class RdeTerminatedService {

    private final RequestService requestService;

    public void terminate(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final RequestPayloadRdeable requestPayload = (RequestPayloadRdeable) request.getPayload();
        requestPayload.cleanRdeData();
    }
}
