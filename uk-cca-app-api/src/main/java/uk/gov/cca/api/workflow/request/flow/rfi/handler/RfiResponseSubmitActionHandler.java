package uk.gov.cca.api.workflow.request.flow.rfi.handler;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.core.service.RequestTaskService;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiOutcome;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiResponseSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.WorkflowService;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.cca.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RfiResponseSubmitActionHandler
    implements RequestTaskActionHandler<RfiResponseSubmitRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;


    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final RfiResponseSubmitRequestTaskActionPayload taskActionPayload) {
        
        // update task payload
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final RfiResponseSubmitRequestTaskPayload taskPayload = (RfiResponseSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setRfiResponsePayload(taskActionPayload.getRfiResponsePayload());
        
        // update request payload
        final RequestPayloadRfiable requestPayload = (RequestPayloadRfiable) request.getPayload();
        requestPayload.getRfiData().setRfiResponsePayload(taskPayload.getRfiResponsePayload());
        requestPayload.getRfiData().setRfiAttachments(taskPayload.getRfiAttachments());

        // complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                   BpmnProcessConstants.RFI_OUTCOME, RfiOutcome.RESPONDED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.RFI_RESPONSE_SUBMIT);
    }
}
