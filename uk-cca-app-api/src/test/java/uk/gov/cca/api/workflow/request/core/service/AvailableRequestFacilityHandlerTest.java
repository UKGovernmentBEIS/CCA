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

import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.FacilityRequestAuthorizationResourceService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByFacilityValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class AvailableRequestFacilityHandlerTest {

	private AvailableRequestFacilityHandler handler;

    @Mock
    private FacilityRequestAuthorizationResourceService facilityRequestAuthorizationResourceService;

    @Mock
    private AvailableRequestFacilityHandlerTest.TestRequestCreateValidatorA requestCreateValidatorA;

    @Mock
    private AvailableRequestFacilityHandlerTest.TestRequestCreateValidatorB requestCreateValidatorB;

    
    @BeforeEach
    void setUp() {
        ArrayList<RequestCreateByFacilityValidator> requestCreateBySectorValidators = new ArrayList<>();
        requestCreateBySectorValidators.add(requestCreateValidatorA);
        requestCreateBySectorValidators.add(requestCreateValidatorB);

        handler = new AvailableRequestFacilityHandler(
        		facilityRequestAuthorizationResourceService, requestCreateBySectorValidators);
    }
    
    @Test
    void getAvailableRequestsForResource() {
        final AppUser user = AppUser.builder().userId("user").build();
        final String resourceId = "1";
        final RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        
        final Set<String> allManuallyCreateCreateRequestTypes = Set.of("code1", "code2", "code3");
        
        when(facilityRequestAuthorizationResourceService.findRequestCreateActionsByFacilityId(user, 1L))
        	.thenReturn(Set.of("code1"));
        when(requestCreateValidatorA.getRequestType()).thenReturn("code1");
        when(requestCreateValidatorA.validateAction(1L)).thenReturn(result);
        
        // Invoke
        final Map<String, RequestCreateValidationResult> availableWorkflows =
        		handler.getAvailableRequestsForResource(resourceId, allManuallyCreateCreateRequestTypes, user);

        // Verify
        verify(facilityRequestAuthorizationResourceService, times(1))
        		.findRequestCreateActionsByFacilityId(user, 1L);
        verify(requestCreateValidatorA, times(1)).getRequestType();
        verify(requestCreateValidatorA, times(1)).validateAction(1L);

        assertThat(availableWorkflows).containsExactly(Map.entry("code1", result));
    }
    
    private static class TestRequestCreateValidatorA implements RequestCreateByFacilityValidator {

        @Override
        public RequestCreateValidationResult validateAction(Long sectorId) {
            return null;
        }

        @Override
        public String getRequestType() {
            return null;
        }
    }

    private static class TestRequestCreateValidatorB implements RequestCreateByFacilityValidator {

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
