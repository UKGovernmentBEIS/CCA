package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateDataUploadCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {


    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        Request request = requestTask.getRequest();

        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        // If the task is closed before the processing has started, set the request status to CLOSED
        if (CcaRequestTaskActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_CLOSE.equals(requestTaskActionType)
                && FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET.equals(taskPayload.getProcessingStatus())) {

            workflowService.completeTask(requestTask.getProcessTaskId());
            request.setStatus(CcaRequestStatuses.CLOSED);

            return taskPayload;
        }

        // TODO: Validate if process is finished

        // TODO: Add submit action request

        // Set request's submission date and status
        request.setSubmissionDate(LocalDateTime.now());
        request.setStatus(RequestStatuses.COMPLETED);

        // Complete
        workflowService.completeTask(requestTask.getProcessTaskId());

        return taskPayload;
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETE,
                CcaRequestTaskActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_CLOSE);
    }
}
