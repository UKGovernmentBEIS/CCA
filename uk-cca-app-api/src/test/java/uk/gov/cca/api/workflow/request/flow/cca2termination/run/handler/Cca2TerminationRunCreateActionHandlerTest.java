package uk.gov.cca.api.workflow.request.flow.cca2termination.run.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2termination.common.config.Cca2TerminationWorkflowConfig;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.service.Cca2TerminationRunInitiateService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
public class Cca2TerminationRunCreateActionHandlerTest {

	@InjectMocks
    private Cca2TerminationRunCreateActionHandler handler;

    @Mock
    private Cca2TerminationRunInitiateService cca2TerminationRunInitiateService;
    
    @Mock
    private Cca2TerminationWorkflowConfig cca2TerminationWorkflowConfig;

    @Test
    void process() {
    	final String userId = "userId";
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().userId(userId).roleType(REGULATOR).build();
        final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	when(cca2TerminationWorkflowConfig.getAccountBusinessIds()).thenReturn(List.of());
    	
        // Invoke
        handler.process(ca, payload, appUser);

        // Verify
        verify(cca2TerminationRunInitiateService, times(1)).createCca2TerminationRun(List.of());
        verify(cca2TerminationWorkflowConfig, times(1)).getAccountBusinessIds();
    }
}
