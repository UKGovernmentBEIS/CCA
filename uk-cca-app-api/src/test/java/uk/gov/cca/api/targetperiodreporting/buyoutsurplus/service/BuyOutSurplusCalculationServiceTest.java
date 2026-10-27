package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusCalculationRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusCalculationServiceTest {

    @InjectMocks
    private BuyOutSurplusCalculationService service;

    @Mock
    private BuyOutSurplusCalculationRepository buyOutSurplusCalculationRepository;
    
    @Mock
    private FacilityDataQueryService facilityDataQueryService;
    
    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void getLatestBuyOutCalculationsPerFacilityAndTargetPeriod() {
        final Long accountId = 1L;
        final List<BuyOutSurplusCalculation> buyOutSurplusCalculations = List.of(
                BuyOutSurplusCalculation.builder()
                        .facilityId(1L)
                        .accountId(1L)
                        .performanceDataId(1L)
                        .targetPeriod(getTargetPeriodByBusinessId(TargetPeriodType.TP7))
                        .creationDate(LocalDate.of(2027, 1, 1).atStartOfDay())
                        .data(BuyOutSurplusCalculationDataContainer.builder()
                                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                                .buyOutRequired(BigDecimal.valueOf(252))
                                .surplusGained(BigDecimal.ZERO)
                                .invoicedBuyOutFee(BigDecimal.ZERO)
                                .previousSurplusGained(BigDecimal.ZERO)
                                .previousSurplusUsed(BigDecimal.ZERO)
                                .surplusUsed(BigDecimal.valueOf(100))
                                .previousPaidFees(BigDecimal.ZERO)
                                .schemeTotalSurplus(BigDecimal.ZERO)
                                .chargeType(null)
                                .build())
                        .build(),
                BuyOutSurplusCalculation.builder()
                        .facilityId(1L)
                        .accountId(1L)
                        .performanceDataId(2L)
                        .targetPeriod(getTargetPeriodByBusinessId(TargetPeriodType.TP7))
                        .creationDate(LocalDate.of(2027, 1, 4).atStartOfDay())
                        .data(BuyOutSurplusCalculationDataContainer.builder()
                                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                                .buyOutRequired(BigDecimal.valueOf(252))
                                .surplusGained(BigDecimal.ZERO)
                                .invoicedBuyOutFee(BigDecimal.ZERO)
                                .previousSurplusGained(BigDecimal.ZERO)
                                .previousSurplusUsed(BigDecimal.ZERO)
                                .surplusUsed(BigDecimal.valueOf(100))
                                .previousPaidFees(BigDecimal.ZERO)
                                .schemeTotalSurplus(BigDecimal.ZERO)
                                .chargeType(null)
                                .build())
                        .build(),
                BuyOutSurplusCalculation.builder()
                        .facilityId(2L)
                        .accountId(1L)
                        .performanceDataId(3L)
                        .targetPeriod(getTargetPeriodByBusinessId(TargetPeriodType.TP7))
                        .creationDate(LocalDate.of(2027, 1, 1).atStartOfDay())
                        .data(BuyOutSurplusCalculationDataContainer.builder()
                                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                                .buyOutRequired(BigDecimal.valueOf(252))
                                .surplusGained(BigDecimal.ZERO)
                                .invoicedBuyOutFee(BigDecimal.ZERO)
                                .previousSurplusGained(BigDecimal.ZERO)
                                .previousSurplusUsed(BigDecimal.ZERO)
                                .surplusUsed(BigDecimal.valueOf(100))
                                .previousPaidFees(BigDecimal.ZERO)
                                .schemeTotalSurplus(BigDecimal.ZERO)
                                .chargeType(null)
                                .build())
                        .build());

        final BuyOutSurplusCalculationDetail expectedResult1 = BuyOutSurplusCalculationDetail.builder()
                .facilityId(1L)
                .accountId(1L)
                .performanceDataId(2L)
                .targetPeriodId(getTargetPeriodByBusinessId(TargetPeriodType.TP7).getId())
                .targetPeriodType(TargetPeriodType.TP7)
                .creationDate(LocalDate.of(2027, 1, 4).atStartOfDay())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.valueOf(252))
                        .surplusGained(BigDecimal.ZERO)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.valueOf(100))
                        .previousPaidFees(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(null)
                        .build())
                .build();

        final BuyOutSurplusCalculationDetail expectedResult2 = BuyOutSurplusCalculationDetail.builder()
                .facilityId(2L)
                .accountId(1L)
                .performanceDataId(3L)
                .targetPeriodId(getTargetPeriodByBusinessId(TargetPeriodType.TP7).getId())
                .targetPeriodType(TargetPeriodType.TP7)
                .creationDate(LocalDate.of(2027, 1, 1).atStartOfDay())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                    .buyOutRequired(BigDecimal.valueOf(252))
                    .surplusGained(BigDecimal.ZERO)
                    .invoicedBuyOutFee(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(100))
                    .previousPaidFees(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .chargeType(null)
                    .build())
                .build();
        
        List<FacilityBaseInfoDTO> facilities = List.of(
                FacilityBaseInfoDTO.builder().id(1L).facilityBusinessId("facilityBusinessId1").build(),
                FacilityBaseInfoDTO.builder().id(2L).facilityBusinessId("facilityBusinessId2").build());

        when(buyOutSurplusCalculationRepository.findAllByAccountIdAndTargetPeriodIdIn(accountId, List.of(1L))).thenReturn(buyOutSurplusCalculations);
        when(facilityDataQueryService.getFacilitiesByAccountId(accountId)).thenReturn(facilities);

        // invoke
        Map<String, Map<TargetPeriodType, BuyOutSurplusCalculationDetail>> result = service.getLatestBuyOutCalculationsPerFacilityAndTargetPeriod(accountId, List.of(getTargetPeriodByBusinessId(TargetPeriodType.TP7).getId()));

        // verify
        verify(buyOutSurplusCalculationRepository, times(1)).findAllByAccountIdAndTargetPeriodIdIn(accountId, List.of(1L));
        assertThat(result).isNotEmpty().containsExactlyInAnyOrderEntriesOf(Map.of(
                "facilityBusinessId1", Map.of(TargetPeriodType.TP7, expectedResult1),
                "facilityBusinessId2", Map.of(TargetPeriodType.TP7, expectedResult2))
        );
    }

    @Test
    void submitBuyOutCalculations() {
        BuyOutSurplusCalculationDetail detail1 = BuyOutSurplusCalculationDetail.builder()
                .facilityId(1L)
                .targetPeriodId(getTargetPeriodByBusinessId(TargetPeriodType.TP7).getId())
                .performanceDataId(100L)
                .build();
        BuyOutSurplusCalculation calculation1 = BuyOutSurplusCalculation.builder()
                .id(1L)
                .facilityId(1L)
                .targetPeriod(getTargetPeriodByBusinessId(TargetPeriodType.TP7))
                .performanceDataId(100L)
                .build();

        BuyOutSurplusCalculationDetail detail2 = BuyOutSurplusCalculationDetail.builder()
                .facilityId(1L)
                .targetPeriodId(getTargetPeriodByBusinessId(TargetPeriodType.TP8).getId())
                .performanceDataId(200L)
                .build();
        BuyOutSurplusCalculation calculation2 = BuyOutSurplusCalculation.builder()
                .id(2L)
                .facilityId(1L)
                .targetPeriod(getTargetPeriodByBusinessId(TargetPeriodType.TP8))
                .performanceDataId(200L)
                .build();
        
        when(buyOutSurplusCalculationRepository.saveAll(anyList()))
                .thenReturn(List.of(calculation1, calculation2));
        
        when(targetPeriodService.findBySchemeVersion(SchemeVersion.CCA_3)).thenReturn(getTargetPeriods());

        // invoke
        Map<Long, Long> result = service.submitBuyOutCalculations(List.of(detail1, detail2), SchemeVersion.CCA_3);

        // verify
        verify(buyOutSurplusCalculationRepository).saveAll(anyList());
        verify(targetPeriodService, times(1)).findBySchemeVersion(SchemeVersion.CCA_3);
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(100L, 1L, 200L, 2L));
    }
    
    @Test
    void shouldReturnEmptyWhenDetailsAreEmpty() {
        Map<Long, Long> result = service.submitBuyOutCalculations(List.of(), SchemeVersion.CCA_3);

        assertThat(result).isEmpty();
        verifyNoInteractions(buyOutSurplusCalculationRepository);
    }
    
    @Test
    void shouldThrowBusinessExceptionWhenTargetPeriodDoesNotExist() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final List<BuyOutSurplusCalculationDetail> details = List.of(BuyOutSurplusCalculationDetail.builder()
                .facilityId(2L)
                .accountId(1L)
                .performanceDataId(3L)
                .targetPeriodId(3L)
                .creationDate(LocalDate.of(2027, 1, 1).atStartOfDay())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                    .buyOutRequired(BigDecimal.valueOf(252))
                    .surplusGained(BigDecimal.ZERO)
                    .invoicedBuyOutFee(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(100))
                    .previousPaidFees(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .chargeType(null)
                    .build())
                .build());
        
        // Arrange
        when(targetPeriodService.findBySchemeVersion(schemeVersion))
                .thenReturn(getTargetPeriods());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.submitBuyOutCalculations(details, schemeVersion)
        );

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verify(buyOutSurplusCalculationRepository, never()).saveAll(anyList());
    }
    
    static List<TargetPeriod> getTargetPeriods() {
        return List.of(
            TargetPeriod.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(BigDecimal.valueOf(10))
                .startDate(LocalDate.of(2026, 1, 1))
                .schemeVersion(SchemeVersion.CCA_3)
                .build(),
                TargetPeriod.builder()
                .id(2L)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(BigDecimal.valueOf(100))
                .startDate(LocalDate.of(2027, 1, 1))
                .schemeVersion(SchemeVersion.CCA_3)
                .build()
        );
    }
    
    static TargetPeriod getTargetPeriodByBusinessId(TargetPeriodType businessId) {
        return getTargetPeriods().stream()
            .filter(dto -> dto.getBusinessId().equals(businessId))
            .findFirst()
            .orElse(null);
    }
}
