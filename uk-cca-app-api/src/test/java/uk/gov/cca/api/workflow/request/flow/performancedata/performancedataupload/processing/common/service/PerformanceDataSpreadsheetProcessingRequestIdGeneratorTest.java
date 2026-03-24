package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetProcessingRequestIdGeneratorTest {

    @InjectMocks
    private PerformanceDataSpreadsheetProcessingRequestIdGenerator generator;

    @Test
    void generate() {
        final RequestParams params = RequestParams.builder()
                .requestMetadata(PerformanceDataSpreadsheetProcessingRequestMetadata.builder()
                        .accountBusinessId("ADS_1-T00001")
                        .targetPeriodDetails(TargetPeriodYearDTO.builder()
                                .businessId(TargetPeriodType.TP6)
                                .build())
                        .reportVersion(1)
                        .build())
                .build();

        // Invoke
        String result = generator.generate(params);

        // Verify
        assertThat(result).isEqualTo("ADS_1-T00001-TPR-TP6-V1");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("TPR");
    }
}
