package uk.gov.cca.api.workflow.request.flow.facilitycertification.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestMetadata;
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
class FacilityCertificationRunRequestIdGeneratorTest {

    @InjectMocks
    private FacilityCertificationRunRequestIdGenerator generator;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void resolveRequestSequence() {
        final FacilityCertificationRunRequestMetadata metadata = FacilityCertificationRunRequestMetadata.builder()
                .type(CcaRequestMetadataType.FACILITY_CERTIFICATION_RUN)
                .certificationPeriodType(CertificationPeriodType.CP7)
                .build();
        final RequestParams params = RequestParams.builder()
                .requestMetadata(metadata)
                .type(CcaRequestType.FACILITY_CERTIFICATION_RUN)
                .build();

        final RequestType type = RequestType.builder().code(CcaRequestType.FACILITY_CERTIFICATION_RUN).build();

        when(requestTypeRepository.findByCode(CcaRequestType.FACILITY_CERTIFICATION_RUN)).thenReturn(Optional.of(type));

        // Invoke
        generator.resolveRequestSequence(params);

        // Verify
        verify(requestTypeRepository, times(1)).findByCode(CcaRequestType.FACILITY_CERTIFICATION_RUN);
        verify(requestSequenceRepository, times(1))
                .findByBusinessIdentifierAndRequestType(CertificationPeriodType.CP7.name(), type);
    }

    @Test
    void generateRequestId() {
        final FacilityCertificationRunRequestMetadata metadata = FacilityCertificationRunRequestMetadata.builder()
                .type(CcaRequestMetadataType.FACILITY_CERTIFICATION_RUN)
                .certificationPeriodType(CertificationPeriodType.CP7)
                .build();
        final RequestParams params = RequestParams.builder()
                .requestMetadata(metadata)
                .type(CcaRequestType.FACILITY_CERTIFICATION_RUN)
                .build();

        // Invoke
        String result = generator.generateRequestId(10L, params);

        // Verify
        assertThat(result).isEqualTo("CRT-CP7010");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.FACILITY_CERTIFICATION_RUN);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("CRT");
    }
}
