package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusProcessedDataServiceTest {

    @InjectMocks
    private BuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;

    @Mock
    private BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    @Test
    void submitBuyOutSurplusProcessedData() {
        final long performanceDataId = 1L;

        final BuyOutSurplusProcessedData entity = BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .build();

        // Invoke
        buyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedData(performanceDataId);

        // Verify
        verify(buyOutSurplusProcessedDataRepository, times(1)).save(entity);
    }
}
