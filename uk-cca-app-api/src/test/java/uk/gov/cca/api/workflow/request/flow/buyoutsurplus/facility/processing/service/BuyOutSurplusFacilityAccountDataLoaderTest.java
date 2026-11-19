package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilityBuyOutSurplusCalculationService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilityBuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilityBuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilitySurplusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingData;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountDataLoaderTest {

    @InjectMocks
    private BuyOutSurplusFacilityAccountDataLoader service;

    @Mock
    private FacilityBuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;

    @Mock
    private FacilityBuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private FacilitySurplusService surplusService;

    @Mock
    private FacilityBuyOutSurplusCalculationService buyOutSurplusCalculationService;

    @Test
    void load() {
        final Long accountId = 1L;
        final String facilityBusinessId = "f11";
        final Long targetPeriodId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final List<TargetPeriodInfoDTO> targetPeriods = List.of(
                TargetPeriodInfoDTO.builder()
                        .id(targetPeriodId)
                        .businessId(targetPeriodType)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .build()
        );

        final Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> tprsByFacilityBusinessId = Map.of(
                facilityBusinessId,
                Map.of(
                        targetPeriodType,
                        FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                                .facilityId(11L)
                                .facilityBusinessId(facilityBusinessId)
                                .build()
                )
        );
        final BuyOutSurplusTransactionDetailSummary summary = BuyOutSurplusTransactionDetailSummary.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(targetPeriodType)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .creationDate(LocalDateTime.now())
                .build();
        final Map<String, Map<TargetPeriodType, BuyOutSurplusCalculationDetail>> latestCalculationsByFacilityBusinessId = Map.of(
                facilityBusinessId,
                Map.of(
                        targetPeriodType,
                        BuyOutSurplusCalculationDetail.builder()
                                .build()
                )
        );

        BuyOutSurplusFacilityAccountProcessingData expected = BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(tprsByFacilityBusinessId)
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, Map.of(targetPeriodType, List.of(summary))))
                .buyOutSurplusCalculations(latestCalculationsByFacilityBusinessId)
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.TEN))
                .build();

        when(buyOutSurplusProcessedDataService
                .getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, List.of(targetPeriodId)))
                .thenReturn(tprsByFacilityBusinessId);
        when(buyOutSurplusQueryService
                .getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, List.of(targetPeriodId)))
                .thenReturn(List.of(summary));
        when(buyOutSurplusCalculationService
                .getLatestBuyOutCalculationsPerFacilityAndTargetPeriod(accountId, List.of(targetPeriodId)))
                .thenReturn(latestCalculationsByFacilityBusinessId);
        when(surplusService.getPositiveSurplusMap(List.of(11L), SchemeVersion.CCA_3))
                .thenReturn(Map.of(11L, BigDecimal.TEN));

        // Invoke
        BuyOutSurplusFacilityAccountProcessingData result = service.load(accountId, targetPeriods);

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(buyOutSurplusProcessedDataService, times(1))
                .getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, List.of(targetPeriodId));
        verify(buyOutSurplusQueryService, times(1))
                .getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, List.of(targetPeriodId));
        verify(buyOutSurplusCalculationService, times(1))
                .getLatestBuyOutCalculationsPerFacilityAndTargetPeriod(accountId, List.of(targetPeriodId));
        verify(surplusService, times(1))
                .getPositiveSurplusMap(List.of(11L), SchemeVersion.CCA_3);
    }
}
