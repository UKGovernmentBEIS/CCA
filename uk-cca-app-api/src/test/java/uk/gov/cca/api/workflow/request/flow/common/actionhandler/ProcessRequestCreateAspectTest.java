package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.AccountQueryService;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.TestRequestCreateActionPayload;
import uk.gov.netz.api.competentauthority.CompetentAuthorityService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByAccountValidator;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByCAValidator;

import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessRequestCreateAspectTest {

    private ProcessRequestCreateAspect aspect;

    @Mock
    private AccountQueryService accountQueryService;
    
    @Mock
    private CompetentAuthorityService competentAuthorityService;

    @Spy
    private ArrayList<RequestCreateByAccountValidator> requestCreateByAccountValidators;
    
    @Spy
    private ArrayList<RequestCreateByCAValidator> requestCreateByCAValidators;

    private RequestCreateByAccountValidator requestCreateByAccountValidator;
    @Mock
    private JoinPoint joinPoint;

    @BeforeEach
    void setUp() {
        requestCreateByAccountValidator = new RequestCreateByAccountValidator() {
            @Override
            public RequestCreateActionType getType() {
                return RequestCreateActionType.DUMMY_REQUEST_CREATE_ACTION_TYPE;
            }

            @Override
            public RequestCreateValidationResult validateAction(Long accountId) {
                return RequestCreateValidationResult.builder().valid(true).build();
            }
        };
    	requestCreateByAccountValidators.add(requestCreateByAccountValidator);
    	
		aspect = new ProcessRequestCreateAspect(requestCreateByAccountValidators, requestCreateByCAValidators,
            accountQueryService, competentAuthorityService);
    }

    @Test
    void process_account_opening_request_type() {
        final RequestCreateActionType type = RequestCreateActionType.DUMMY_REQUEST_CREATE_ACTION_TYPE;
        final TestRequestCreateActionPayload payload = TestRequestCreateActionPayload.builder().build();
        final AppUser currentUser = AppUser.builder().userId("userId").build();
        final Object[] arguments = new Object[] {
                null, type, payload, currentUser
        };

        when(joinPoint.getArgs()).thenReturn(arguments);

        aspect.process(joinPoint);

        verify(joinPoint, times(1)).getArgs();
        verifyNoInteractions(accountQueryService);
        verifyNoInteractions(competentAuthorityService);
    }
}
