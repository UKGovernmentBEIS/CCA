package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusProcessedDataQueryServiceTest {

    @InjectMocks
    private BuyOutSurplusProcessedDataQueryService buyOutSurplusProcessedDataQueryService;

    @Mock
    private BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    @Test
    void getBuyOutSurplusProcessedDataByPerformanceData() {
        final Long performanceDataId = 1L;
        final BuyOutSurplusProcessedData entity = BuyOutSurplusProcessedData.builder()
                .id(22L)
                .performanceDataId(performanceDataId)
                .creationDate(LocalDateTime.of(2025, 4, 1, 12, 22, 44))
                .build();

        final Long expected = 22L;

        when(buyOutSurplusProcessedDataRepository.findByPerformanceDataId(performanceDataId))
                .thenReturn(Optional.of(entity));

        // Invoke
        Optional<Long> result = buyOutSurplusProcessedDataQueryService
                .getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);

        // Verify
        assertThat(result).isPresent().contains(expected);
        verify(buyOutSurplusProcessedDataRepository, times(1))
                .findByPerformanceDataId(performanceDataId);
    }
}
