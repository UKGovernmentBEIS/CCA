package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.validation.FacilityPerformanceAccountTemplateDataViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.transform.FacilityPerformanceAccountTemplateDataUploadCompletedMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
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
    private final RequestService requestService;

    private static final FacilityPerformanceAccountTemplateDataUploadCompletedMapper UPLOAD_COMPLETED_MAPPER =
            Mappers.getMapper(FacilityPerformanceAccountTemplateDataUploadCompletedMapper.class);

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        Request request = requestTask.getRequest();

        FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload =
                (FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        // If the task is closed before the processing has started, set the request status to CLOSED
        if (CcaRequestTaskActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_CLOSE.equals(requestTaskActionType)
                && FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET.equals(taskPayload.getProcessingStatus())) {

            return closeRequest(appUser, requestTask, request, taskPayload);
        }

        return completeRequest(appUser, requestTask, request, taskPayload);
    }

    private RequestTaskPayload completeRequest(AppUser appUser, RequestTask requestTask, Request request,
                                               FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload) {

        // Validate if process is finished
        validateCompleted(taskPayload);

        // Add submit action request
        addCompletedRequestAction(appUser, taskPayload, requestTask.getRequest());

        // Set request's submission date and status
        request.setSubmissionDate(LocalDateTime.now());
        request.setStatus(RequestStatuses.COMPLETED);

        // Complete
        workflowService.completeTask(requestTask.getProcessTaskId());

        return taskPayload;
    }

    private void validateCompleted(FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload) {

        if (!FacilityPerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED.equals(taskPayload.getProcessingStatus())) {

            throw new BusinessException(CcaErrorCode.INVALID_FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_UPLOAD_PROCESS_STATUS,
                    FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.PROCESS_NOT_COMPLETED.getMessage());
        }
    }

    private void addCompletedRequestAction(AppUser user,
                                           FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload,
                                           Request request) {

        FacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload actionPayload = UPLOAD_COMPLETED_MAPPER
                .toFacilityPerformanceAccountTemplateDataUploadCompletedRequestActionPayload(taskPayload);

        requestService.addActionToRequest(
                request,
                actionPayload,
                CcaRequestActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETED,
                user.getUserId());
    }

    private RequestTaskPayload closeRequest(AppUser appUser, RequestTask requestTask, Request request,
                                            FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload taskPayload) {

        // Add close action
        requestService.addActionToRequest(
                request,
                null,
                CcaRequestActionType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_CLOSED,
                appUser.getUserId());

        // Set request status
        request.setStatus(CcaRequestStatuses.CLOSED);

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
