package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusErrorType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityRunCreateCsvServiceTest {

	@InjectMocks
    private BuyOutSurplusFacilityRunCreateCsvService service;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;
    
    @Test
    void createCsvFile() {
        final String requestId = "requestId";
        
        List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults = List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("facilityBusinessId")
                .siteName("facilitySiteName")
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .primaryRulesApplied(true)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.valueOf(100))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.valueOf(20))
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(8000)).build())
                .succeeded(true)
                .build());

        BuyOutSurplusFacilityRunRequestPayload requestPayload = BuyOutSurplusFacilityRunRequestPayload.builder()
                .submitterId("regulator")
                .accountStates(Map.of(
                        1L, BuyOutSurplusFacilityAccountState.builder()
                        .accountBusinessId("account1")
                        .facilityPerformanceResults(facilityPerformanceResults)
                        .succeeded(true)
                        .build()
                ))
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final String fileCsv = "fileCsv";

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED)))
                .thenReturn(fileCsv);

        // Invoke
        service.createCsvFile(requestId);

        // Verify
        assertThat(requestPayload.getErrorType()).isNull();
        assertThat(requestPayload.getCsvFile()).isNotNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED));
    }

    @Test
    void createCsvFile_no_accounts() {
        final String requestId = "requestId";

        BuyOutSurplusFacilityRunRequestPayload requestPayload = BuyOutSurplusFacilityRunRequestPayload.builder()
                .submitterId("regulator")
                .accountStates(Map.of())
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.createCsvFile(requestId);

        // Verify
        assertThat(requestPayload.getErrorType()).isNull();
        assertThat(requestPayload.getCsvFile()).isNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(ccaFileAttachmentService);
    }

    @Test
    void createCsvFile_throw_exception() {
        final String requestId = "requestId";

        BuyOutSurplusFacilityRunRequestPayload requestPayload = BuyOutSurplusFacilityRunRequestPayload.builder()
                .submitterId("regulator")
                .accountStates(Map.of(
                        1L, BuyOutSurplusFacilityAccountState.builder()
                        .accountBusinessId("account1")
                        .facilityPerformanceResults(List.of())
                        .succeeded(true)
                        .build()
                ))
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED)))
                .thenThrow(new NullPointerException("test"));

        // Invoke
        service.createCsvFile(requestId);

        // Verify
        assertThat(requestPayload.getErrorType()).isEqualTo(BuyOutSurplusErrorType.GENERATE_CSV_FAILED);
        assertThat(requestPayload.getCsvFile()).isNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED));
    }

    
}
