package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PerformanceDataUploadCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PerformanceDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataUploadSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate if process is finished
        if(!Boolean.TRUE.equals(taskPayload.getProcessCompleted())) {
            PerformanceDataUploadViolation violation = new PerformanceDataUploadViolation(
                    PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.PROCESS_NOT_COMPLETED);
            BusinessValidationResult validationResult = BusinessValidationResult.invalid(List.of(violation));

            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPLOAD,
                    ValidatorHelper.extractViolations(List.of(validationResult)));
        }

        // Set request's submission date
        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        // Complete
        workflowService.completeTask(requestTask.getProcessTaskId());

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_UPLOAD_COMPLETE);
    }
}
