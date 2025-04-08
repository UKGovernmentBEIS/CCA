package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class MoaCompletedService {
	
	private final RequestService requestService;
	private final WorkflowService workflowService;
	
	@Transactional
	public void completed(
			String requestId, Long resourceId, MoaType moaType, String moaRequestId, Boolean succeeded, List<String> errors) {
		final Request request = requestService.findRequestById(requestId);
		
		// Update report metadata
		SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) request.getMetadata();
		MoaReport moareport = MoaType.SECTOR_MOA.equals(moaType) 
				? metadata.getSectorsReports().get(resourceId) 
						: metadata.getAccountsReports().get(resourceId);
		moareport.setSucceeded(succeeded);
		
		if(Boolean.TRUE.equals(succeeded)) {
			moareport.setIssueDate(LocalDate.now());
		} else {
			moareport.setErrors(errors);
			//delete request
			final Request moaRequest = requestService.findRequestById(moaRequestId);
			workflowService.deleteProcessInstance(moaRequest.getProcessInstanceId(), "Moa request failed");
		}
	}

}
