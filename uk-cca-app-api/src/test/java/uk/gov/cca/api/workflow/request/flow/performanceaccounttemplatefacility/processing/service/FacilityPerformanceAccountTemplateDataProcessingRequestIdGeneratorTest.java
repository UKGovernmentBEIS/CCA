package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataProcessingRequestIdGeneratorTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataProcessingRequestIdGenerator generator;

    @Test
    void generateRequestId() {
        final Long sequenceNo = 2L;
        final RequestParams params = RequestParams.builder()
                .requestPayload(FacilityPerformanceAccountTemplateDataProcessingRequestPayload.builder()
                        .sectorAssociationInfo(SectorAssociationInfo.builder()
                                .acronym("ADS_1")
                                .build())
                        .build())
                .build();

        // Invoke
        String result = generator.generateRequestId(sequenceNo, params);

        assertThat(result).isEqualTo("ADS_1-PATFPC-2");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_PROCESSING);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("PATFPC");
    }
}
