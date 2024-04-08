package uk.gov.cca.api.workflow.request.flow.rde.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.cca.api.workflow.request.core.service.RequestService;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeDecisionForcedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RdeDecisionType;
import uk.gov.cca.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestPayload;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RdeDecisionForcedService {

    private final RequestService requestService;

    public void force(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final RequestPayload requestPayload = request.getPayload();
        final RequestPayloadRdeable requestPayloadRdeable = (RequestPayloadRdeable) requestPayload;

        // write timeline action
        final RdeDecisionForcedRequestActionPayload timelinePayload =
            RdeDecisionForcedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.RDE_DECISION_FORCED_PAYLOAD)
                .rdeForceDecisionPayload(requestPayloadRdeable.getRdeData().getRdeForceDecisionPayload())
                .rdeAttachments(new HashMap<>(requestPayloadRdeable.getRdeData().getRdeAttachments()))
                .build();
        
        final RequestActionType actionType =
            timelinePayload.getRdeForceDecisionPayload().getDecision() == RdeDecisionType.ACCEPTED ?
                RequestActionType.RDE_FORCE_ACCEPTED : RequestActionType.RDE_FORCE_REJECTED; 
        
        requestService.addActionToRequest(request,
            timelinePayload,
            actionType,
            requestPayload.getRegulatorAssignee());
    }
}
