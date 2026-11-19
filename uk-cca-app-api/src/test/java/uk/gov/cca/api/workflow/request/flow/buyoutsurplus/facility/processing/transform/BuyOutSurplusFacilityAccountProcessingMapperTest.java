package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.transform;


import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityPerformanceDetail;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityTransactionSummary;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingMapperTest {
    
    private BuyOutSurplusFacilityAccountProcessingMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(BuyOutSurplusFacilityAccountProcessingMapper.class);
    }
    
    @Test
    void toSubmittedAction_onlySurplus() {
        final String parentRequestId = "BOS1";
        
        BuyOutSurplusFacilityPerformanceResult result1 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.TEN)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.TEN)
                        .chargeType(null)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult result2 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(2L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.TWO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.TWO)
                        .chargeType(null)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult result3 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(1)
                .performanceDataId(3L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.valueOf(100L))
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.TEN)
                        .chargeType(null)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityAccountProcessingRequestPayload payload =
            BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
            .facilityPerformanceResults(List.of(result1, result2, result3))
            .targetPeriodsDetails(getTargetPeriodDetailsWithTP8Current())
            .build();
    
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder()
                .parentRequestId(parentRequestId)
                .buyOutTransactionCodes(Map.of())
                .refundTransactionCodes(Map.of())
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail1 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .transactionCode(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.TEN)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.TEN)
                        .chargeType(null)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail2 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(2L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .transactionCode(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.TWO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.TWO)
                        .chargeType(null)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail3 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(1)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.valueOf(100L))
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.TEN)
                        .chargeType(null)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .build();
                
                
        
        BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload requestActionPayload = mapper
                .toSubmittedAction(payload, metadata);
        
        assertThat(requestActionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD);
        assertThat(requestActionPayload.getRunId()).isEqualTo(parentRequestId);
        assertThat(requestActionPayload.getSummary()).isEmpty();
        assertThat(requestActionPayload.getFacilities()).hasSize(3);
        assertThat(requestActionPayload.getFacilities()).containsExactlyInAnyOrder(detail1, detail2, detail3);
    }
    
    @Test
    void toSubmittedAction_buyOuts() {
        final String parentRequestId = "BOS1";
        
        BuyOutSurplusFacilityPerformanceResult result1 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.TEN)
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.TEN.multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult result2 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(1)
                .performanceDataId(2L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost()))
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult result3 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(1)
                .performanceDataId(3L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(10))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(10).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost()))
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityAccountProcessingRequestPayload payload =
            BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
            .facilityPerformanceResults(List.of(result1, result2, result3))
            .targetPeriodsDetails(getTargetPeriodDetailsWithTP8Current())
            .invoicedPaymentDeadlinePerTP(Map.of(TargetPeriodType.TP7, LocalDate.of(2026, 10, 10), TargetPeriodType.TP8, LocalDate.of(2026, 12, 12)))
            .buyOutOfficialNotices(Map.of(TargetPeriodType.TP7, FileInfoDTO.builder().uuid("uuid").build(), TargetPeriodType.TP8, FileInfoDTO.builder().uuid("uuid1").build()))
            .build();
    
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder()
                .parentRequestId(parentRequestId)
                .buyOutTransactionCodes(Map.of(TargetPeriodType.TP7, "CCA07001", TargetPeriodType.TP8, "CCA08001"))
                .refundTransactionCodes(Map.of())
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail1 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .transactionCode("CCA07001")
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.TEN)
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.TEN.multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail2 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(1)
                .performanceDataId(2L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .transactionCode("CCA08001")
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost()))
                        .build())
                .build();   
        
        BuyOutSurplusFacilityPerformanceDetail detail3 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .targetPeriodType(TargetPeriodType.TP8)
                .performanceDataReportVersion(1)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .transactionCode("CCA08001")
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(10))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(10).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost()))
                        .build())
                .build();         
        
        BuyOutSurplusFacilityTransactionSummary summary1 = BuyOutSurplusFacilityTransactionSummary.builder()
                .chargeType(BuyOutSurplusChargeType.FEE)
                .targetPeriodType(TargetPeriodType.TP7)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .totalFacilities(1L)
                .invoicedBuyOutFee(BigDecimal.valueOf(10).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                .dueDate(payload.getInvoicedPaymentDeadlinePerTP().get(TargetPeriodType.TP7))
                .officialNotice(FileInfoDTO.builder().uuid("uuid").build())
                .build();
        
        BuyOutSurplusFacilityTransactionSummary summary2 = BuyOutSurplusFacilityTransactionSummary.builder()
                .chargeType(BuyOutSurplusChargeType.FEE)
                .targetPeriodType(TargetPeriodType.TP8)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost())
                .totalFacilities(2L)
                .dueDate(payload.getInvoicedPaymentDeadlinePerTP().get(TargetPeriodType.TP8))
                .invoicedBuyOutFee(BigDecimal.valueOf(50).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP8).getBuyOutCost()))
                .officialNotice(FileInfoDTO.builder().uuid("uuid1").build())
                .build();
                
        
        BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload requestActionPayload = mapper
                .toSubmittedAction(payload, metadata);
        
        assertThat(requestActionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD);
        assertThat(requestActionPayload.getRunId()).isEqualTo(parentRequestId);
        assertThat(requestActionPayload.getSummary()).containsAll(List.of(summary1, summary2));
        assertThat(requestActionPayload.getFacilities()).hasSize(3);
        assertThat(requestActionPayload.getFacilities()).containsExactlyInAnyOrder(detail1, detail2, detail3);
    }
    
    @Test
    void toSubmittedAction_surplusAndBuyOutsAndRefunds() {
        final String parentRequestId = "BOS1";
        
        BuyOutSurplusFacilityPerformanceResult result1 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(2)
                .performanceDataId(2L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.TEN)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.valueOf(50))
                        .schemeTotalSurplus(BigDecimal.TEN)
                        .chargeType(null)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult result2 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(2)
                .performanceDataId(3L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult result3 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_3")
                .siteName("siteName_3")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(2)
                .performanceDataId(4L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.valueOf(60).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .invoicedBuyOutFee(BigDecimal.valueOf(20).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult result4 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_4")
                .siteName("siteName_4")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(2)
                .performanceDataId(5L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(80))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.valueOf(90).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .invoicedBuyOutFee(BigDecimal.valueOf(10).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityAccountProcessingRequestPayload payload =
            BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
            .facilityPerformanceResults(List.of(result1, result2, result3, result4))
            .targetPeriodsDetails(getTargetPeriodDetailsWithTP8Current())
            .invoicedPaymentDeadlinePerTP(Map.of(TargetPeriodType.TP7, LocalDate.of(2026, 10, 10), TargetPeriodType.TP8, LocalDate.of(2026, 12, 12)))
            .buyOutOfficialNotices(Map.of(TargetPeriodType.TP7, FileInfoDTO.builder().uuid("uuid").build()))
            .refundOfficialNotices(Map.of(TargetPeriodType.TP7, FileInfoDTO.builder().uuid("uuid1").build()))
            .build();
    
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder()
                .parentRequestId(parentRequestId)
                .buyOutTransactionCodes(Map.of(TargetPeriodType.TP7, "CCA07001"))
                .refundTransactionCodes(Map.of(TargetPeriodType.TP7, "CCA07002"))
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail1 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(2)
                .performanceDataId(2L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .transactionCode(null)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.TEN)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.valueOf(50))
                        .schemeTotalSurplus(BigDecimal.TEN)
                        .chargeType(null)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail2 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(2)
                .performanceDataId(3L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .transactionCode( "CCA07001")
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail3 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_3")
                .siteName("siteName_3")
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(2)
                .performanceDataId(4L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .transactionCode( "CCA07002")
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.valueOf(60).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .invoicedBuyOutFee(BigDecimal.valueOf(20).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .build())
                .build();
        
        BuyOutSurplusFacilityPerformanceDetail detail4 = BuyOutSurplusFacilityPerformanceDetail.builder()
                .facilityBusinessId("businessId_4")
                .siteName("siteName_4")
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(2)
                .performanceDataId(5L)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .transactionCode( "CCA07002")
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(80))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.valueOf(90).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .invoicedBuyOutFee(BigDecimal.valueOf(10).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                        .build())
                .build();
        
        BuyOutSurplusFacilityTransactionSummary summary1 = BuyOutSurplusFacilityTransactionSummary.builder()
                .chargeType(BuyOutSurplusChargeType.FEE)
                .targetPeriodType(TargetPeriodType.TP7)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .totalFacilities(1L)
                .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                .dueDate(payload.getInvoicedPaymentDeadlinePerTP().get(TargetPeriodType.TP7))
                .officialNotice(FileInfoDTO.builder().uuid("uuid").build())
                .build();
        
        BuyOutSurplusFacilityTransactionSummary summary2 = BuyOutSurplusFacilityTransactionSummary.builder()
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .targetPeriodType(TargetPeriodType.TP7)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost())
                .totalFacilities(2L)
                .dueDate(null)
                .invoicedBuyOutFee(BigDecimal.valueOf(30).multiply(getTargetPeriodDetailsByBusinessId(TargetPeriodType.TP7).getBuyOutCost()))
                .officialNotice(FileInfoDTO.builder().uuid("uuid1").build())
                .build();
                
        
        BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload requestActionPayload = mapper
                .toSubmittedAction(payload, metadata);
        
        assertThat(requestActionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD);
        assertThat(requestActionPayload.getRunId()).isEqualTo(parentRequestId);
        assertThat(requestActionPayload.getSummary()).containsAll(List.of(summary1, summary2));
        assertThat(requestActionPayload.getFacilities()).hasSize(4);
        assertThat(requestActionPayload.getFacilities()).containsExactlyInAnyOrder(detail1, detail2, detail3, detail4);
    }
    
    static List<TargetPeriodInfoDTO> getTargetPeriodDetailsWithTP8Current() {
        return List.of(
            TargetPeriodInfoDTO.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(BigDecimal.valueOf(10))
                .startDate(LocalDate.of(2026, 1, 1))
                .schemeVersion(SchemeVersion.CCA_3)
                .build(),
            TargetPeriodInfoDTO.builder()
                .id(2L)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(BigDecimal.valueOf(100))
                .startDate(LocalDate.of(2027, 1, 1))
                .schemeVersion(SchemeVersion.CCA_3)
                .build()
        );
    }
    
    static TargetPeriodInfoDTO getTargetPeriodDetailsByBusinessId(TargetPeriodType businessId) {
        return getTargetPeriodDetailsWithTP8Current().stream()
            .filter(dto -> dto.getBusinessId().equals(businessId))
            .findFirst()
            .orElse(null);
    }
}
