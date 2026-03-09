package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivatedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedActivatedHandlerFlowableTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedActivatedHandlerFlowable handler;

    @Mock
    private UnderlyingAgreementVariationActivatedService underlyingAgreementVariationActivatedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(underlyingAgreementVariationActivatedService, times(1))
                .activateUnderlyingAgreementVariation(requestId);
    }
}
