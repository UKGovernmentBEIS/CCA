package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetGenerateRequestIdGeneratorTest {

    @InjectMocks
    private PerformanceDataSpreadsheetGenerateRequestIdGenerator generator;

    @Test
    void generate() {
        final RequestParams params = RequestParams.builder()
                .requestMetadata(PerformanceDataSpreadsheetGenerateRequestMetadata.builder()
                        .accountBusinessId("ADS_1-T00001")
                        .targetPeriodType(PerformanceDataTargetPeriodType.TP6)
                        .build())
                .build();

        // Invoke
        String result = generator.generate(params);

        // Verify
        assertThat(result).isEqualTo("ADS_1-T00001-TPRGN-TP6");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.PERFORMANCE_DATA_SPREADSHEET_GENERATE);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("TPRGN");
    }
}
