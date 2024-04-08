package uk.gov.cca.api.workflow.request.flow.rde.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.service.RequestService;
import uk.gov.cca.api.workflow.request.core.service.RequestTaskService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.rde.handler.RdeSubmitActionHandler;
import uk.gov.cca.api.workflow.request.flow.rde.service.RdeSendEventService;
import uk.gov.cca.api.workflow.request.flow.rde.service.RdeSubmitOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.rde.validation.SubmitRdeValidatorService;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RdeSubmitActionHandlerTest {

    @InjectMocks
    private RdeSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;
    
    @Mock
    private UserAuthService userAuthService;
    
    @Mock
    private SubmitRdeValidatorService validator;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private RequestService requestService;

    @Mock
    private RdeSendEventService rdeSendEventService;
    
    @Mock
    private RdeSubmitOfficialNoticeService rdeSubmitOfficialNoticeService;


    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.RDE_SUBMIT);
    }
}
