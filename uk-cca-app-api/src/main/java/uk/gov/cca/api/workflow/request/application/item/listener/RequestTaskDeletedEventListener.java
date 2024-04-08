package uk.gov.cca.api.workflow.request.application.item.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.application.item.service.RequestTaskVisitService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestTaskAttachmentsUncoupleService;
import uk.gov.cca.api.workflow.request.application.taskdeleted.RequestTaskDeletedEvent;

@RequiredArgsConstructor
@Component
public class RequestTaskDeletedEventListener {

    private final RequestTaskVisitService requestTaskVisitService;
    private final RequestTaskAttachmentsUncoupleService requestTaskAttachmentsUncoupleService;

    @EventListener
    public void onRequestTaskDeletedEvent(final RequestTaskDeletedEvent event) {

        final Long requestTaskId = event.getRequestTaskId();
        requestTaskVisitService.deleteByTaskId(requestTaskId);
        requestTaskAttachmentsUncoupleService.deletePendingAttachments(requestTaskId);
    }
}
