package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountBuyOutSurplusProcessedDataServiceTest {

    @InjectMocks
    private AccountBuyOutSurplusProcessedDataService accountBuyOutSurplusProcessedDataService;

    @Mock
    private BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    @Test
    void getBuyOutSurplusProcessedData() {
        final Long performanceDataId = 1L;
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.ACCOUNT;
        final BuyOutSurplusProcessedData entity = BuyOutSurplusProcessedData.builder()
                .id(22L)
                .performanceDataId(performanceDataId)
                .creationDate(LocalDateTime.of(2025, 4, 1, 12, 22, 44))
                .build();

        final Long expected = 22L;

        when(buyOutSurplusProcessedDataRepository.findByPerformanceDataIdAndPerformanceDataResourceType(performanceDataId, resourceType))
                .thenReturn(Optional.of(entity));

        // Invoke
        Optional<Long> result = accountBuyOutSurplusProcessedDataService.getBuyOutSurplusProcessedData(performanceDataId);

        // Verify
        assertThat(result).isPresent().contains(expected);
        verify(buyOutSurplusProcessedDataRepository, times(1))
                .findByPerformanceDataIdAndPerformanceDataResourceType(performanceDataId, resourceType);
    }

    @Test
    void submitBuyOutSurplusProcessedData() {
        final long performanceDataId = 1L;

        final BuyOutSurplusProcessedData entity = BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .build();

        // Invoke
        accountBuyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedData(performanceDataId);

        // Verify
        verify(buyOutSurplusProcessedDataRepository, times(1)).save(entity);
    }

    @Test
    void submitBuyOutSurplusProcessedDataBatch() {
        final long performanceDataId = 1L;

        final BuyOutSurplusProcessedData entities = BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .build();

        // Invoke
        accountBuyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedDataBatch(Set.of(performanceDataId));

        // Verify
        verify(buyOutSurplusProcessedDataRepository, times(1)).saveAll(List.of(entities));
    }

    @Test
    void submitBuyOutSurplusProcessedDataBatch_shouldDoNothingWhenPerformanceDataIdsAreEmpty() {
        // Invoke
        accountBuyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedDataBatch(Set.of());

        // Verify
        verifyNoInteractions(buyOutSurplusProcessedDataRepository);
    }

    @Test
    void getResourceType() {
        assertThat(accountBuyOutSurplusProcessedDataService.getResourceType()).isEqualTo(PerformanceDataResourceType.ACCOUNT);
    }
}
