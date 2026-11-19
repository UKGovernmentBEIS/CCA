package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleFacilitiesCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityBuyOutSurplusProcessedDataServiceTest {

    @InjectMocks
    private FacilityBuyOutSurplusProcessedDataService facilityBuyOutSurplusProcessedDataService;

    @Mock
    protected BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private BuyOutSurplusEligibleFacilitiesCustomRepository buyOutSurplusEligibleFacilitiesCustomRepository;

    @Test
    void getBuyOutSurplusProcessedData() {
        final Long performanceDataId = 1L;
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.FACILITY;
        final BuyOutSurplusProcessedData entity = BuyOutSurplusProcessedData.builder()
                .id(22L)
                .performanceDataId(performanceDataId)
                .creationDate(LocalDateTime.of(2025, 4, 1, 12, 22, 44))
                .build();

        final Long expected = 22L;

        when(buyOutSurplusProcessedDataRepository.findByPerformanceDataIdAndPerformanceDataResourceType(performanceDataId, resourceType))
                .thenReturn(Optional.of(entity));

        // Invoke
        Optional<Long> result = facilityBuyOutSurplusProcessedDataService.getBuyOutSurplusProcessedData(performanceDataId);

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
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .build();

        // Invoke
        facilityBuyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedData(performanceDataId);

        // Verify
        verify(buyOutSurplusProcessedDataRepository, times(1)).save(entity);
    }

    @Test
    void submitBuyOutSurplusProcessedDataBatch() {
        final long performanceDataId = 1L;

        final BuyOutSurplusProcessedData entities = BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .build();

        // Invoke
        facilityBuyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedDataBatch(Set.of(performanceDataId));

        // Verify
        verify(buyOutSurplusProcessedDataRepository, times(1)).saveAll(List.of(entities));
    }

    @Test
    void getLatestBuyOutSurplusProcessedPrimaryPerformanceData_for_facilityIds() {
        final Set<Long> facilityIds = Set.of(1L);
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        final FacilityBuyOutSurplusProcessedDataInfo info = FacilityBuyOutSurplusProcessedDataInfo.builder()
                .facilityId(1L)
                .performanceDataId(22L)
                .build();

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(TargetPeriodInfoDTO.builder().schemeVersion(SchemeVersion.CCA_3).build());
        when(buyOutSurplusProcessedDataRepository.findLatestBuyOutSurplusProcessedFacilitiesPrimaryPerformanceDataByTargetPeriodType(facilityIds, targetPeriodType))
                .thenReturn(List.of(info));

        // Invoke
        List<FacilityBuyOutSurplusProcessedDataInfo> result = facilityBuyOutSurplusProcessedDataService
                .getLatestBuyOutSurplusProcessedPrimaryPerformanceData(facilityIds, targetPeriodType);

        // Verify
        assertThat(result).contains(info);
        verify(targetPeriodService, times(1))
                .getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(buyOutSurplusProcessedDataRepository, times(1))
                .findLatestBuyOutSurplusProcessedFacilitiesPrimaryPerformanceDataByTargetPeriodType(facilityIds, targetPeriodType);
    }

    @Test
    void shouldGroupPerformanceDataByFacilityAndTargetPeriod() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = List.of(1L, 2L, 3L);

        when(buyOutSurplusEligibleFacilitiesCustomRepository
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds))
                .thenReturn(List.of(
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP7),
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP8),
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP9),
                        mockPerformanceData("FACILITY-2", TargetPeriodType.TP9)
                ));

        // Invoke
        Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> result =
                facilityBuyOutSurplusProcessedDataService.getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, targetPeriodIds);

        // Verify
        assertThat(result).hasSize(2).containsKeys("FACILITY-1", "FACILITY-2");
        assertThat(result.get("FACILITY-1")).containsKeys(
                TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9);
        verify(buyOutSurplusEligibleFacilitiesCustomRepository)
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds);
    }

    @Test
    void shouldReturnEmptyMapWhenRepositoryReturnsNoResults() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = List.of(1L, 2L, 3L);

        when(buyOutSurplusEligibleFacilitiesCustomRepository
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds))
                .thenReturn(List.of());

        // Invoke
        Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> result =
                facilityBuyOutSurplusProcessedDataService.getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, targetPeriodIds);

        // Verify
        assertThat(result).isEmpty();
        verify(buyOutSurplusEligibleFacilitiesCustomRepository)
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds);
    }

    @Test
    void shouldOrderTargetPeriodsByNumber() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = List.of(1L, 2L, 3L);

        when(buyOutSurplusEligibleFacilitiesCustomRepository
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds))
                .thenReturn(List.of(
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP8),
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP7),
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP9)));

        // Invoke
        Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> result =
                facilityBuyOutSurplusProcessedDataService.getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, targetPeriodIds);

        // Verify
        assertThat(result.get("FACILITY-1").keySet()).containsExactly(
                TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9);
        verify(buyOutSurplusEligibleFacilitiesCustomRepository)
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds);
    }

    @Test
    void getResourceType() {
        assertThat(facilityBuyOutSurplusProcessedDataService.getResourceType()).isEqualTo(PerformanceDataResourceType.FACILITY);
    }

    private FacilityPerformanceDataWithBuyOutProcessedStatus mockPerformanceData(String facilityBusinessId, TargetPeriodType targetPeriodType) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(targetPeriodType)
                .build();
    }
}
