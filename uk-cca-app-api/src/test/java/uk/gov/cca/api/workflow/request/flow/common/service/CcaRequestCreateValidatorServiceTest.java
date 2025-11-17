package uk.gov.cca.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class CcaRequestCreateValidatorServiceTest {

	@InjectMocks
    private CcaRequestCreateValidatorService validatorService;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void validate() {
        final Long resourceId = 1L;
        final String resourceType = CcaResourceType.SECTOR_ASSOCIATION;
        final Set<String> mutuallyExclusiveRequestsTypes = Set.of("DUMMY_REQUEST_TYPE");

        when(requestQueryService.findInProgressRequestsByResource(resourceId, resourceType)).thenReturn(
                List.of(Request.builder().type(RequestType.builder().code("another").build()).build())
        );

        // Invoke
        RequestCreateValidationResult result = validatorService
                .validate(resourceId, resourceType, mutuallyExclusiveRequestsTypes);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(requestQueryService, times(1)).findInProgressRequestsByResource(resourceId, resourceType);
    }

    @Test
    void validate_whenConflicts_thenFail() {
    	final Long resourceId = 1L;
        final String resourceType = CcaResourceType.SECTOR_ASSOCIATION;
        final Set<String> mutuallyExclusiveRequestsTypes = Set.of("DUMMY_REQUEST_TYPE");

        when(requestQueryService.findInProgressRequestsByResource(resourceId, resourceType)).thenReturn(
                List.of(Request.builder().type(RequestType.builder().code("DUMMY_REQUEST_TYPE").build()).build())
        );

        // Invoke
        RequestCreateValidationResult result = validatorService
                .validate(resourceId, resourceType, mutuallyExclusiveRequestsTypes);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(false)
                .reportedRequestTypes(Set.of("DUMMY_REQUEST_TYPE")).build());

        verify(requestQueryService, times(1)).findInProgressRequestsByResource(resourceId, resourceType);
    }
}
