package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.validation.BuyOutSurplusFacilityViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingData;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils.BuyOutSurplusFacilityCalculationUtil;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingServiceTest {
    
    @Mock
    private RequestService requestService;

    @Mock
    private BuyOutSurplusFacilityAccountDataLoader dataLoader;

    private BuyOutSurplusFacilityProcessingContextFactory contextFactory;
    private BuyOutSurplusFacilityProcessor processor;

    private BuyOutSurplusFacilityAccountProcessingService service;

    @BeforeEach
    void setUp() {
        contextFactory = new BuyOutSurplusFacilityProcessingContextFactory();
        processor = new BuyOutSurplusFacilityProcessor();

        service = new BuyOutSurplusFacilityAccountProcessingService(requestService, 
                dataLoader, 
                contextFactory,
                processor);
    }

    // TP8 V1 BuyOut 100 (Primary) ~ no cascading effect
    @Test
    void doProcess_tp8_v1_BuyOut100() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = true;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v1_BuyOut100(facilityBusinessId, facilitySiteName));
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .primaryRulesApplied(true)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                        .buyOutRequired(BigDecimal.valueOf(100))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.valueOf(20))
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(8000))
                        .build())
                .succeeded(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of())
                .buyOutSurplusCalculations(Map.of())
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(20)))
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());        
    }
    
    // TP8 V1 Surplus 80 (Primary) ~ no cascading effect
    @Test
    void doProcess_tp8_v1_Surplus80() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = true;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v2_Surplus80(facilityBusinessId, facilitySiteName));
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(null)
                .processed(false)
                .succeeded(true)
                .primaryRulesApplied(true)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(80))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(100))
                    .chargeType(null)
                    .invoicedBuyOutFee(BigDecimal.ZERO)
                    .build())                
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of())
                .buyOutSurplusCalculations(Map.of())
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(20)))
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());        
    }

    // TP8 V2 Surplus 80 (Secondary) ~ no cascading effect
    @Test
    void doProcess_tp8_v2_Surplus80() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(getTargetPeriodDetailsWithTP8Current())
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v2_Surplus80(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> transactions = List.of(BuyOutSurplusTransactionDetailSummary.builder()   
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP8)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .performanceDataId(2L)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .creationDate(LocalDateTime.now())
                .buyOutFee(BigDecimal.valueOf(800))
                .build());
        
        BuyOutSurplusCalculationDetail calculation = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .buyOutRequired(BigDecimal.valueOf(800))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.valueOf(20))
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .processed(false)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(80))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.valueOf(20))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(800))
                    .schemeTotalSurplus(BigDecimal.valueOf(100))
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .build())
                .succeeded(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(transactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, groupCalculationsByTargetPeriod(List.of(calculation))))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());        
    }
    
    // TP8 V2 Surplus 80 (Secondary) ~ no previous calculation ~ no cascading effect
    @Test
    void doProcess_tp8_v2_Surplus80_no_previous_calculation() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v2_Surplus80(facilityBusinessId, facilitySiteName));
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .processed(false)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(80))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(80))
                    .chargeType(null)
                    .invoicedBuyOutFee(BigDecimal.ZERO)
                    .build())
                .paymentStatus(null)
                .succeeded(true)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of())
                .buyOutSurplusCalculations(Map.of())
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());        
    }
    
    // TP8 V3a BuyOut 200 (Secondary - REFUNDED) ~ no cascading effect
    @Test
    void doProcess_tp8_v3a_BuyOut200() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v3_BuyOut200(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> transactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                        .performanceDataId(2L)
                        .targetPeriodType(TargetPeriodType.TP8)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                        .buyOutFee(BigDecimal.valueOf(800))
                        .creationDate(LocalDateTime.now())
                        .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                        .performanceDataId(3L)
                        .targetPeriodType(TargetPeriodType.TP8)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED)
                        .buyOutFee(BigDecimal.valueOf(800))
                        .creationDate(LocalDateTime.now()).build());
        
        BuyOutSurplusCalculationDetail calculation = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(80))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(800))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.valueOf(20))
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .schemeTotalSurplus(BigDecimal.valueOf(100))
                    .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .processed(false)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(200))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.valueOf(80))
                    .previousPaidFees(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(20))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(20000))
                    .build())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .succeeded(true)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(transactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, groupCalculationsByTargetPeriod(List.of(calculation))))
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(100)))
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());        
    }
    
    // TP8 V3b BuyOut 200 (Secondary - Awaiting Refunded) ~ no cascading effect
    @Test
    void doProcess_tp8_v3b_BuyOut200() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v3_BuyOut200(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> transactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                        .performanceDataId(2L)
                        .targetPeriodType(TargetPeriodType.TP8)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                        .buyOutFee(BigDecimal.valueOf(800))
                        .creationDate(LocalDateTime.now())
                        .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                        .performanceDataId(3L)
                        .targetPeriodType(TargetPeriodType.TP8)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                        .buyOutFee(BigDecimal.valueOf(800))
                        .creationDate(LocalDateTime.now()).build());
        
        BuyOutSurplusCalculationDetail calculation = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .facilityId(1L)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.valueOf(80))
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.valueOf(800))
                        .previousSurplusGained(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.valueOf(20))
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .invoicedBuyOutFee(BigDecimal.valueOf(800))
                        .schemeTotalSurplus(BigDecimal.valueOf(100))
                        .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(200))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.valueOf(80))
                    .previousPaidFees(BigDecimal.valueOf(800))
                    .schemeTotalSurplus(BigDecimal.valueOf(20))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(19200))
                    .build())
                .succeeded(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(transactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, groupCalculationsByTargetPeriod(List.of(calculation))))
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(100)))
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());        
    }
    
    // TP8 V3c BuyOut 200 (Secondary - Refunded) ~ no cascading effect
    @Test
    void doProcess_tp8_v3c_BuyOut200_failed() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v3_BuyOut200(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> transactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                        .performanceDataId(2L)
                        .targetPeriodType(TargetPeriodType.TP8)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                        .buyOutFee(BigDecimal.valueOf(800))
                        .creationDate(LocalDateTime.now())
                        .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                        .performanceDataId(3L)
                        .targetPeriodType(TargetPeriodType.TP8)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED)
                         // Manually Update to 1000 (v3c)
                        .buyOutFee(BigDecimal.valueOf(1000))
                        .creationDate(LocalDateTime.now()).build());
        
        BuyOutSurplusCalculationDetail calculation = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .facilityId(1L)
                .creationDate(LocalDateTime.now())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(80))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(800))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.valueOf(20))
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .schemeTotalSurplus(BigDecimal.valueOf(100))
                    .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .AMOUNT_REFUNDED_GREATER_THAN_AMOUNT_RECEIVED
                        .getMessage(TargetPeriodType.TP8))).build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(transactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, groupCalculationsByTargetPeriod(List.of(calculation))))
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(100)))
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());        
    }
    
    // TP8 V3d Surplus 100 (Secondary)  ~ no cascading effect
    @Test
    void doProcess_tp8_v3d_Surplus100() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v3d_Surplus100(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> transactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                        .performanceDataId(2L)
                        .targetPeriodType(TargetPeriodType.TP8)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                        .buyOutFee(BigDecimal.valueOf(800))
                        .creationDate(LocalDateTime.now())
                        .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                        .performanceDataId(3L)
                        .targetPeriodType(TargetPeriodType.TP8)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                        .buyOutFee(BigDecimal.valueOf(800))
                        .creationDate(LocalDateTime.now()).build());
        
        BuyOutSurplusCalculationDetail calculation = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .facilityId(1L)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(80))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(800))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.valueOf(20))
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .schemeTotalSurplus(BigDecimal.valueOf(100))
                    .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .processed(false)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(100))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.valueOf(80))
                    .previousPaidFees(BigDecimal.valueOf(800))
                    .schemeTotalSurplus(BigDecimal.valueOf(120))
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .build())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .succeeded(true)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(transactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, groupCalculationsByTargetPeriod(List.of(calculation))))
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(100)))
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());        
    }
    
    // Example 1 — Cascading Effect of a Secondary TP7 TPR Submission (Surplus: 80)
    @Test
    void doProcess_cascading_ex1() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex1_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex1_tp8(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP9, getTpr_ex1_tp9(facilityBusinessId, facilitySiteName));
        
        // Surplus 50
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(50))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(50))
                    .build())
                .build();
        
        // Buy-out 20
        BuyOutSurplusCalculationDetail calculationTP8 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(20))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(20))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(30))
                    .build())
                .build();
        
        // Buy-out 10
        BuyOutSurplusCalculationDetail calculationTP9 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(10))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(10))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(20))
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7,
                        TargetPeriodType.TP8, calculationTP8,
                        TargetPeriodType.TP9, calculationTP9
                );
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP7).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(tprbyTP.get(TargetPeriodType.TP7).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP7).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.valueOf(50))
                    .schemeTotalSurplus(BigDecimal.valueOf(30).negate())
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP8 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(20))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.valueOf(20))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(10).negate())
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(2000))
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP9 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP9).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(1000))
                .submissionType(tprbyTP.get(TargetPeriodType.TP9).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP9).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(10))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.valueOf(10))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(10000))
                    .build())
                .succeeded(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of())
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(20)))
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP7);
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP8);   
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP9);
    }
    
    // Example 2.1 — Cascading Effect of a Secondary TP8 TPR Submission (Buyout: 10)
    @Test
    void doProcess_cascading_ex2a() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex21_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex21_tp8(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP9, getTpr_ex21_tp9(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> transactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(3L)
                .targetPeriodType(TargetPeriodType.TP9)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(60000))
                .creationDate(LocalDateTime.now())
                .build());
        
        // Surplus 50
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(50))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(50))
                    .build())
                .build();
        
        // Buy-out 30
        BuyOutSurplusCalculationDetail calculationTP8 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(30))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(30))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(20))
                    .build())
                .build();
        
        // Buy-out 80
        BuyOutSurplusCalculationDetail calculationTP9 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(20))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7,
                        TargetPeriodType.TP8, calculationTP8,
                        TargetPeriodType.TP9, calculationTP9
                );

        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP8 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(10))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.valueOf(30))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(30))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(1000))
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP9 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP9).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(1000))
                .submissionType(tprbyTP.get(TargetPeriodType.TP9).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP9).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(60000))
                    .previousSurplusUsed(BigDecimal.valueOf(20))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(50))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(20000))
                    .build())
                .succeeded(true)
                .build();
        
        List<BuyOutSurplusFacilityPerformanceResult> facilityResultsExpected = List.of(resultExpectedTP8, resultExpectedTP9);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(transactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(facilityResultsExpected, accountState.getFacilityPerformanceResults());        
    }
    
    // Example 2.2 — Cascading Effect of a Secondary TP8 TPR Submission (Surplus: 25)
    @Test
    void doProcess_cascading_ex2b() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(getTargetPeriodDetailsWithTP9Current())
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex21_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex22_tp8(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP9, getTpr_ex21_tp9(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> transactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataId(3L)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(60000))
                .creationDate(LocalDateTime.now())
                .build());
        
        // Surplus 50
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(50))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(50))
                    .build())
                .build();
        
        // Buy-out 30
        BuyOutSurplusCalculationDetail calculationTP8 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(30))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(30))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(20))
                    .build())
                .build();
        
        // Buy-out 80
        BuyOutSurplusCalculationDetail calculationTP9 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(20))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7,
                        TargetPeriodType.TP8, calculationTP8,
                        TargetPeriodType.TP9, calculationTP9
                );

        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP8 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(25))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.valueOf(30))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(55))
                    .invoicedBuyOutFee(BigDecimal.ZERO)
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP9 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP9).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(1000))
                .submissionType(tprbyTP.get(TargetPeriodType.TP9).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP9).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(60000))
                    .previousSurplusUsed(BigDecimal.valueOf(20))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(75))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(20000))
                    .build())
                .succeeded(true)
                .build();
        
        List<BuyOutSurplusFacilityPerformanceResult> facilityResultsExpected = List.of(resultExpectedTP8, resultExpectedTP9);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(transactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(facilityResultsExpected, accountState.getFacilityPerformanceResults());        
    }
    
    // Example 4 — Cascading Effect of a Secondary TP7 TPR Submission (Buyout: 120)
    @Test
    void doProcess_cascading_ex4() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex4_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex4_tp8(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> allTransactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(1L)
                .targetPeriodType(TargetPeriodType.TP7)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(200))
                .creationDate(LocalDateTime.now())
                .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(2L)
                .targetPeriodType(TargetPeriodType.TP8)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(10000))
                .creationDate(LocalDateTime.now())
                .build());
        
        // Buy-out 20
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(20))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(200))
                    .schemeTotalSurplus(BigDecimal.valueOf(0))
                    .build())
                .build();
        
        // Buy-out 100
        BuyOutSurplusCalculationDetail calculationTP8 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(100))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(1000))
                    .schemeTotalSurplus(BigDecimal.valueOf(0))
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7,
                        TargetPeriodType.TP8, calculationTP8
                );

        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP7).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(tprbyTP.get(TargetPeriodType.TP7).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP7).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(120))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(200))
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(1000))
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP8 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(100))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(10000))
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .invoicedBuyOutFee(BigDecimal.ZERO)
                    .build())
                .succeeded(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(allTransactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP7); 
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP8);   
    }
    
    // Example 5 — Manual Update of the Refund Amount in a Transaction - Cascading Effect on TP7
    @Test
    void doProcess_cascading_ex5() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex5_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex5_tp8(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> allTransactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(1L)
                .targetPeriodType(TargetPeriodType.TP7)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(800))
                .creationDate(LocalDateTime.now())
                .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(2L)
                .targetPeriodType(TargetPeriodType.TP8)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(2000))
                .creationDate(LocalDateTime.now())
                .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(3L)
                .targetPeriodType(TargetPeriodType.TP8)
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .buyOutFee(BigDecimal.valueOf(1200)) //updated manually
                .creationDate(LocalDateTime.now())
                .build());
        
        // Buy-out 80
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .build())
                .build();
        
        // Buy-out 10
        BuyOutSurplusCalculationDetail calculationTP8 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(10))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(2000))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .invoicedBuyOutFee(BigDecimal.valueOf(1000))
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7,
                        TargetPeriodType.TP8, calculationTP8
                );

        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP7).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(tprbyTP.get(TargetPeriodType.TP7).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP7).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(50))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(800))
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .invoicedBuyOutFee(BigDecimal.valueOf(300))
                .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP8 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(10))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(2000))
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .invoicedBuyOutFee(BigDecimal.valueOf(1000))
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .build())
                .succeeded(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(allTransactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP7);
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP8);  
    }

    
    // Example 6 — Manual Increase of the Surplus Amount - Cascading Effect on TP7
    @Test
    void doProcess_cascading_ex6() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex6_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex6_tp8(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP9, getTpr_ex6_tp9(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> allTransactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(3L)
                .targetPeriodType(TargetPeriodType.TP9)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(20000))
                .creationDate(LocalDateTime.now())
                .build());
        
        // Surplus 20
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(20))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(null)
                    .invoicedBuyOutFee(null)
                    .schemeTotalSurplus(BigDecimal.valueOf(20))
                    .build())
                .build();
        
        // Surplus 50
        BuyOutSurplusCalculationDetail calculationTP8 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(50))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(null)
                    .invoicedBuyOutFee(null)
                    .schemeTotalSurplus(BigDecimal.valueOf(70))
                    .build())
                .build();
        
        // Buy-out 120
        BuyOutSurplusCalculationDetail calculationTP9 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(120))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(100))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(20000))
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7,
                        TargetPeriodType.TP8, calculationTP8,
                        TargetPeriodType.TP9, calculationTP9
                );

        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP7).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(tprbyTP.get(TargetPeriodType.TP7).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP7).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(30))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.valueOf(20))
                    .schemeTotalSurplus(BigDecimal.valueOf(-20))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(300))
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP8 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(50))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.valueOf(50))
                    .schemeTotalSurplus(BigDecimal.valueOf(-20))
                    .invoicedBuyOutFee(BigDecimal.ZERO)
                    .chargeType(null)
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP9 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP9).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(1000))
                .submissionType(tprbyTP.get(TargetPeriodType.TP9).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP9).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(120))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(20000))
                    .previousSurplusUsed(BigDecimal.valueOf(100))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(80))
                    .invoicedBuyOutFee(BigDecimal.valueOf(100000))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .build())
                .succeeded(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(allTransactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP7);
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP8); 
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP9);       
    }
    
    // Example 7 — Manual Decrease of the Surplus Amount ~ Cascading effect on TP7
    @Test
    void doProcess_cascading_ex7() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex6_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex6_tp8(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP9, getTpr_ex6_tp9(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> allTransactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(3L)
                .targetPeriodType(TargetPeriodType.TP9)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(60000))
                .creationDate(LocalDateTime.now())
                .build());
        
        // Surplus 20
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(20))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(null)
                    .invoicedBuyOutFee(null)
                    .schemeTotalSurplus(BigDecimal.valueOf(20))
                    .build())
                .build();
        
        // Surplus 50
        BuyOutSurplusCalculationDetail calculationTP8 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(50))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(null)
                    .invoicedBuyOutFee(null)
                    .schemeTotalSurplus(BigDecimal.valueOf(70))
                    .build())
                .build();
        
        // Buy-out 120
        BuyOutSurplusCalculationDetail calculationTP9 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP9)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(120))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(60))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(6000))
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7,
                        TargetPeriodType.TP8, calculationTP8,
                        TargetPeriodType.TP9, calculationTP9
                );

        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP7).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(tprbyTP.get(TargetPeriodType.TP7).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP7).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(30))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.valueOf(20))
                    .schemeTotalSurplus(BigDecimal.valueOf(-20))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(300))
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP8 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(50))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.valueOf(50))
                    .schemeTotalSurplus(BigDecimal.valueOf(-20))
                    .invoicedBuyOutFee(BigDecimal.ZERO)
                    .chargeType(null)
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP9 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataReportVersion(0)
                .processed(true)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP9).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(1000))
                .submissionType(tprbyTP.get(TargetPeriodType.TP9).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP9).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(120))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(60000))
                    .previousSurplusUsed(BigDecimal.valueOf(60))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(40))
                    .invoicedBuyOutFee(BigDecimal.valueOf(60000))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .build())
                .succeeded(true)
                .build();
        
        List<BuyOutSurplusFacilityPerformanceResult> facilityResultsExpected = List.of(resultExpectedTP7, resultExpectedTP8, resultExpectedTP9);

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(allTransactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(facilityResultsExpected, accountState.getFacilityPerformanceResults());        
    }
    
    // Example 8 — Primary Period with No Unprocessed TPR for the Current Period
    @Test
    void doProcess_primaryPeriod_whenNoUnprocessedTprExistsForCurrentTP() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = true;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex6_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex6_tp8(facilityBusinessId, facilitySiteName));
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of())
                .buyOutSurplusCalculations(Map.of())
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState); 
        
        assertEquals(0, accountState.getFacilityPerformanceResults().size());
    }
    
    // Example 9 — Secondary Period with missing TPR for TP9
    @Test
    void doProcess_cascading_ex9() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                new TreeMap<> (Map.of(TargetPeriodType.TP7, getTpr_ex6_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex6_tp8(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP9, getTpr_ex9_tp9(facilityBusinessId, facilitySiteName)));
        

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of())
                .buyOutSurplusCalculations(Map.of())
                .surplusBuckets(Map.of())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        List<BuyOutSurplusFacilityPerformanceResult> facilityResultsExpected = List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .performanceDataId(4L)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .MISSING_TPRS
                        .getMessage(Set.of(TargetPeriodType.TP9))))
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .performanceDataId(2L)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .MISSING_TPRS
                        .getMessage(Set.of(TargetPeriodType.TP9))))
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataReportVersion(0)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .MISSING_TPRS
                        .getMessage(Set.of(TargetPeriodType.TP9))))
                .build());
        
        assertEquals(facilityResultsExpected, accountState.getFacilityPerformanceResults());        
    }
    
    // Example 10 — Primary Period with Unprocessed TPR for the Current Period and missing TPRs
    @Test
    void doProcess_primaryPeriod_whenProcessedTprExistsForCurrentTP() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = true;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP7, getTpr_ex10_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex10_tp8(facilityBusinessId, facilitySiteName));
        
        List<BuyOutSurplusTransactionDetailSummary> allTransactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataId(1L)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(200))
                .creationDate(LocalDateTime.now())
                .build());
        
        // Buy-out 20
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(20))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(200))
                    .schemeTotalSurplus(BigDecimal.valueOf(0))
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7
                );
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .performanceDataId(2L)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .MISSING_TPRS
                        .getMessage(new TreeSet<> (Set.of(TargetPeriodType.TP7)))))
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP8 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .processed(false)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(100))
                .submissionType(tprbyTP.get(TargetPeriodType.TP8).getSubmissionType())
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .primaryRulesApplied(true)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(tprbyTP.get(TargetPeriodType.TP8).getPerformanceOutcome())
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .invoicedBuyOutFee(BigDecimal.valueOf(8000))
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .build())
                .succeeded(true)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(allTransactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(2, accountState.getFacilityPerformanceResults().size());
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP7);
        assertThat(accountState.getFacilityPerformanceResults()).contains(resultExpectedTP8);
    }
    
    // Example 11 — Secondary Period with missing TPR for TP8, TP9
    @Test
    void doProcess_cascading_ex11() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                new TreeMap<> (Map.of(TargetPeriodType.TP7, getTpr_ex6_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex11_tp8(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP9, getTpr_ex9_tp9(facilityBusinessId, facilitySiteName)));
        

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of())
                .buyOutSurplusCalculations(Map.of())
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        List<BuyOutSurplusFacilityPerformanceResult> facilityResultsExpected = List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .performanceDataId(4L)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .MISSING_TPRS
                        .getMessage(new TreeSet<> (Set.of(TargetPeriodType.TP8, TargetPeriodType.TP9)))))
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .MISSING_TPRS
                        .getMessage(new TreeSet<> (Set.of(TargetPeriodType.TP8, TargetPeriodType.TP9)))))
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataReportVersion(0)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .MISSING_TPRS
                        .getMessage(new TreeSet<> (Set.of(TargetPeriodType.TP8, TargetPeriodType.TP9)))))
                .build());
        
        assertEquals(facilityResultsExpected, accountState.getFacilityPerformanceResults());    
    }

    
    // Example 12 — with error
    @Test
    void doProcess_cascading_ex12() {
        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = false;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP9Current();

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                new TreeMap<> (Map.of(TargetPeriodType.TP7, getTpr_ex5_tp7(facilityBusinessId, facilitySiteName),
                        TargetPeriodType.TP8, getTpr_ex5_tp8(facilityBusinessId, facilitySiteName)));
        
        List<BuyOutSurplusTransactionDetailSummary> allTransactions = List.of(
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(1L)
                .targetPeriodType(TargetPeriodType.TP7)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(800))
                .creationDate(LocalDateTime.now())
                .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(2L)
                .targetPeriodType(TargetPeriodType.TP8)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.valueOf(2000))
                .creationDate(LocalDateTime.now())
                .build(),
                BuyOutSurplusTransactionDetailSummary.builder().facilityBusinessId(facilityBusinessId)
                .performanceDataId(3L)
                .targetPeriodType(TargetPeriodType.TP8)
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED) //updated manually
                .buyOutFee(BigDecimal.valueOf(3000)) //updated manually
                .creationDate(LocalDateTime.now())
                .build());
        
        // Buy-out 80
        BuyOutSurplusCalculationDetail calculationTP7 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.FEE)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .build())
                .build();
        
        // Buy-out 10
        BuyOutSurplusCalculationDetail calculationTP8 = BuyOutSurplusCalculationDetail.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .buyOutRequired(BigDecimal.valueOf(10))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(2000))
                    .previousSurplusGained(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .chargeType(BuyOutSurplusChargeType.REFUND)
                    .invoicedBuyOutFee(BigDecimal.valueOf(1000))
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .build())
                .build();
        
        Map<TargetPeriodType, BuyOutSurplusCalculationDetail> calculationsByTargetPriod = 
                Map.of(
                        TargetPeriodType.TP7, calculationTP7,
                        TargetPeriodType.TP8, calculationTP8
                );
        
        List<BuyOutSurplusFacilityPerformanceResult> facilityResultsExpected = List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(0)
                .performanceDataId(4L)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .AMOUNT_REFUNDED_GREATER_THAN_AMOUNT_RECEIVED
                        .getMessage(TargetPeriodType.TP8)))
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .performanceDataId(3L)
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .AMOUNT_REFUNDED_GREATER_THAN_AMOUNT_RECEIVED
                        .getMessage(TargetPeriodType.TP8)))
                .build());

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of(facilityBusinessId, groupTransactionsByTargetPeriod(allTransactions)))
                .buyOutSurplusCalculations(Map.of(facilityBusinessId, calculationsByTargetPriod))
                .surplusBuckets(emptySurplusBucket())
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(facilityResultsExpected, accountState.getFacilityPerformanceResults());        
    }
    
    // TP8 V1 BuyOut 100 (Primary) ~ no cascading effect PROCESSING ERROR
    @Test
    void doProcess_tp8_v1_BuyOut100_processing_error() {

        final String requestId = "ADS-T00001-BS-TP8001";
        final Long accountId = 1L;
        final String accountBusinessId = "ADS-T00001";
        final String submitterId = "regulator";
        final String facilityBusinessId = "ADS-F00001";
        final String facilitySiteName = "siteName";
        final boolean applyPrimaryRules = true;
        
        final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
        
        List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
        
        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .submitterId(submitterId)
                        .schemeVersion(SchemeVersion.CCA_3)
                        .targetPeriodsDetails(targetPeriodDetails)
                        .applyPrimaryRules(applyPrimaryRules)
                        .invoicedPaymentDeadlinePerTP(Map.of())
                        .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                .build();
                
        // Performance Data
        Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                Map.of(TargetPeriodType.TP8, getTpr_tp8_v1_BuyOut100(facilityBusinessId, facilitySiteName));
        
        BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(0)
                .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                .succeeded(false)
                .errors(List.of(BuyOutSurplusFacilityViolation
                        .BuyOutSurplusFacilityViolationMessage
                        .PROCESSING_ERROR
                        .getMessage())).build();

        try (MockedStatic<BuyOutSurplusFacilityCalculationUtil> mockedUtil =
                Mockito.mockStatic(BuyOutSurplusFacilityCalculationUtil.class)) {

            mockedUtil.when(() ->BuyOutSurplusFacilityCalculationUtil.calculatePrimary(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Forced processing error"));
            
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                .paidOrRefundedTransactions(Map.of())
                .buyOutSurplusCalculations(Map.of())
                .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(20)))
                .build());
        
        // Invoke
        service.doProcess(requestId, accountState);
        
        assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
        assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());
        }
    }
    
    // TP8 V1 BuyOut 100 (Primary) ~ no cascading effect INVALID DATA ERROR
        @Test
        void doProcess_tp8_v1_BuyOut100_invalid_data() {

            final String requestId = "ADS-T00001-BS-TP8001";
            final Long accountId = 1L;
            final String accountBusinessId = "ADS-T00001";
            final String submitterId = "regulator";
            final String facilityBusinessId = "ADS-F00001";
            final String facilitySiteName = "siteName";
            final boolean applyPrimaryRules = true;
            
            final BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder().accountId(accountId).build();
            
            List<TargetPeriodInfoDTO> targetPeriodDetails = getTargetPeriodDetailsWithTP8Current();
            
            final Request request = Request.builder()
                    .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                            .submitterId(submitterId)
                            .schemeVersion(SchemeVersion.CCA_3)
                            .targetPeriodsDetails(targetPeriodDetails)
                            .applyPrimaryRules(applyPrimaryRules)
                            .invoicedPaymentDeadlinePerTP(Map.of())
                            .accountDetails(getTargetUnitAccountDetails(accountId, accountBusinessId))
                            .build())
                    .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().build())
                    .build();
                    
            // Performance Data
            Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprbyTP = 
                    Map.of(TargetPeriodType.TP8, getTpr_tp8_v1_BuyOut100(facilityBusinessId, facilitySiteName));
            tprbyTP.get(TargetPeriodType.TP8).setPerformanceOutcome(PerformanceDataFacilityTargetPeriodResultType.SUBMITTED);// Force invalid data
            
            BuyOutSurplusFacilityPerformanceResult resultExpected = BuyOutSurplusFacilityPerformanceResult.builder()
                    .facilityBusinessId(facilityBusinessId)
                    .targetPeriodType(TargetPeriodType.TP8)
                    .performanceDataReportVersion(0)
                    .performanceDataId(tprbyTP.get(TargetPeriodType.TP8).getPerformanceDataId())
                    .succeeded(false)
                    .errors(List.of(BuyOutSurplusFacilityViolation
                            .BuyOutSurplusFacilityViolationMessage
                            .INVALID_DATA
                            .getMessage())).build();
                
            when(requestService.findRequestById(requestId)).thenReturn(request);
            when(dataLoader.load(accountId, targetPeriodDetails)).thenReturn(BuyOutSurplusFacilityAccountProcessingData.builder()
                    .performanceDataReports(Map.of(facilityBusinessId, tprbyTP))
                    .paidOrRefundedTransactions(Map.of())
                    .buyOutSurplusCalculations(Map.of())
                    .surplusBuckets(Map.of(facilityBusinessId, BigDecimal.valueOf(20)))
                    .build());
            
            // Invoke
            service.doProcess(requestId, accountState);
            
            assertTrue(!accountState.getFacilityPerformanceResults().isEmpty());
            assertEquals(List.of(resultExpected), accountState.getFacilityPerformanceResults());
        }
    
    static List<TargetPeriodInfoDTO> getTargetPeriodDetailsWithTP9Current() {
        return List.of(
            TargetPeriodInfoDTO.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(BigDecimal.valueOf(10))
                .startDate(LocalDate.of(2026, 1, 1))
                .build(),
            TargetPeriodInfoDTO.builder()
                .id(2L)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(BigDecimal.valueOf(100))
                .startDate(LocalDate.of(2027, 1, 1))
                .build(),
            TargetPeriodInfoDTO.builder()
                .id(3L)
                .businessId(TargetPeriodType.TP9)
                .buyOutCost(BigDecimal.valueOf(1000))
                .startDate(LocalDate.of(2029, 1, 1))
                .build()
        );
    }
    
    static List<TargetPeriodInfoDTO> getTargetPeriodDetailsWithTP8Current() {
        return List.of(
            TargetPeriodInfoDTO.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(BigDecimal.valueOf(10))
                .startDate(LocalDate.of(2026, 1, 1))
                .build(),
            TargetPeriodInfoDTO.builder()
                .id(2L)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(BigDecimal.valueOf(100))
                .startDate(LocalDate.of(2027, 1, 1))
                .build()
        );
    }

    private TargetUnitAccountDetailsDTO getTargetUnitAccountDetails(final Long id, final String businessId) {
        return TargetUnitAccountDetailsDTO.builder().id(id).businessId(businessId).sectorAssociationId(11L).build();
    }

    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_tp8_v1_BuyOut100(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(2L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(100))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_tp8_v2_Surplus80(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(80))
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .processed(false)
                .locked(false)
                .build();
    }

    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_tp8_v3_BuyOut200(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(4L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(200))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_tp8_v3d_Surplus100(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(4L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(100))
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex1_tp7(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataId(4L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(80))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex1_tp8(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(2L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(20))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(true)
                .locked(true)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex1_tp9(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(10))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(true)
                .locked(true)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex21_tp7(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataId(1L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(50))
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .processed(true)
                .locked(true)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex21_tp8(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(4L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(10))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex21_tp9(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(80))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(true)
                .locked(true)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex22_tp8(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(4L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(25))
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex4_tp7(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(120))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex4_tp8(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(2L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(100))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(true)
                .locked(true)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex5_tp7(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataId(4L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(50))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex5_tp8(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(10))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(true)
                .locked(true)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex6_tp7(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataId(4L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(30))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex6_tp8(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(2L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.ZERO)
                .surplusGained(BigDecimal.valueOf(50))
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                .processed(true)
                .locked(true)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex6_tp9(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .buyOutRequired(BigDecimal.valueOf(120))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(true)
                .locked(true)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex9_tp9(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP9)
                .processed(true)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex10_tp7(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataId(2L)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .processed(true)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex10_tp8(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataId(3L)
                .submissionDate(LocalDate.of(2026, 10, 10).atStartOfDay())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .buyOutRequired(BigDecimal.valueOf(80))
                .surplusGained(BigDecimal.ZERO)
                .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                .processed(false)
                .locked(false)
                .build();
    }
    
    private FacilityPerformanceDataWithBuyOutProcessedStatus getTpr_ex11_tp8(final String facilityBusinessId, final String facilitySiteName) {
        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .siteName(facilitySiteName)
                .targetPeriodType(TargetPeriodType.TP8)
                .processed(true)
                .locked(false)
                .build();
    }
    
    private static Map<String, BigDecimal> emptySurplusBucket() {
        return Map.of();
    }
    
    private Map<TargetPeriodType, List<BuyOutSurplusTransactionDetailSummary>> groupTransactionsByTargetPeriod(
            List<BuyOutSurplusTransactionDetailSummary> transactions) {

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        BuyOutSurplusTransactionDetailSummary::getTargetPeriodType
                ));
    }
    
    private Map<TargetPeriodType, BuyOutSurplusCalculationDetail> groupCalculationsByTargetPeriod(
            List<BuyOutSurplusCalculationDetail> calculations) {

        return calculations.stream()
                .collect(Collectors.toMap(
                        BuyOutSurplusCalculationDetail::getTargetPeriodType,
                        Function.identity()
                ));
    }
    
    @Test
    void shouldCompleteSuccessfullyWhenAccountHasNoErrorsAndFacilitySucceeded() {
        // Given
        String requestId = "request-123";
        String parentRequestId = "parent-request-123";
        Long accountId = 123L;
        
        List<BuyOutSurplusFacilityPerformanceResult> results = List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(1L)
                .succeeded(true)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(2L)
                .succeeded(false)
                .build());

        BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder()
                .accountId(accountId)
                .facilityPerformanceResults(results)
                .errors(new ArrayList<>())
                .build();
        
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = new BuyOutSurplusFacilityAccountProcessingRequestMetadata();
        metadata.setParentRequestId(parentRequestId);

        Request request = Request.builder()
                .metadata(metadata)
                .build();

        Request parentRequest = Request.builder()
                .payload(new BuyOutSurplusFacilityRunRequestPayload())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);

        service.complete(requestId, accountState);

        assertTrue(accountState.isSucceeded());
        assertNotNull(((BuyOutSurplusFacilityRunRequestPayload) parentRequest.getPayload()).getAccountStates().get(accountId));
    }

    @Test
    void shouldNotCompleteSuccessfullyWhenAccountHasErrors() {
        // Given
        String requestId = "request-123";
        String parentRequestId = "parent-request-123";
        Long accountId = 123L;
        
        List<BuyOutSurplusFacilityPerformanceResult> results = List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(1L)
                .succeeded(true)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(2L)
                .succeeded(false)
                .build());

        BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder()
                .accountId(accountId)
                .facilityPerformanceResults(results)
                .errors(List.of("Some error"))
                .build();
        
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = new BuyOutSurplusFacilityAccountProcessingRequestMetadata();
        metadata.setParentRequestId(parentRequestId);

        Request request = Request.builder()
                .metadata(metadata)
                .build();

        Request parentRequest = Request.builder()
                .payload(new BuyOutSurplusFacilityRunRequestPayload())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);

        service.complete(requestId, accountState);

        assertTrue(!accountState.isSucceeded());
        assertTrue(((BuyOutSurplusFacilityRunRequestPayload) parentRequest.getPayload()).getAccountStates().containsKey(accountId));
    }

    @Test
    void shouldNotCompleteSuccessfullyWhenNoFacilitySucceeded() {
        // Given
        String requestId = "request-123";
        String parentRequestId = "parent-request-123";
        Long accountId = 123L;
        
        List<BuyOutSurplusFacilityPerformanceResult> results = List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(1L)
                .succeeded(false)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(2L)
                .succeeded(false)
                .build());

        BuyOutSurplusFacilityAccountState accountState = BuyOutSurplusFacilityAccountState.builder()
                .accountId(accountId)
                .facilityPerformanceResults(results)
                .build();
        
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = new BuyOutSurplusFacilityAccountProcessingRequestMetadata();
        metadata.setParentRequestId(parentRequestId);

        Request request = Request.builder()
                .metadata(metadata)
                .build();

        Request parentRequest = Request.builder()
                .payload(new BuyOutSurplusFacilityRunRequestPayload())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);

        service.complete(requestId, accountState);

        assertTrue(!accountState.isSucceeded());
        assertTrue(((BuyOutSurplusFacilityRunRequestPayload) parentRequest.getPayload()).getAccountStates().containsKey(accountId));
    }

}