package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingRequestIdGeneratorTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingRequestIdGenerator generator;

    @Test
    void generate() {
        final RequestParams params = RequestParams.builder()
                .requestMetadata(Cca2ExtensionNoticeAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING)
                        .accountBusinessId("AIC-T0041")
                        .parentRequestId("CCA2-EXT-10")
                        .build())
                .build();

        // Invoke
        String result = generator.generate(params);

        // Verify
        assertThat(result).isEqualTo("AIC-T0041-CCA2-EXT-10");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEmpty();
    }
}
