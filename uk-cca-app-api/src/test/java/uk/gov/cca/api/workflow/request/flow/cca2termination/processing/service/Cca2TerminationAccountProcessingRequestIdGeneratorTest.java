package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain.Cca2TerminationAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationAccountProcessingRequestIdGeneratorTest {

	@InjectMocks
    private Cca2TerminationAccountProcessingRequestIdGenerator generator;

    @Test
    void generate() {
        final RequestParams params = RequestParams.builder()
                .requestMetadata(Cca2TerminationAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_TERMINATION_ACCOUNT_PROCESSING)
                        .parentRequestId("CCA2-END-1")
                        .accountBusinessId("AIC-T0041")
                        .build())
                .build();

        // Invoke
        String result = generator.generate(params);

        // Verify
        assertThat(result).isEqualTo("AIC-T0041-CCA2-END-1");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.CCA2_TERMINATION_ACCOUNT_PROCESSING);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEmpty();
    }
}
