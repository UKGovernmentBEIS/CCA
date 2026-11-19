package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingCreateValidatorTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator validator;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void validateCreation() {
        final Long accountId = 1L;
        final RequestCreateActionEmptyPayload payload =  new RequestCreateActionEmptyPayload();

        when(requestQueryService
                .existsRequestByAccountAndType(accountId, CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING))
                .thenReturn(false);
        when(requestQueryService.findInProgressRequestsByAccount(accountId))
                .thenReturn(List.of());

        // Invoke
        RequestCreateValidationResult result = validator.validateCreation(accountId, payload);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(requestQueryService, times(1))
                .existsRequestByAccountAndType(accountId, CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING);
        verify(requestQueryService, times(1)).findInProgressRequestsByAccount(accountId);
    }

    @Test
    void validateAction_request_exist_not_valid() {
        final Long accountId = 1L;
        final RequestCreateActionEmptyPayload payload =  new RequestCreateActionEmptyPayload();

        when(requestQueryService
                .existsRequestByAccountAndType(accountId, CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING))
                .thenReturn(true);

        // Invoke
        RequestCreateValidationResult result = validator.validateCreation(accountId, payload);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder()
                .valid(false)
                .reportedRequestTypes(Set.of(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING))
                .build());
        verify(requestQueryService, times(1))
                .existsRequestByAccountAndType(accountId, CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING);
        verifyNoMoreInteractions(requestQueryService);
    }

    @Test
    void validateCreation_in_progress_exist_not_valid() {
        final Long accountId = 1L;
        final RequestCreateActionEmptyPayload payload =  new RequestCreateActionEmptyPayload();

        when(requestQueryService
                .existsRequestByAccountAndType(accountId, CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING))
                .thenReturn(false);
        when(requestQueryService.findInProgressRequestsByAccount(accountId))
                .thenReturn(List.of(Request.builder().type(RequestType.builder().code(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION).build()).build()));

        // Invoke
        RequestCreateValidationResult result = validator.validateCreation(accountId, payload);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder()
                .valid(false)
                .reportedRequestTypes(Set.of(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION))
                .build());
        verify(requestQueryService, times(1))
                .existsRequestByAccountAndType(accountId, CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING);
        verify(requestQueryService, times(1)).findInProgressRequestsByAccount(accountId);
    }

    @Test
    void getApplicableAccountStatuses() {
        assertThat(validator.getApplicableAccountStatuses()).isEmpty();
    }

    @Test
    void getMutuallyExclusiveRequests() {
        assertThat(validator.getMutuallyExclusiveRequests())
                .containsExactlyInAnyOrder(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION, CcaRequestType.ADMIN_TERMINATION);
    }

    @Test
    void getRequestType() {
        assertThat(validator.getRequestType()).isEqualTo(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING);
    }
}
