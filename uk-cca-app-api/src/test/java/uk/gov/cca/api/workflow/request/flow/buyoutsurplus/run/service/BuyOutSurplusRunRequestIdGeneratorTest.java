package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
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
class BuyOutSurplusRunRequestIdGeneratorTest {

    @InjectMocks
    private BuyOutSurplusRunRequestIdGenerator generator;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Mock
    private RequestSequenceRepository requestSequenceRepository;

    @Test
    void resolveRequestSequence() {
        final BuyOutSurplusRunRequestMetadata metadata = BuyOutSurplusRunRequestMetadata.builder()
                .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_RUN)
                .targetPeriodType(TargetPeriodType.TP6)
                .build();
        final RequestParams params = RequestParams.builder()
                .requestMetadata(metadata)
                .type(CcaRequestType.BUY_OUT_SURPLUS_RUN)
                .build();

        final RequestType type = RequestType.builder().code(CcaRequestType.BUY_OUT_SURPLUS_RUN).build();

        when(requestTypeRepository.findByCode(CcaRequestType.BUY_OUT_SURPLUS_RUN)).thenReturn(Optional.of(type));

        // Invoke
        generator.resolveRequestSequence(params);

        // Verify
        verify(requestTypeRepository, times(1)).findByCode(CcaRequestType.BUY_OUT_SURPLUS_RUN);
        verify(requestSequenceRepository, times(1))
                .findByBusinessIdentifierAndRequestType(TargetPeriodType.TP6.name(), type);
    }

    @Test
    void generateRequestId() {
        final BuyOutSurplusRunRequestMetadata metadata = BuyOutSurplusRunRequestMetadata.builder()
                .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_RUN)
                .targetPeriodType(TargetPeriodType.TP6)
                .build();
        final RequestParams params = RequestParams.builder()
                .requestMetadata(metadata)
                .type(CcaRequestType.BUY_OUT_SURPLUS_RUN)
                .build();

        // Invoke
        String result = generator.generateRequestId(10L, params);

        // Verify
        assertThat(result).isEqualTo("BOS-TP6010");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.BUY_OUT_SURPLUS_RUN);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("BOS");
    }
}
