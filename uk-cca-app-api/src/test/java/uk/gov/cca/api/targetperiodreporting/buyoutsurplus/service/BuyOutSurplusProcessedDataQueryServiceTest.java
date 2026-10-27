package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    void getAccountBuyOutSurplusProcessedDataByPerformanceData() {
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
        Optional<Long> result = buyOutSurplusProcessedDataQueryService
                .getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);

        // Verify
        assertThat(result).isPresent().contains(expected);
        verify(buyOutSurplusProcessedDataRepository, times(1))
                .findByPerformanceDataIdAndPerformanceDataResourceType(performanceDataId, resourceType);
    }

    @Test
    void getFacilitiesBuyOutSurplusProcessedData() {
        final Set<Long> facilityIds = Set.of(1L);
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        final FacilityBuyOutSurplusProcessedDataInfo info = FacilityBuyOutSurplusProcessedDataInfo.builder()
                .facilityId(1L)
                .performanceDataId(22L)
                .build();

        when(buyOutSurplusProcessedDataRepository.findLatestBuyOutSurplusProcessedFacilitiesPrimaryPerformanceDataByTargetPeriodType(facilityIds, targetPeriodType))
                .thenReturn(List.of(info));

        // Invoke
        List<FacilityBuyOutSurplusProcessedDataInfo> result = buyOutSurplusProcessedDataQueryService
                .getFacilitiesBuyOutSurplusProcessedData(facilityIds, targetPeriodType);

        // Verify
        assertThat(result).contains(info);
        verify(buyOutSurplusProcessedDataRepository, times(1))
                .findLatestBuyOutSurplusProcessedFacilitiesPrimaryPerformanceDataByTargetPeriodType(facilityIds, targetPeriodType);
    }
}
