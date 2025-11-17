package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeRunRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeRunServiceTest {

    @InjectMocks
    private Cca2ExtensionNoticeRunService cca2ExtensionNoticeRunService;

    @Mock
    private RequestService requestService;

    @Test
    void accountProcessingCompleted() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .build();

        final Cca2ExtensionNoticeRunRequestPayload runRequestPayload = Cca2ExtensionNoticeRunRequestPayload.builder()
                .accountStates(new HashMap<>())
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(runRequestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        cca2ExtensionNoticeRunService.accountProcessingCompleted(requestId, accountId, accountState);

        // Verify
        assertThat(((Cca2ExtensionNoticeRunRequestPayload) request.getPayload()).getAccountStates())
                .isNotEmpty()
                .containsAllEntriesOf(Map.of(accountId, accountState));
        verify(requestService, times(1)).findRequestById(requestId);
    }

    @Test
    void complete() {
        final String requestId = "requestId";

        final Map<Long, Cca2ExtensionNoticeAccountState> accountStates = Map.of(
                1L, Cca2ExtensionNoticeAccountState.builder().succeeded(true).build(),
                2L, Cca2ExtensionNoticeAccountState.builder().succeeded(true).build()
        );
        final Cca2ExtensionNoticeRunRequestPayload payload = Cca2ExtensionNoticeRunRequestPayload.builder()
                .accountStates(accountStates)
                .build();
        final Cca2ExtensionNoticeRunRequestMetadata metadata = Cca2ExtensionNoticeRunRequestMetadata.builder().build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(payload)
                .metadata(metadata)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        cca2ExtensionNoticeRunService.complete(requestId);

        // Verify
        assertThat(metadata.getAccountStates()).isEqualTo(accountStates);
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void complete_with_failures() {
        final String requestId = "requestId";

        final Map<Long, Cca2ExtensionNoticeAccountState> accountStates = Map.of(
                1L, Cca2ExtensionNoticeAccountState.builder().succeeded(false).build(),
                2L, Cca2ExtensionNoticeAccountState.builder().succeeded(true).build()
        );
        final Cca2ExtensionNoticeRunRequestPayload payload = Cca2ExtensionNoticeRunRequestPayload.builder()
                .accountStates(accountStates)
                .build();
        final Cca2ExtensionNoticeRunRequestMetadata metadata = Cca2ExtensionNoticeRunRequestMetadata.builder().build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(payload)
                .metadata(metadata)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        cca2ExtensionNoticeRunService.complete(requestId);

        // Verify
        assertThat(metadata.getAccountStates()).isEqualTo(accountStates);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
    }
}
