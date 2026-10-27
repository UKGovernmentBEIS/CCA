package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusProcessedDataServiceTest {

    @InjectMocks
    private BuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;

    @Mock
    private BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    @Test
    void submitBuyOutSurplusProcessedData() {
        final long performanceDataId = 1L;
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.ACCOUNT;

        final BuyOutSurplusProcessedData entity = BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .performanceDataResourceType(resourceType)
                .build();

        // Invoke
        buyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedData(performanceDataId, resourceType);

        // Verify
        verify(buyOutSurplusProcessedDataRepository, times(1)).save(entity);
    }
    
    @Test
    void shouldDoNothingWhenPerformanceDataIdsAreEmpty() {
        buyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedDataBatch(
                Set.of(),
                PerformanceDataResourceType.FACILITY
        );

        verifyNoInteractions(buyOutSurplusProcessedDataRepository);
    }
    
    @Test
    void submitBuyOutSurplusProcessedDataBatch() {
        final long performanceDataId = 1L;
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.FACILITY;

        final BuyOutSurplusProcessedData entities = BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .performanceDataResourceType(resourceType)
                .build();

        // Invoke
        buyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedDataBatch(Set.of(performanceDataId), resourceType);

        // Verify
        verify(buyOutSurplusProcessedDataRepository, times(1)).saveAll(List.of(entities));
    }
}
