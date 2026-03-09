package uk.gov.cca.api.workflow.bpmn.flowable.handler.underlyingagreementvariation;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestCustomContext;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationSetRegulatorLedRequestTypePrefixHandlerFlowableTest {

    @InjectMocks
    private UnderlyingAgreementVariationSetRegulatorLedRequestTypePrefixHandlerFlowable handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution,times(1)).setVariable(BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX,
                CcaRequestCustomContext.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED);
    }
}
