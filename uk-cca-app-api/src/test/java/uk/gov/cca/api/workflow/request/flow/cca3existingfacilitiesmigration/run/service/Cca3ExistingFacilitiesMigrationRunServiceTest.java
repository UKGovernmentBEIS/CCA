package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
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
class Cca3ExistingFacilitiesMigrationRunServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationRunService cca3ExistingFacilitiesMigrationRunService;

    @Mock
    private RequestService requestService;

    @Test
    void accountProcessingCompleted() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                .accountId(accountId)
                .build();

        final Cca3ExistingFacilitiesMigrationRunRequestPayload runRequestPayload = Cca3ExistingFacilitiesMigrationRunRequestPayload.builder()
                .accountStates(new HashMap<>())
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(runRequestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        cca3ExistingFacilitiesMigrationRunService.accountProcessingCompleted(requestId, accountId, accountState);

        // Verify
        assertThat(((Cca3ExistingFacilitiesMigrationRunRequestPayload) request.getPayload()).getAccountStates())
                .isNotEmpty()
                .containsAllEntriesOf(Map.of(accountId, accountState));
        verify(requestService, times(1)).findRequestById(requestId);
    }

    @Test
    void complete() {
        final String requestId = "requestId";

        final Map<Long, Cca3FacilityMigrationAccountState> accountStates = Map.of(
                1L, Cca3FacilityMigrationAccountState.builder().succeeded(true).build(),
                2L, Cca3FacilityMigrationAccountState.builder().succeeded(true).build()
        );
        final Cca3ExistingFacilitiesMigrationRunRequestPayload payload = Cca3ExistingFacilitiesMigrationRunRequestPayload.builder()
                .accountStates(accountStates)
                .build();
        final Cca3ExistingFacilitiesMigrationRunRequestMetadata metadata = Cca3ExistingFacilitiesMigrationRunRequestMetadata.builder().build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(payload)
                .metadata(metadata)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        cca3ExistingFacilitiesMigrationRunService.complete(requestId);

        // Verify
        assertThat(metadata.getAccountStates()).isEqualTo(accountStates);
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoMoreInteractions(requestService);
    }

    @Test
    void complete_with_failures() {
        final String requestId = "requestId";

        final Map<Long, Cca3FacilityMigrationAccountState> accountStates = Map.of(
                1L, Cca3FacilityMigrationAccountState.builder().succeeded(false).build(),
                2L, Cca3FacilityMigrationAccountState.builder().succeeded(true).build()
        );
        final Cca3ExistingFacilitiesMigrationRunRequestPayload payload = Cca3ExistingFacilitiesMigrationRunRequestPayload.builder()
                .accountStates(accountStates)
                .build();
        final Cca3ExistingFacilitiesMigrationRunRequestMetadata metadata = Cca3ExistingFacilitiesMigrationRunRequestMetadata.builder().build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(payload)
                .metadata(metadata)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        cca3ExistingFacilitiesMigrationRunService.complete(requestId);

        // Verify
        assertThat(metadata.getAccountStates()).isEqualTo(accountStates);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
    }
}
