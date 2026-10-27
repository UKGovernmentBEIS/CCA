package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

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
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusCalculationService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountDataLoaderTest {

    @InjectMocks
    private BuyOutSurplusFacilityAccountDataLoader service;

    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private BuyOutSurplusCalculationService buyOutSurplusCalculationService;

    @Mock
    private SurplusQueryService surplusQueryService;

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

        when(buyOutSurplusQueryService
                .getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, List.of(targetPeriodId)))
                .thenReturn(tprsByFacilityBusinessId);
        when(buyOutSurplusQueryService
                .getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, List.of(targetPeriodId)))
                .thenReturn(List.of(summary));
        when(buyOutSurplusCalculationService
                .getLatestBuyOutCalculationsPerFacilityAndTargetPeriod(accountId, List.of(targetPeriodId)))
                .thenReturn(latestCalculationsByFacilityBusinessId);
        when(surplusQueryService.getFacilityPositiveSurplusMap(List.of(11L), SchemeVersion.CCA_3))
                .thenReturn(Map.of(11L, BigDecimal.TEN));

        // Invoke
        BuyOutSurplusFacilityAccountProcessingData result = service.load(accountId, targetPeriods);

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(buyOutSurplusQueryService, times(1))
                .getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, List.of(targetPeriodId));
        verify(buyOutSurplusQueryService, times(1))
                .getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, List.of(targetPeriodId));
        verify(buyOutSurplusCalculationService, times(1))
                .getLatestBuyOutCalculationsPerFacilityAndTargetPeriod(accountId, List.of(targetPeriodId));
        verify(surplusQueryService, times(1))
                .getFacilityPositiveSurplusMap(List.of(11L), SchemeVersion.CCA_3);
    }
}
