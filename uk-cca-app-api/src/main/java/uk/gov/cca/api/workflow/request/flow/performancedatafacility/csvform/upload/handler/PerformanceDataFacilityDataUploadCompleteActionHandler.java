package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.transform.PerformanceDataFacilityDataUploadCompletedMapper;
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

@Component
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

	private final RequestTaskService requestTaskService;
	private final WorkflowService workflowService;
    private final RequestService requestService;
    
    private static final PerformanceDataFacilityDataUploadCompletedMapper UPLOAD_COMPLETED_MAPPER = 
    		Mappers.getMapper(PerformanceDataFacilityDataUploadCompletedMapper.class);

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        
    	RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
    	Request request = requestTask.getRequest();
    	
    	PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTask.getPayload();
    	
    	// If the task is closed before the processing has started, set the request status to CLOSED
    	if (CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_CLOSE.equals(requestTaskActionType) 
    			&& PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET.equals(taskPayload.getProcessingStatus())) {
    	    
    		return closeRequest(appUser, requestTask, request, taskPayload);
    	}
        	
    	return completeRequest(appUser, requestTask, request, taskPayload);
    }

	private RequestTaskPayload completeRequest(AppUser appUser, RequestTask requestTask, Request request,
			PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload) {
		
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

	private RequestTaskPayload closeRequest(AppUser appUser, RequestTask requestTask, Request request,
			PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload) {
		
		// Add close action
		requestService.addActionToRequest(
		        request,
		        null,
		        CcaRequestActionType.PERFORMANCE_DATA_FACILITY_UPLOAD_CLOSED,
		        appUser.getUserId());
		
		// Set request status
		request.setStatus(CcaRequestStatuses.CLOSED);
		
		// Complete
		workflowService.completeTask(requestTask.getProcessTaskId());

		return taskPayload;
	}
    
    private void validateCompleted(PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload) {

        if (!PerformanceDataFacilityDataUploadProcessingStatus.COMPLETED.equals(taskPayload.getProcessingStatus())) {

            PerformanceDataFacilityViolation violation = new PerformanceDataFacilityViolation(
            		PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.PROCESS_NOT_COMPLETED);

            BusinessValidationResult validationResult = BusinessValidationResult.invalid(List.of(violation));

            throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_PROCESS_STATUS,
            		ValidatorHelper.extractViolations(List.of(validationResult)));
        }
    }

    private void addCompletedRequestAction(
            AppUser user,
            PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload,
            Request request) {

        PerformanceDataFacilityDataUploadCompletedRequestActionPayload actionPayload = UPLOAD_COMPLETED_MAPPER
        		.toPerformanceDataFacilityDataUploadCompletedRequestActionPayload(taskPayload);

        requestService.addActionToRequest(
                request,
                actionPayload,
                CcaRequestActionType.PERFORMANCE_DATA_FACILITY_UPLOAD_COMPLETED,
                user.getUserId());
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_COMPLETE, 
        		CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_CLOSE);
    }
}
