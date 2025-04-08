package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationOutcome;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.service.UnderlyingAgreementVariationSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationSubmitActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UnderlyingAgreementVariationSubmitService underlyingAgreementVariationSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION;
        final RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder().build();

        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final UnderlyingAgreementVariationSubmitRequestTaskPayload requestTaskPayload = UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
        		.id(requestTaskId)
        		.request(request)
        		.processTaskId(processTaskId)
        		.payload(requestTaskPayload)
        		.build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload = handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementVariationSubmitService, times(1))
                .submitUnderlyingAgreementVariation(requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_OUTCOME, UnderlyingAgreementVariationOutcome.SUBMITTED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION);
    }
}
