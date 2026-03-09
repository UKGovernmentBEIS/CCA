package uk.gov.cca.api.workflow.request.flow.cca2termination.run.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain.Cca2TerminationRunRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class Cca2TerminationRunRequestIdGeneratorTest {

	@InjectMocks
    private Cca2TerminationRunRequestIdGenerator generator;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void resolveRequestSequence() {
        final Cca2TerminationRunRequestMetadata metadata = Cca2TerminationRunRequestMetadata.builder()
                .type(CcaRequestMetadataType.CCA2_TERMINATION_RUN)
                .build();
        final RequestParams params = RequestParams.builder()
                .requestMetadata(metadata)
                .type(CcaRequestType.CCA2_TERMINATION_RUN)
                .build();

        final RequestType type = RequestType.builder().code(CcaRequestType.CCA2_TERMINATION_RUN).build();

        when(requestTypeRepository.findByCode(CcaRequestType.CCA2_TERMINATION_RUN)).thenReturn(Optional.of(type));

        // Invoke
        generator.resolveRequestSequence(params);

        // Verify
        verify(requestTypeRepository, times(1)).findByCode(CcaRequestType.CCA2_TERMINATION_RUN);
        verify(requestSequenceRepository, times(1)).findByRequestType(type);
    }

    @Test
    void generateRequestId() {
        final Cca2TerminationRunRequestMetadata metadata = Cca2TerminationRunRequestMetadata.builder()
                .type(CcaRequestMetadataType.CCA2_TERMINATION_RUN)
                .build();
        final RequestParams params = RequestParams.builder()
                .requestMetadata(metadata)
                .type(CcaRequestType.CCA2_TERMINATION_RUN)
                .build();

        // Invoke
        String result = generator.generateRequestId(10L, params);

        // Verify
        assertThat(result).isEqualTo("CCA2-END-10");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.CCA2_TERMINATION_RUN);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("CCA2-END");
    }
}
