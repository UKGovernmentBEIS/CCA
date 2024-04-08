package uk.gov.cca.api.workflow.request.flow.rde.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.WorkflowService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.service.RequestTaskService;
import uk.gov.cca.api.workflow.request.flow.rde.handler.RdeForceDecisionActionHandler;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class RdeForceDecisionActionHandlerTest {

    @InjectMocks
    private RdeForceDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.RDE_FORCE_DECISION);
    }
}
