package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandlerMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.cca.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.core.validation.EnabledWorkflowValidator;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestCreateActionHandlerMapperTest {

    @Mock
    private EnabledWorkflowValidator enabledWorkflowValidator;

    @Mock
    private RequestCreateActionHandler requestCreateActionHandler;

    @BeforeEach
    void setUp() {
        requestCreateActionHandler = new RequestCreateActionHandler() {
            @Override
            public String process(Long accountId, RequestCreateActionType type, RequestCreateActionPayload payload, AppUser appUser) {
                return "id";
            }

            @Override
            public RequestCreateActionType getType() {
                return RequestCreateActionType.DUMMY_REQUEST_CREATE_ACTION_TYPE;
            }
        };
    }
    @Test
    void get_not_return_disabled_workflows() {
        List<RequestCreateActionHandler<? extends RequestCreateActionPayload>> handlers = List.of(requestCreateActionHandler);

        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.DUMMY_REQUEST_TYPE)).thenReturn(false);
        RequestCreateActionHandlerMapper requestCreateActionHandlerMapper = new RequestCreateActionHandlerMapper(handlers, enabledWorkflowValidator);
        assertThrows(BusinessException.class, () -> requestCreateActionHandlerMapper.get(RequestCreateActionType.DUMMY_REQUEST_CREATE_ACTION_TYPE));
        
        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.DUMMY_REQUEST_TYPE)).thenReturn(true);
        RequestCreateActionHandlerMapper requestCreateActionHandlerMapper2 = new RequestCreateActionHandlerMapper(handlers, enabledWorkflowValidator);
        var handler = requestCreateActionHandlerMapper2.get(RequestCreateActionType.DUMMY_REQUEST_CREATE_ACTION_TYPE);

        assertThat(handler).isEqualTo(requestCreateActionHandler);
    }
}
