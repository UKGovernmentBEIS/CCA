package uk.gov.cca.api.workflow.request.flow.cca2termination.run.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationBaseRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class Cca2TerminationRunRequestService {
	
	private final RequestService requestService;
	private final RequestQueryService requestQueryService;
	private final WorkflowService workflowService;
	
    private static final String TERMINATE_REASON = "Workflow terminated by the CCA2 end date workflow.";

	@Transactional
    public void terminateVariationRequests() {
        // Terminate all in progress variation requests that contain CCA2-only facilities in the request or request task payload
		List<Request> requests = requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
        		CcaRequestType.UNDERLYING_AGREEMENT_VARIATION, ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()).stream()
				.filter(req -> RequestStatuses.IN_PROGRESS.equals(req.getStatus()) 
						&& containsCca2OnlyFacility((UnderlyingAgreementVariationRequestPayload) req.getPayload(), req.getRequestTasks()))
				.toList();
		
		requests.stream().forEach(this::terminateRequestAndAddRequestAction);
    }
	
	@Transactional
    public void terminateCca3MigrationAccountProcessingRequests() {
		// Terminate all in progress cca3 migration account processing requests
		List<Request> requests = requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
        		CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING, ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()).stream()
				.filter(req -> RequestStatuses.IN_PROGRESS.equals(req.getStatus()))
				.toList();
		
		requests.stream().forEach(this::terminateRequestAndAddRequestAction);
    }
	
	@Transactional
    public void terminatePerformanceDataRequests() {
        // Terminate all in progress performance data upload/download requests, where the data processing has not started or has been completed
		List<Request> requests = requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
        		CcaRequestType.PERFORMANCE_DATA_UPLOAD, ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()).stream()
				.filter(req -> RequestStatuses.IN_PROGRESS.equals(req.getStatus()) 
						&& processingNotStartedOrCompleted(req.getRequestTasks(), CcaRequestTaskType.PERFORMANCE_DATA_UPLOAD_SUBMIT))
				.collect(Collectors.toList());
		
		requests.addAll(requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
        		CcaRequestType.PERFORMANCE_DATA_DOWNLOAD, ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()).stream()
				.filter(req -> RequestStatuses.IN_PROGRESS.equals(req.getStatus()) 
						&& processingNotStartedOrCompleted(req.getRequestTasks(), CcaRequestTaskType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT))
				.toList());
		
		requests.stream().forEach(request -> terminateRequestWithStatus(request, CcaRequestStatuses.CLOSED));
    }

	public long getNumberOfAccountsCompleted(String requestId) {
		final Request batchRequest = requestService.findRequestById(requestId);
		final Cca2TerminationRunRequestMetadata metadata = (Cca2TerminationRunRequestMetadata) batchRequest.getMetadata();
		return metadata.getCca2TerminationAccountStates()
				.values().stream()
				.filter(report -> report.getSucceeded() != null)
				.count();
	}

	@Transactional
	public void completeCca2TerminationRun(String requestId) {
		final Request batchRequest = requestService.findRequestById(requestId);
		final Cca2TerminationRunRequestMetadata metadata = (Cca2TerminationRunRequestMetadata) batchRequest.getMetadata();
		long failedAccounts = metadata.getCca2TerminationAccountStates()
				.values().stream()
				.filter(report -> Boolean.FALSE.equals(report.getSucceeded()))
				.count();
		// Update status
        if(failedAccounts > 0) {
            requestService.updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
        }		
	}
	
	private boolean containsCca2OnlyFacility(UnderlyingAgreementVariationRequestPayload payload, List<RequestTask> tasks) {
		return tasksContainCca2OnlyFacility(tasks) || requestContainsCca2OnlyFacility(payload);
	}

	private boolean tasksContainCca2OnlyFacility(List<RequestTask> tasks) {
		Set<String> taskTypes = Set.of(
				CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT, 
				CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT, 
				CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW,
				CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW);
		
		return tasks.stream()
				.filter(task -> taskTypes.contains(task.getType().getCode()))
				.flatMap(task -> ((UnderlyingAgreementVariationBaseRequestTaskPayload) task.getPayload()).getUnderlyingAgreement()
						.getUnderlyingAgreement().getFacilities().stream())
				.anyMatch(facility -> facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().equals(Set.of(SchemeVersion.CCA_2)));
	}
	
	private boolean requestContainsCca2OnlyFacility(UnderlyingAgreementVariationRequestPayload payload) {
		return payload.getUnderlyingAgreementProposed() != null 
				&& payload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities().stream()
				.anyMatch(facility -> facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().equals(Set.of(SchemeVersion.CCA_2)));
	}
	
	private boolean processingNotStartedOrCompleted(List<RequestTask> tasks, String taskType) {
		return tasks.stream()
				.filter(task -> taskType.equals(task.getType().getCode()))
				.allMatch(task -> processingNotStartedOrCompleted(task.getPayload()));
	}
	
	private boolean processingNotStartedOrCompleted(RequestTaskPayload payload) {
	    return switch (payload) {
	        case PerformanceDataUploadSubmitRequestTaskPayload p -> 
	        p.getProcessCompleted() == null || p.getProcessCompleted();

	        case PerformanceDataDownloadSubmitRequestTaskPayload p ->
	        p.getProcessCompleted() == null || p.getProcessCompleted();

	        default -> false;
	    };
	}
	
	private void terminateRequestAndAddRequestAction(Request request) {
		requestService.addActionToRequest(request, null, CcaRequestActionType.REQUEST_TERMINATED, null);
		
		terminateRequestWithStatus(request, CcaRequestStatuses.CANCELLED);
	}

	private void terminateRequestWithStatus(Request request, String status) {		
		request.setStatus(status);
		workflowService.deleteProcessInstance(request.getProcessInstanceId(), TERMINATE_REASON);
	}

}
