package uk.gov.cca.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.cca.api.account.TestAccountStatus;
import uk.gov.cca.api.account.domain.enumeration.AccountStatus;
import uk.gov.cca.api.account.service.AccountQueryService;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.core.service.RequestQueryService;
import uk.gov.cca.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestCreateValidatorServiceTest {

    @InjectMocks
    private RequestCreateValidatorService validatorService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void validate() {
        final Long accountId = 1L;
        final Set<AccountStatus> applicableAccountStatuses = Set.of(
                TestAccountStatus.DUMMY,
                TestAccountStatus.DUMMY2
        );
        final Set<RequestType> mutuallyExclusiveRequests = Set.of(RequestType.DUMMY_REQUEST_TYPE);

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(TestAccountStatus.DUMMY);
        when(requestQueryService.findInProgressRequestsByAccount(accountId)).thenReturn(
                List.of(Request.builder().type(mock(RequestType.class)).build())
        );

        // Invoke
        RequestCreateValidationResult result = validatorService
                .validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findInProgressRequestsByAccount(accountId);
    }

    @Test
    void validate_failed() {
        final Long accountId = 1L;
        final Set<AccountStatus> applicableAccountStatuses = Set.of(
                TestAccountStatus.DUMMY
        );

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(TestAccountStatus.DUMMY2);

        // Invoke
        RequestCreateValidationResult result = validatorService
                .validate(accountId, applicableAccountStatuses, Set.of());

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(false)
                .reportedAccountStatus(TestAccountStatus.DUMMY2)
                .applicableAccountStatuses(Set.of(
                        TestAccountStatus.DUMMY
                )).build());

        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, never()).findInProgressRequestsByAccount(anyLong());
    }

    @Test
    void validate_whenConflicts_thenFail() {
        final Long accountId = 1L;
        final Set<AccountStatus> applicableAccountStatuses = Set.of(
                TestAccountStatus.DUMMY,
                TestAccountStatus.DUMMY2
        );
        final Set<RequestType> mutuallyExclusiveRequests = Set.of(RequestType.DUMMY_REQUEST_TYPE);

        when(accountQueryService.getAccountStatus(accountId)).thenReturn(TestAccountStatus.DUMMY);
        when(requestQueryService.findInProgressRequestsByAccount(accountId)).thenReturn(
                List.of(Request.builder().type(RequestType.DUMMY_REQUEST_TYPE).build())
        );

        // Invoke
        RequestCreateValidationResult result = validatorService
                .validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(false)
                .reportedRequestTypes(Set.of(RequestType.DUMMY_REQUEST_TYPE)).build());

        verify(accountQueryService, times(1)).getAccountStatus(accountId);
        verify(requestQueryService, times(1)).findInProgressRequestsByAccount(accountId);
    }
}
