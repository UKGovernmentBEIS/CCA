package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType.EXISTING_FACILITIES_MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingActivatedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivatedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void activateMigratedUnderlyingAgreement() {
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();

        // Invoke
        Map<String, Object> result = provider.constructParams(payload);

        // Verify
        assertThat(result).containsExactlyEntriesOf(Map.of("version", "v1"));
    }

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType())
                .isEqualTo(EXISTING_FACILITIES_MIGRATION_UNDERLYING_AGREEMENT_ACTIVATED_CCA3);
    }
}
