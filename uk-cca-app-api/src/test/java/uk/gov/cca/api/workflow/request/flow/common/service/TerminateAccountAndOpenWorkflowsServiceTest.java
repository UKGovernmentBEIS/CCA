package uk.gov.cca.api.workflow.request.flow.common.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class TerminateAccountAndOpenWorkflowsServiceTest {

	@InjectMocks
    private TerminateAccountAndOpenWorkflowsService terminateAccountAndOpenWorkflowsService;
	
	@Mock
    private RequestService requestService;

    @Mock
    private TargetUnitAccountUpdateService targetUnitAccountUpdateService;
    
    @Mock
    private RequestQueryService requestQueryService;
   
    @Mock
    private FacilityDataUpdateService facilityDataUpdateService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private UnderlyingAgreementService underlyingAgreementService;
    
    private static final String TERMINATE_REASON = "Workflow terminated by the system because the account was terminated";


    @Test
    void terminateAccountAndOpenWorkflows() {
        final String requestId = "requestId";
        final Long accountId = 999L;
        final String regulator = "regulator";
        final String processInstanceId = "processInstanceId";
        final String businessId = "ADS_53-T00004";
        final LocalDateTime terminationDate = LocalDateTime.now();

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.LIVE)
                .businessId(businessId)
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .processInstanceId(processInstanceId)
                .payload(AdminTerminationRequestPayload.builder()
                        .regulatorAssignee(regulator)
                        .build())
                .type(RequestType.builder()
                        .code(CcaRequestType.ADMIN_TERMINATION)
                        .build())
                .status(RequestStatuses.IN_PROGRESS)
                .build();
        addResourcesToRequest(accountId, request);
        account.setId(accountId);

        // Mock the requestQueryService to include the created request
        Request anotherRequest = Request.builder()
                .id("anotherRequestId")
                .processInstanceId("anotherProcessInstanceId")
                .payload(AdminTerminationRequestPayload.builder()
                        .regulatorAssignee(regulator)
                        .build())
                .type(RequestType.builder().code("OTHER_TYPE").build())
                .status(RequestStatuses.IN_PROGRESS)
                .build();
        addResourcesToRequest(accountId, anotherRequest);

        // Facility Audit request
        Request facilityAuditRequest = Request.builder()
                .id("facilityAuditRequestId")
                .processInstanceId("facilityAuditProcessInstanceId")
                .payload(FacilityAuditRequestPayload.builder()
                        .regulatorAssignee(regulator)
                        .build())
                .type(RequestType.builder().code(CcaRequestType.FACILITY_AUDIT).build())
                .status(RequestStatuses.IN_PROGRESS)
                .build();
        addResourcesToRequest(accountId, facilityAuditRequest);
        
        // Non-compiance request
        Request nonComplianceRequest = Request.builder()
                .id("nonComplianceRequestId")
                .processInstanceId("nonComplianceProcessInstanceId")
                .payload(CcaRequestPayload.builder()
                        .regulatorAssignee(regulator)
                        .build())
                .type(RequestType.builder().code(CcaRequestType.NON_COMPLIANCE).build())
                .status(RequestStatuses.IN_PROGRESS)
                .build();
        addResourcesToRequest(accountId, nonComplianceRequest);

        when(requestQueryService.findInProgressRequestsByAccount(accountId))
        		.thenReturn(List.of(request, anotherRequest, facilityAuditRequest, nonComplianceRequest));

        // Invoke the method under test
        terminateAccountAndOpenWorkflowsService.terminateAccountAndOpenWorkflows(request, terminationDate, regulator);

        // Verify the interactions
        verify(targetUnitAccountUpdateService, times(1)).handleTargetUnitAccountTerminated(eq(accountId), any(LocalDateTime.class));
        verify(underlyingAgreementService, times(1)).terminateActiveUnaDocuments(eq(accountId), any(LocalDateTime.class));
        verify(workflowService, times(1)).deleteProcessInstance("anotherProcessInstanceId", TERMINATE_REASON);
        verify(requestService, times(1)).addActionToRequest(eq(anotherRequest), any(), eq(CcaRequestActionType.REQUEST_TERMINATED), eq(regulator));

        // Verifying that no action was taken on the request itself plus Facility Audit and Non-Compliance
        verify(requestService, never()).addActionToRequest(eq(request), any(), eq(CcaRequestActionType.REQUEST_TERMINATED), eq(regulator));
        verify(workflowService, never()).deleteProcessInstance(processInstanceId, TERMINATE_REASON);
        verify(requestService, never()).addActionToRequest(eq(facilityAuditRequest), any(), eq(CcaRequestActionType.REQUEST_TERMINATED), eq(regulator));
        verify(workflowService, never()).deleteProcessInstance("facilityAuditProcessInstanceId", TERMINATE_REASON);
        verify(requestService, never()).addActionToRequest(eq(nonComplianceRequest), any(), eq(CcaRequestActionType.REQUEST_TERMINATED), eq(regulator));
        verify(workflowService, never()).deleteProcessInstance("nonComplianceProcessInstanceId", TERMINATE_REASON);

        verify(facilityDataUpdateService, times(1)).terminateActiveFacilities(eq(accountId), any(LocalDateTime.class));
        verifyNoMoreInteractions(requestService, targetUnitAccountUpdateService, underlyingAgreementService, requestQueryService, workflowService);

    }
    
    private void addResourcesToRequest(Long accountId, Request request) {
		RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.ACCOUNT)
				.resourceId(accountId.toString())
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
	}
}
