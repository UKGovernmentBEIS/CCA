package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.TerminateAccountAndOpenWorkflowsService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalisedServiceTest {

	@InjectMocks
    private AdminTerminationFinalisedService finalisedService;
	
    @Mock
    private RequestService requestService;
    
    @Mock
    private TerminateAccountAndOpenWorkflowsService terminateAccountAndOpenWorkflowsService;

    
    @Test
    void terminateAccountStatusAndWorkflows() {
        final String requestId = "requestId";
        final String regulator = "regulator";
        final String processInstanceId = "processInstanceId";

        final AdminTerminationFinalDecisionReasonDetails reasonDetails = AdminTerminationFinalDecisionReasonDetails.builder()
                .finalDecisionType(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                .build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "attachment");
        final CcaDecisionNotification ccaDecisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();

        final Request request = Request.builder()
                .id(requestId)
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

        // Mock the requestService
        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke the method under test
        finalisedService.terminateAccountAndOpenWorkflows(requestId);

        // Verify the interactions
        verify(requestService, times(1)).findRequestById(requestId);
        verify(terminateAccountAndOpenWorkflowsService, times(1)).terminateAccountAndOpenWorkflows(eq(request), any(LocalDateTime.class), eq(regulator));
    }
}
