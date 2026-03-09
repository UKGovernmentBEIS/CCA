package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.flow.common.service.TerminateAccountAndOpenWorkflowsService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class AdminTerminationFinalisedService {
	
	private final RequestService requestService;
	private final TerminateAccountAndOpenWorkflowsService terminateAccountAndOpenWorkflowsService;

    @Transactional
    public void terminateAccountAndOpenWorkflows(String requestId) {
    	final Request request = requestService.findRequestById(requestId);
    	final LocalDateTime terminationDate = LocalDateTime.now();
    	final String terminatedBy = request.getPayload().getRegulatorAssignee();
    	
    	terminateAccountAndOpenWorkflowsService.terminateAccountAndOpenWorkflows(request, terminationDate, terminatedBy);

    }
}
