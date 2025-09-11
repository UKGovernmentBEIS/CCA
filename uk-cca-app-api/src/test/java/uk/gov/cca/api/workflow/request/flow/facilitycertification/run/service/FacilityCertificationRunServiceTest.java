package uk.gov.cca.api.workflow.request.flow.facilitycertification.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunSummary;
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
class FacilityCertificationRunServiceTest {

    @InjectMocks
    private FacilityCertificationRunService facilityCertificationRunService;

    @Mock
    private RequestService requestService;

    @Test
    void submit() {
        final String requestId = "requestId";

        final Request request = Request.builder().id(requestId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        facilityCertificationRunService.submit(requestId);

        // Verify
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestService, times(1)).findRequestById(requestId);
    }

    @Test
    void accountProcessingCompleted() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder().accountId(accountId).build();

        final FacilityCertificationRunRequestPayload runRequestPayload = FacilityCertificationRunRequestPayload.builder()
                .facilityCertificationAccountStates(new HashMap<>())
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(runRequestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        facilityCertificationRunService.accountProcessingCompleted(requestId, accountId, accountState);

        // Verify
        assertThat(((FacilityCertificationRunRequestPayload) request.getPayload()).getFacilityCertificationAccountStates())
                .isNotEmpty()
                .containsAllEntriesOf(Map.of(1L, accountState));
        verify(requestService, times(1)).findRequestById(requestId);
    }

    @Test
    void complete() {
        final String requestId = "requestId";

        final FacilityCertificationRunRequestPayload payload = FacilityCertificationRunRequestPayload.builder()
                .facilityCertificationAccountStates(Map.of(
                        1L, FacilityCertificationAccountState.builder().succeeded(true).facilitiesCertified(5L).build(),
                        2L, FacilityCertificationAccountState.builder().succeeded(true).facilitiesCertified(10L).build()
                ))
                .build();
        final FacilityCertificationRunRequestMetadata metadata = FacilityCertificationRunRequestMetadata.builder().build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(payload)
                .metadata(metadata)
                .build();
        final FacilityCertificationRunSummary runSummary = FacilityCertificationRunSummary.builder()
                .totalAccounts(2L)
                .failedAccounts(0L)
                .facilitiesCertified(15L)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        facilityCertificationRunService.complete(requestId);

        // Verify
        assertThat(((FacilityCertificationRunRequestPayload) request.getPayload()).getRunSummary()).isEqualTo(runSummary);
        assertThat(((FacilityCertificationRunRequestMetadata) request.getMetadata()).getTotalAccounts()).isEqualTo(runSummary.getTotalAccounts());
        assertThat(((FacilityCertificationRunRequestMetadata) request.getMetadata()).getFailedAccounts()).isEqualTo(runSummary.getFailedAccounts());
        assertThat(((FacilityCertificationRunRequestMetadata) request.getMetadata()).getFacilitiesCertified()).isEqualTo(runSummary.getFacilitiesCertified());
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void complete_with_failures() {
        final String requestId = "requestId";

        final FacilityCertificationRunRequestPayload payload = FacilityCertificationRunRequestPayload.builder()
                .facilityCertificationAccountStates(Map.of(
                        1L, FacilityCertificationAccountState.builder().succeeded(false).facilitiesCertified(0L).build(),
                        2L, FacilityCertificationAccountState.builder().succeeded(true).facilitiesCertified(10L).build()
                ))
                .build();
        final FacilityCertificationRunRequestMetadata metadata = FacilityCertificationRunRequestMetadata.builder().build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(payload)
                .metadata(metadata)
                .build();
        final FacilityCertificationRunSummary runSummary = FacilityCertificationRunSummary.builder()
                .totalAccounts(2L)
                .failedAccounts(1L)
                .facilitiesCertified(10L)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        facilityCertificationRunService.complete(requestId);

        // Verify
        assertThat(((FacilityCertificationRunRequestPayload) request.getPayload()).getRunSummary()).isEqualTo(runSummary);
        assertThat(((FacilityCertificationRunRequestMetadata) request.getMetadata()).getTotalAccounts()).isEqualTo(runSummary.getTotalAccounts());
        assertThat(((FacilityCertificationRunRequestMetadata) request.getMetadata()).getFailedAccounts()).isEqualTo(runSummary.getFailedAccounts());
        assertThat(((FacilityCertificationRunRequestMetadata) request.getMetadata()).getFacilitiesCertified()).isEqualTo(runSummary.getFacilitiesCertified());
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
    }
}
