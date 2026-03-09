package uk.gov.cca.api.workflow.request.flow.noncompliance.details.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.service.NonComplianceDetailsSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.transform.NonComplianceDetailsSubmitMapper;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.validation.NonComplianceDetailsSubmitValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceDetailsSubmitCompleteActionHandlerTest {

    @InjectMocks
    private NonComplianceDetailsSubmitCompleteActionHandler handler;

    @Mock
    private RequestService requestService;
    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private NonComplianceDetailsSubmitService nonComplianceDetailsSubmitService;

    @Mock
    private NonComplianceDetailsSubmitValidator nonComplianceDetailsSubmitValidator;

    private final NonComplianceDetailsSubmitMapper NON_COMPLIANCE_DETAILS_SUBMIT_MAPPER = Mappers.getMapper(NonComplianceDetailsSubmitMapper.class);

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
        final RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();

        final NonComplianceDetailsSubmitRequestTaskPayload requestTaskPayload = NonComplianceDetailsSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_DETAILS_SUBMIT_PAYLOAD)
                .nonComplianceDetails(NonComplianceDetails.builder()
                        .nonCompliantDate(LocalDate.now().minusDays(1))
                        .nonComplianceType(NonComplianceType.FAILURE_TO_NOTIFY_OF_AN_ERROR)
                        .isEnforcementResponseNoticeRequired(false)
                        .build())
                .build();

        NonComplianceDetailsSubmittedRequestActionPayload actionPayload =
                NON_COMPLIANCE_DETAILS_SUBMIT_MAPPER.toNonComplianceDetailsSubmittedRequestActionPayload(requestTaskPayload);

        Request request = Request.builder().id(requestId).build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload =
                handler.process(requestTaskId, CcaRequestTaskActionType.NON_COMPLIANCE_DETAILS_SUBMIT_APPLICATION, appUser, taskActionEmptyPayload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(nonComplianceDetailsSubmitValidator, times(1)).validate(requestTaskPayload);
        verify(nonComplianceDetailsSubmitService, times(1)).submitDetails(requestTask);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.NON_COMPLIANCE_DETAILS_SUBMITTED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, "",
                        CcaBpmnProcessConstants.IS_NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_NEEDED, false));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_DETAILS_SUBMIT_APPLICATION);
    }
}
