package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusAccountProcessingRequestIdGeneratorTest {

    @InjectMocks
    private BuyOutSurplusAccountProcessingRequestIdGenerator generator;

    @Test
    void generate() {
        final RequestParams params = RequestParams.builder()
                .requestMetadata(BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING)
                        .parentRequestId("BS-TP6010")
                        .accountBusinessId("AIC-T0041")
                        .build())
                .build();

        // Invoke
        String result = generator.generate(params);

        // Verify
        assertThat(result).isEqualTo("AIC-T0041-BS-TP6010");
    }

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactlyInAnyOrder(
        		CcaRequestType.BUY_OUT_SURPLUS_ACCOUNT_PROCESSING,
        		CcaRequestType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEmpty();
    }
}
