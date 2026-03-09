package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service.UnderlyingAgreementVariationRejectedAddRequestActionService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRejectedAddRequestActionHandlerFlowableTest {

    @InjectMocks
    private UnderlyingAgreementVariationRejectedAddRequestActionHandlerFlowable handler;

    @Mock
    private UnderlyingAgreementVariationRejectedAddRequestActionService requestActionService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        verify(requestActionService, times(1)).addRequestAction(requestId);
    }
}