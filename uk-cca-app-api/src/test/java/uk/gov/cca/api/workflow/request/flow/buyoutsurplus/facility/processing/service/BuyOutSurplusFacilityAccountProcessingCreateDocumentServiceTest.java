package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityTransactionDetailTemplateData;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityTransactionTemplateData;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingCreateDocumentServiceTest {
    
    @InjectMocks
    private BuyOutSurplusFacilityAccountProcessingCreateDocumentService createDocumentService;

    @Mock
    private CcaFileDocumentGeneratorService fileDocumentGeneratorService;
    
    @Mock
    private Request request;
    
    @Mock
    private BuyOutSurplusFacilityAccountProcessingRequestPayload payload;
    
    @Mock
    private BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata;
    
    BuyOutSurplusFacilityDocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    
    @BeforeEach
    void setUp() {
        documentTemplateTransformationMapper = new BuyOutSurplusFacilityDocumentTemplateTransformationMapper();
        createDocumentService = new BuyOutSurplusFacilityAccountProcessingCreateDocumentService(fileDocumentGeneratorService, 
                documentTemplateTransformationMapper);
        
        when(request.getPayload()).thenReturn(payload);
        when(request.getMetadata()).thenReturn(metadata);
    }
    
    @Test
    void create_primaryBuyOut() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final BuyOutSurplusChargeType chargeType = BuyOutSurplusChargeType.FEE;
        final FacilityAddressDTO facilityAddressDTO = FacilityAddressDTO.builder()
                .line1("Line 1")
                .line2("Line 2")
                .city("City")
                .county("County")
                .postcode("code")
                .country("Country")
                .build();
        final FacilityAddressDTO facilityAddressDTO2 = FacilityAddressDTO.builder()
                .line1("Line 1")
                .line2("Line 2")
                .city("City")
                .postcode("code")
                .country("Country")
                .build();
        
        Map<Long, FacilityAddressDTO> facilityAddressesById = Map.of(1L, facilityAddressDTO, 2L, facilityAddressDTO2);
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7a = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(1L)
                .facilityBusinessId("facilityBusinessId")
                .siteName("facilitySiteName")
                .targetPeriodType(targetPeriodType)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(1L)
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .chargeType(chargeType)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .build())
                .succeeded(true)
                .build();
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7b = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(2L)
                .facilityBusinessId("facilityBusinessId2")
                .siteName("facilitySiteName2")
                .targetPeriodType(targetPeriodType)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(2L)
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 15))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                    .buyOutRequired(BigDecimal.valueOf(70))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.valueOf(20))
                    .previousPaidFees(BigDecimal.ZERO)
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .chargeType(chargeType)
                    .invoicedBuyOutFee(BigDecimal.valueOf(700))
                    .build())
                .succeeded(true)
                .build();
        
        when(metadata.getBuyOutTransactionCodes()).thenReturn(Map.of(targetPeriodType, "CCA070001"));
        
        when(payload.getInvoicedPaymentDeadlinePerTP()).thenReturn(Map.of(targetPeriodType, LocalDate.of(2016, 10, 10)));
        when(payload.getFacilityPerformanceResults()).thenReturn(List.of(resultExpectedTP7a, resultExpectedTP7b));
        when(payload.isApplyPrimaryRules()).thenReturn(true);
        when(payload.getTargetPeriodsDetails()).thenReturn(List.of(TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .build()));
        when(payload.getSubmitterId()).thenReturn("sector1");
        
        BuyOutSurplusFacilityTransactionTemplateData transactionTemplateData = BuyOutSurplusFacilityTransactionTemplateData.builder()
                .transactionCode("CCA070001")
                .invoicedBuyOutFee(BigDecimal.valueOf(1500))
                .invoicedPaymentDeadline(LocalDate.of(2016, 10, 10))
                .targetPeriodType(targetPeriodType)
                .targetPeriodTypeNumber(targetPeriodType.getNumber())
                .build();
        
        List<BuyOutSurplusFacilityTransactionDetailTemplateData> transactionDetailTemplateDataList = 
                List.of(BuyOutSurplusFacilityTransactionDetailTemplateData.builder()
                        .facilityBusinessId("facilityBusinessId")
                        .siteName("facilitySiteName")
                        .facilityAddress("Line 1, Line 2, City, County, code")
                        .buyOutRequired(BigDecimal.valueOf(80))
                        .surplusUsed(BigDecimal.ZERO)
                        .invoicedBuyOutFee(BigDecimal.valueOf(800))
                        .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                        .previousPaidFees(BigDecimal.ZERO)
                        .performanceDataBuyOutFee(BigDecimal.valueOf(800))
                        .netShortFall(BigDecimal.valueOf(80))
                        .surplusGained(BigDecimal.ZERO)
                        .build(),
                        BuyOutSurplusFacilityTransactionDetailTemplateData.builder()
                        .facilityBusinessId("facilityBusinessId2")
                        .siteName("facilitySiteName2")
                        .facilityAddress("Line 1, Line 2, City, code")
                        .buyOutRequired(BigDecimal.valueOf(70))
                        .surplusUsed(BigDecimal.valueOf(20))
                        .invoicedBuyOutFee(BigDecimal.valueOf(700))
                        .performanceDataSubmissionDate(LocalDate.of(2026, 10, 15))
                        .previousPaidFees(BigDecimal.ZERO)
                        .performanceDataBuyOutFee(BigDecimal.valueOf(700))
                        .netShortFall(BigDecimal.valueOf(50))
                        .surplusGained(BigDecimal.ZERO)
                        .build());
        
        
        final TemplateParams templateParams = new TemplateParams();
        templateParams.getParams().put("transaction", transactionTemplateData);
        templateParams.getParams().put("transactionDetails", transactionDetailTemplateDataList);
       

        // invoke
        createDocumentService.create(request, targetPeriodType, chargeType, facilityAddressesById);
        
        verify(fileDocumentGeneratorService, times(1)).generateAsync(request, "sector1",
                CcaDocumentTemplateType.PRIMARY_BUY_OUT_CCA3,
                templateParams,
                "CCA070001 Primary buy-out MoA.pdf");
    }
    
    @Test
    void create_secondaryBuyOut() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final BuyOutSurplusChargeType chargeType = BuyOutSurplusChargeType.FEE;
        final FacilityAddressDTO facilityAddressDTO = FacilityAddressDTO.builder()
                .line1("Line 1")
                .line2("Line 2")
                .city("City")
                .county("County")
                .postcode("code")
                .country("Country")
                .build();
        
        Map<Long, FacilityAddressDTO> facilityAddressesById = Map.of(1L, facilityAddressDTO);
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(1L)
                .facilityBusinessId("facilityBusinessId")
                .siteName("facilitySiteName")
                .targetPeriodType(targetPeriodType)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(1L)
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                    .buyOutRequired(BigDecimal.valueOf(80))
                    .surplusGained(BigDecimal.ZERO)
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(200))
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.ZERO)
                    .chargeType(chargeType)
                    .invoicedBuyOutFee(BigDecimal.valueOf(800))
                    .build())
                .succeeded(true)
                .build();
        
        when(metadata.getBuyOutTransactionCodes()).thenReturn(Map.of(targetPeriodType, "CCA070001"));
        
        when(payload.getInvoicedPaymentDeadlinePerTP()).thenReturn(Map.of(targetPeriodType, LocalDate.of(2016, 10, 10)));
        when(payload.getFacilityPerformanceResults()).thenReturn(List.of(resultExpectedTP7));
        when(payload.isApplyPrimaryRules()).thenReturn(true);
        when(payload.getTargetPeriodsDetails()).thenReturn(List.of(TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .build(),
                TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP8)
                .build()));
        when(payload.getSubmitterId()).thenReturn("sector1");
        
        BuyOutSurplusFacilityTransactionTemplateData transactionTemplateData = BuyOutSurplusFacilityTransactionTemplateData.builder()
                .transactionCode("CCA070001")
                .invoicedBuyOutFee(BigDecimal.valueOf(800))
                .invoicedPaymentDeadline(LocalDate.of(2016, 10, 10))
                .targetPeriodType(targetPeriodType)
                .targetPeriodTypeNumber(targetPeriodType.getNumber())
                .build();
        
        List<BuyOutSurplusFacilityTransactionDetailTemplateData> transactionDetailTemplateDataList = 
                List.of(BuyOutSurplusFacilityTransactionDetailTemplateData.builder()
                        .facilityBusinessId("facilityBusinessId")
                        .siteName("facilitySiteName")
                        .facilityAddress("Line 1, Line 2, City, County, code")
                        .buyOutRequired(BigDecimal.valueOf(80))
                        .surplusUsed(BigDecimal.ZERO)
                        .invoicedBuyOutFee(BigDecimal.valueOf(800))
                        .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                        .previousPaidFees(BigDecimal.valueOf(200))
                        .performanceDataBuyOutFee(BigDecimal.valueOf(800))
                        .netShortFall(BigDecimal.valueOf(80))
                        .surplusGained(BigDecimal.ZERO)
                        .build());
        
        
        final TemplateParams templateParams = new TemplateParams();
        templateParams.getParams().put("transaction", transactionTemplateData);
        templateParams.getParams().put("transactionDetails", transactionDetailTemplateDataList);
       

        // invoke
        createDocumentService.create(request, targetPeriodType, chargeType, facilityAddressesById);
        
        verify(fileDocumentGeneratorService, times(1)).generateAsync(request, "sector1",
                CcaDocumentTemplateType.SECONDARY_BUY_OUT_CCA3,
                templateParams,
                "CCA070001 Secondary buy-out MoA.pdf");
    }
    
    @Test
    void create_secondaryRefund() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final BuyOutSurplusChargeType chargeType = BuyOutSurplusChargeType.REFUND;
        final FacilityAddressDTO facilityAddressDTO = FacilityAddressDTO.builder()
                .line1("Line 1")
                .line2("Line 2")
                .city("City")
                .county("County")
                .postcode("code")
                .country("Country")
                .build();
        
        Map<Long, FacilityAddressDTO> facilityAddressesById = Map.of(1L, facilityAddressDTO);
        
        BuyOutSurplusFacilityPerformanceResult resultExpectedTP7 = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityId(1L)
                .facilityBusinessId("facilityBusinessId")
                .siteName("facilitySiteName")
                .targetPeriodType(targetPeriodType)
                .performanceDataReportVersion(0)
                .processed(false)
                .performanceDataId(1L)
                .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                    .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                    .buyOutRequired(BigDecimal.ZERO)
                    .surplusGained(BigDecimal.valueOf(40))
                    .surplusUsed(BigDecimal.ZERO)
                    .previousPaidFees(BigDecimal.valueOf(200))
                    .previousSurplusUsed(BigDecimal.ZERO)
                    .previousSurplusGained(BigDecimal.ZERO)
                    .schemeTotalSurplus(BigDecimal.valueOf(40))
                    .chargeType(chargeType)
                    .invoicedBuyOutFee(BigDecimal.valueOf(200))
                    .build())
                .succeeded(true)
                .build();
        
        when(metadata.getRefundTransactionCodes()).thenReturn(Map.of(targetPeriodType, "CCA070001"));
        
        when(payload.getFacilityPerformanceResults()).thenReturn(List.of(resultExpectedTP7));
        when(payload.isApplyPrimaryRules()).thenReturn(true);
        when(payload.getTargetPeriodsDetails()).thenReturn(List.of(TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .build(),
                TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP8)
                .build()));
        when(payload.getSubmitterId()).thenReturn("sector1");
        
        BuyOutSurplusFacilityTransactionTemplateData transactionTemplateData = BuyOutSurplusFacilityTransactionTemplateData.builder()
                .transactionCode("CCA070001")
                .invoicedBuyOutFee(BigDecimal.valueOf(200))
                .targetPeriodType(targetPeriodType)
                .targetPeriodTypeNumber(targetPeriodType.getNumber())
                .build();
        
        List<BuyOutSurplusFacilityTransactionDetailTemplateData> transactionDetailTemplateDataList = 
                List.of(BuyOutSurplusFacilityTransactionDetailTemplateData.builder()
                        .facilityBusinessId("facilityBusinessId")
                        .siteName("facilitySiteName")
                        .facilityAddress("Line 1, Line 2, City, County, code")
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .invoicedBuyOutFee(BigDecimal.valueOf(200))
                        .performanceDataSubmissionDate(LocalDate.of(2026, 10, 10))
                        .previousPaidFees(BigDecimal.valueOf(200))
                        .performanceDataBuyOutFee(BigDecimal.ZERO)
                        .netShortFall(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.valueOf(40))
                        .build());
        
        
        final TemplateParams templateParams = new TemplateParams();
        templateParams.getParams().put("transaction", transactionTemplateData);
        templateParams.getParams().put("transactionDetails", transactionDetailTemplateDataList);
       

        // invoke
        createDocumentService.create(request, targetPeriodType, chargeType, facilityAddressesById);
        
        verify(fileDocumentGeneratorService, times(1)).generateAsync(request, "sector1",
                CcaDocumentTemplateType.SECONDARY_OVERPAYMENT_BUY_OUT_CCA3,
                templateParams,
                "CCA070001 Secondary buy-out overpayment letter.pdf");
    }

}
