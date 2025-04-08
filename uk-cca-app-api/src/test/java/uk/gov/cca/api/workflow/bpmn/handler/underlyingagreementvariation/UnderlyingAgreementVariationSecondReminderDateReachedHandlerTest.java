package uk.gov.cca.api.workflow.bpmn.handler.underlyingagreementvariation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.SendReminderNotificationService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.time.Instant;
import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationSecondReminderDateReachedHandlerTest {

    @InjectMocks
    UnderlyingAgreementVariationSecondReminderDateReachedHandler handler;
    @Mock
    RequestService requestService;
    @Mock
    SendReminderNotificationService reminderNotificationService;
    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception{
        final String requestId = "1";
        final Date expirationDate = Date.from(Instant.now());

        final UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .regulatorAssignee("bbb2820b-cbc6-4923-b3f1-8de409ea34c1")
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .type(RequestType.builder().code(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION).resourceType(ResourceType.ACCOUNT).build())
                .payload(payload).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(execution.getVariable(CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_EXPIRATION_DATE)).thenReturn(expirationDate);
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_EXPIRATION_DATE);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(reminderNotificationService, times(1)).sendSecondReminderNotification(request, expirationDate, request.getPayload().getRegulatorAssignee());
    }
}