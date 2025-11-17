package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationRunRequestIdGeneratorTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationRunRequestIdGenerator generator;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void resolveRequestSequence() {
        final RequestParams params = RequestParams.builder()
                .type(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN)
                .build();

        final RequestType type = RequestType.builder().code(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN).build();

        when(requestTypeRepository.findByCode(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN)).thenReturn(Optional.of(type));

        // Invoke
        generator.resolveRequestSequence(params);

        // Verify
        verify(requestTypeRepository, times(1)).findByCode(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN);
        verify(requestSequenceRepository, times(1)).findByRequestType(type);
    }

    @Test
    void generateRequestId() {
        final RequestParams params = RequestParams.builder()
                .type(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN)
                .build();

        // Invoke
        String result = generator.generateRequestId(10L, params);

        // Verify
        assertThat(result).isEqualTo("CCA3-EFM-10");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("CCA3-EFM");
    }
}
