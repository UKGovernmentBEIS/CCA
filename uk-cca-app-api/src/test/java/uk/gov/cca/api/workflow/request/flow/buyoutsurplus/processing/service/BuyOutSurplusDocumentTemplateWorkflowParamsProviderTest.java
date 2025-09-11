package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private BuyOutSurplusDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(CcaDocumentTemplateGenerationContextActionType.BUY_OUT_SURPLUS_NOTICE);
    }

    @Test
    void constructParams() {
        final LocalDateTime submissionDate = LocalDateTime.of(2025, 5, 5, 12, 0);
        final BuyOutSurplusResult buyOutSurplus = BuyOutSurplusResult.builder().build();
        final BuyOutSurplusAccountProcessingRequestPayload payload = BuyOutSurplusAccountProcessingRequestPayload.builder()
                .buyOutSurplus(buyOutSurplus)
                .performanceData(PerformanceDataBuyOutSurplusDetailsDTO.builder()
                        .submissionDate(submissionDate)
                        .build())
                .build();

        // Invoke
        final Map<String, Object> result = provider.constructParams(payload);

        // Verify
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "buyOut", buyOutSurplus,
                "submissionDate", submissionDate
        ));
    }
}
