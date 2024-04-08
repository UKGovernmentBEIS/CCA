package uk.gov.cca.api.workflow.request.flow.rfi.handler;

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
import uk.gov.cca.api.workflow.request.flow.rfi.handler.RfiSubmitActionHandler;
import uk.gov.cca.api.workflow.request.flow.rfi.service.RfiSendEventService;
import uk.gov.cca.api.workflow.request.flow.rfi.service.RfiSubmitOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.rfi.validation.SubmitRfiValidatorService;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RfiSubmitActionHandlerTest {
    
    @InjectMocks
    private RfiSubmitActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RfiSendEventService rfiSendEventService;

    @Mock
    private SubmitRfiValidatorService validator;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;
    
    @Mock
    private UserAuthService userAuthService;
    
    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;
    
    @Mock
    private RfiSubmitOfficialNoticeService rfiSubmitOfficialNoticeService;


    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.RFI_SUBMIT);
    }
}
