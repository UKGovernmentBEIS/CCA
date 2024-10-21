package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalisedServiceTest {

    @Mock
    private RequestService requestService;
    @InjectMocks
    private AdminTerminationFinalisedService finalisedService;

    @Mock
    private TargetUnitAccountUpdateService targetUnitAccountUpdateService;
    @Mock
    private RequestQueryService requestQueryService;
    @Mock
    private FacilityDataUpdateService facilityDataUpdateService;

    @Mock
    private WorkflowService workflowService;
    private static final String TERMINATE_REASON = "Workflow terminated by the system because the final decision is \"Terminate agreement\"";


    @Test
    void terminateAccountStatusAndWorkflows() {
        final String requestId = "requestId";
        final Long accountId = 999L;
        final String regulator = "regulator";
        final String processInstanceId = "processInstanceId";

        final AdminTerminationFinalDecisionReasonDetails reasonDetails = AdminTerminationFinalDecisionReasonDetails.builder()
                .finalDecisionType(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                .build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "attachment");
        final CcaDecisionNotification ccaDecisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();

        String businessId = "ADS_53-T00004";

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(accountId)
                .status(TargetUnitAccountStatus.LIVE)
                .businessId(businessId)
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .processInstanceId(processInstanceId)
                .payload(AdminTerminationRequestPayload.builder()
                        .regulatorAssignee(regulator)
                        .adminTerminationFinalDecisionReasonDetails(reasonDetails)
                        .adminTerminationFinalDecisionAttachments(attachments)
                        .decisionNotification(ccaDecisionNotification)
                        .build())
                .type(RequestType.builder()
                        .code(CcaRequestType.ADMIN_TERMINATION)
                        .build())
                .status(RequestStatuses.IN_PROGRESS)
                .build();
        account.setId(accountId);

        // Mock the requestService
        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Mock the requestQueryService to include the created request
        Request anotherRequest = Request.builder()
                .id("anotherRequestId")
                .accountId(accountId)
                .processInstanceId("anotherProcessInstanceId")
                .payload(AdminTerminationRequestPayload.builder()
                        .regulatorAssignee(regulator)
                        .adminTerminationFinalDecisionReasonDetails(reasonDetails)
                        .adminTerminationFinalDecisionAttachments(attachments)
                        .decisionNotification(ccaDecisionNotification)
                        .build())
                .type(RequestType.builder().code("OTHER_TYPE").build())
                .status(RequestStatuses.IN_PROGRESS)
                .build();

        when(requestQueryService.findInProgressRequestsByAccount(accountId)).thenReturn(List.of(request, anotherRequest));

        // Invoke the method under test
        finalisedService.terminateAccountAndOpenWorkflows(requestId);

        // Verify the interactions
        verify(requestService, times(1)).findRequestById(requestId);
        verify(targetUnitAccountUpdateService, times(1)).handleTargetUnitAccountTerminated(accountId);
        verify(workflowService, times(1)).deleteProcessInstance("anotherProcessInstanceId", TERMINATE_REASON);
        verify(requestService, times(1)).addActionToRequest(eq(anotherRequest), any(), eq(CcaRequestActionType.REQUEST_TERMINATED), eq(regulator));

        // Verifying that no action was taken on the ADMIN_TERMINATION request itself since it is filtered out
        verify(requestService, never()).addActionToRequest(eq(request), any(), eq(CcaRequestActionType.REQUEST_TERMINATED), eq(regulator));
        verify(workflowService, never()).deleteProcessInstance(processInstanceId, TERMINATE_REASON);

        verify(facilityDataUpdateService, times(1)).terminateFacilities(accountId);
        verifyNoMoreInteractions(requestService, targetUnitAccountUpdateService, requestQueryService, workflowService);

    }
}
