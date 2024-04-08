package uk.gov.cca.api.workflow.request.flow.rfi.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestTaskAttachmentsUncoupleService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RfiResponseSubmitInitializer implements InitializeRequestTaskHandler {

    private final RequestTaskAttachmentsUncoupleService uncoupleService;

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final RequestPayloadRfiable requestPayload = (RequestPayloadRfiable) request.getPayload();
        final Map<UUID, String> requestRfiAttachments = requestPayload.getRfiData().getRfiAttachments();

        final RfiResponseSubmitRequestTaskPayload requestTaskPayload =
            RfiResponseSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.RFI_RESPONSE_SUBMIT_PAYLOAD)
                .rfiQuestionPayload(requestPayload.getRfiData().getRfiQuestionPayload())
                .rfiAttachments(new HashMap<>(requestRfiAttachments))
                .build();
        
        requestRfiAttachments.clear();

        uncoupleService.uncoupleAttachments(requestTaskPayload);

        return requestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return RequestTaskType.getRfiResponseTypes();
    }
}
