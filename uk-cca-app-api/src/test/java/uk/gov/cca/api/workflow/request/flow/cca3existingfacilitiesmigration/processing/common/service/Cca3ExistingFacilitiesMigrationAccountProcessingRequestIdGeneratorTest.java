package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingRequestIdGeneratorTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingRequestIdGenerator generator;

    @Test
    void generate() {
        final RequestParams params = RequestParams.builder()
                .requestMetadata(Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING)
                        .accountBusinessId("AIC-T0041")
                        .parentRequestId("CCA3-EFM-10")
                        .build())
                .build();

        // Invoke
        String result = generator.generate(params);

        // Verify
        assertThat(result).isEqualTo("AIC-T0041-CCA3-EFM-10");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEmpty();
    }
}
