package uk.gov.cca.api.workflow.request.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationRequestAuthorizationResourceService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAssociationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class AvailableRequestSectorAssociationHandlerTest {

	private AvailableRequestSectorAssociationHandler handler;

    @Mock
    private SectorAssociationRequestAuthorizationResourceService sectorAssociationRequestAuthorizationResourceService;

    @Mock
    private AvailableRequestSectorAssociationHandlerTest.TestRequestCreateValidatorA requestCreateValidatorA;

    @Mock
    private AvailableRequestSectorAssociationHandlerTest.TestRequestCreateValidatorB requestCreateValidatorB;

    
    @BeforeEach
    public void setUp() {
        ArrayList<RequestCreateBySectorAssociationValidator> requestCreateBySectorValidators = new ArrayList<>();
        requestCreateBySectorValidators.add(requestCreateValidatorA);
        requestCreateBySectorValidators.add(requestCreateValidatorB);

        handler = new AvailableRequestSectorAssociationHandler(
        		sectorAssociationRequestAuthorizationResourceService, requestCreateBySectorValidators);
    }
    
    @Test
    void getAvailableRequestsForResource() {
        final AppUser user = AppUser.builder().userId("user").build();
        final String resourceId = "1";
        final RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        
        final Set<String> allManuallyCreateCreateRequestTypes = Set.of("code1", "code2", "code3");
        
        when(sectorAssociationRequestAuthorizationResourceService.findRequestCreateActionsBySectorAssociationId(user, 1L))
        	.thenReturn(Set.of("code1"));
        when(requestCreateValidatorA.getRequestType()).thenReturn("code1");
        when(requestCreateValidatorA.validateAction(1L)).thenReturn(result);
        
        // Invoke
        final Map<String, RequestCreateValidationResult> availableWorkflows =
        		handler.getAvailableRequestsForResource(resourceId, allManuallyCreateCreateRequestTypes, user);

        // Verify
        verify(sectorAssociationRequestAuthorizationResourceService, times(1))
        		.findRequestCreateActionsBySectorAssociationId(user, 1L);
        verify(requestCreateValidatorA, times(1)).getRequestType();
        verify(requestCreateValidatorA, times(1)).validateAction(1L);

        assertThat(availableWorkflows).containsExactly(Map.entry("code1", result));
    }
    
    private static class TestRequestCreateValidatorA implements RequestCreateBySectorAssociationValidator {

        @Override
        public RequestCreateValidationResult validateAction(Long sectorId) {
            return null;
        }

        @Override
        public String getRequestType() {
            return null;
        }
    }

    private static class TestRequestCreateValidatorB implements RequestCreateBySectorAssociationValidator {

        @Override
        public RequestCreateValidationResult validateAction(Long sectorId) {
            return null;
        }

        @Override
        public String getRequestType() {
            return null;
        }
    }
}
