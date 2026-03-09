package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationAccountProcessingCompletedServiceTest {

	@InjectMocks
    private Cca2TerminationAccountProcessingCompletedService cca2TerminationAccountProcessingCompletedService;

    @Mock
    private RequestService requestService;
   
    @Test
    void completed() {
        final String requestId = "requestId";
        final String parentRequestId = "parentRequestId";
        final Long accountId = 1L;
        final Cca2TerminationAccountState accountState = Cca2TerminationAccountState.builder().accountId(accountId).build();

        final Cca2TerminationRunRequestMetadata metadata = Cca2TerminationRunRequestMetadata.builder()
                .cca2TerminationAccountStates(new HashMap<>())
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .metadata(metadata)
                .build();

        when(requestService.findRequestByIdForUpdate(parentRequestId)).thenReturn(request);

        // Invoke
        cca2TerminationAccountProcessingCompletedService.completed(parentRequestId, requestId, accountId, accountState);

        // Verify
        assertThat(((Cca2TerminationRunRequestMetadata) request.getMetadata()).getCca2TerminationAccountStates())
                .isNotEmpty()
                .containsAllEntriesOf(Map.of(1L, accountState));
        verify(requestService, times(1)).findRequestByIdForUpdate(parentRequestId);
    }
}
