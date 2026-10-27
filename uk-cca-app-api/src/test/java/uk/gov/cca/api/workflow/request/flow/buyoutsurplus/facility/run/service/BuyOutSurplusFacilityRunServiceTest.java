package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunSummaryEntry;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityRunServiceTest {

	@InjectMocks
    private BuyOutSurplusFacilityRunService buyOutSurplusFacilityRunService;

    @Mock
    private RequestService requestService;

    @Test
    void submit() {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityRunRequestPayload.builder()
                        .submitterId(submitterId)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusFacilityRunService.submit(requestId);

        // Verify
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, null, CcaRequestActionType.BUY_OUT_SURPLUS_FACILITY_RUN_SUBMITTED, submitterId);
    }

    @Test
    void complete() {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        BuyOutSurplusFacilityRunRequestPayload requestPayload = BuyOutSurplusFacilityRunRequestPayload.builder()
                .accountStates(Map.of(
                        1L, BuyOutSurplusFacilityAccountState.builder()
                                .succeeded(true)
                                .facilityPerformanceResults(List.of(
                                        BuyOutSurplusFacilityPerformanceResult.builder()
                                                .facilityBusinessId("f1")
                                                .targetPeriodType(TargetPeriodType.TP7)
                                                .succeeded(true)
                                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                                                .build(),
                                        BuyOutSurplusFacilityPerformanceResult.builder()
                                                .facilityBusinessId("f1b")
                                                .targetPeriodType(TargetPeriodType.TP7)
                                                .succeeded(true)
                                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                                                .build()
                                ))
                                .build(),
                        2L, BuyOutSurplusFacilityAccountState.builder()
                                .succeeded(true)
                                .facilityPerformanceResults(List.of(
                                        BuyOutSurplusFacilityPerformanceResult.builder()
                                                .facilityBusinessId("f2")
                                                .targetPeriodType(TargetPeriodType.TP7)
                                                .succeeded(true)
                                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                                                .build(),
                                        BuyOutSurplusFacilityPerformanceResult.builder()
                                                .facilityBusinessId("f3")
                                                .targetPeriodType(TargetPeriodType.TP7)
                                                .succeeded(false)
                                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                                                .build(),
                                        BuyOutSurplusFacilityPerformanceResult.builder()
                                                .facilityBusinessId("f4")
                                                .targetPeriodType(TargetPeriodType.TP7)
                                                .succeeded(true)
                                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                                                .build(),
                                        BuyOutSurplusFacilityPerformanceResult.builder()
                                                .facilityBusinessId("f5")
                                                .targetPeriodType(TargetPeriodType.TP7)
                                                .succeeded(true)
                                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                                                .build()
                                ))
                                .build(),
                        3L, BuyOutSurplusFacilityAccountState.builder()
                                .succeeded(false)
                                .facilityPerformanceResults(List.of(
                                        BuyOutSurplusFacilityPerformanceResult.builder()
                                                .facilityBusinessId("f6")
                                                .targetPeriodType(TargetPeriodType.TP7)
                                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                                                .succeeded(false)
                                                .build()
                                ))
                                .build()
                ))
                .targetPeriodsDetails(List.of(TargetPeriodInfoDTO.builder().buyOutCost(BigDecimal.TEN).businessId(TargetPeriodType.TP7).build(),
                                              TargetPeriodInfoDTO.builder().buyOutCost(BigDecimal.valueOf(100)).businessId(TargetPeriodType.TP8).build()))
                .submitterId(submitterId)
                .build();
        BuyOutSurplusFacilityRunRequestMetadata metadata = BuyOutSurplusFacilityRunRequestMetadata.builder().build();
        Request request = Request.builder()
                .payload(requestPayload)
                .metadata(metadata)
                .build();
        
        List<BuyOutSurplusFacilityRunSummaryEntry> entries = List.of(BuyOutSurplusFacilityRunSummaryEntry.builder()
                .targetPeriod(TargetPeriodType.TP7)
                .buyOutCost(BigDecimal.TEN)
                .totalFacilities((long) 5)
                .totalBuyOutTransactions((long) 2)
                .totalBuyOutFacilities((long) 3)
                .totalRefundTransactions((long) 1)
                .totalRefundFacilities((long) 2)
                .build());

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusFacilityRunService.complete(requestId);

        // Verify
        assertThat(requestPayload.getRunSummary().getTotalAccounts()).isEqualTo(3L);
        assertThat(requestPayload.getRunSummary().getFailedAccounts()).isEqualTo(1L);
        assertThat(requestPayload.getRunSummary().getTotalFacilities()).isEqualTo(7L);
        assertThat(requestPayload.getRunSummary().getFailedFacilities()).isEqualTo(2L);
        assertThat(requestPayload.getRunSummary().getRunSummaryEntries()).isNotEmpty();
        assertThat(requestPayload.getRunSummary().getRunSummaryEntries()).containsExactlyInAnyOrderElementsOf(entries);
        assertThat(metadata.getTotalAccounts()).isEqualTo(3L);
        assertThat(metadata.getFailedAccounts()).isEqualTo(1L);
        assertThat(metadata.getTotalFacilities()).isEqualTo(7L);
        assertThat(metadata.getFailedFacilities()).isEqualTo(2L);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
        verify(requestService, times(1)).addActionToRequest(
                eq(request), any(), eq(CcaRequestActionType.BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_WITH_FAILURES), eq(submitterId));
    }
}
