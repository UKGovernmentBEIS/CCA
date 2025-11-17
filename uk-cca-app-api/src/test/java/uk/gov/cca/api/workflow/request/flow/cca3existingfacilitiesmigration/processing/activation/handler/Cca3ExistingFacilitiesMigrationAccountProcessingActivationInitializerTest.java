package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingActivationInitializerTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationInitializer initializer;

    @Test
    void initializePayload() {
        final Request request = Request.builder()
                .id("ADS_1-T00001-UNA")
                .build();

        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload expected =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_PAYLOAD)
                        .build();
        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION);
    }
}
