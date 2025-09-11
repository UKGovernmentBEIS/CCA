package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionSummaryDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionDetailsMapperTest {
    
    private final BuyOutSurplusTransactionDetailsMapper mapper = Mappers.getMapper(BuyOutSurplusTransactionDetailsMapper.class);
    
    @Test
    void shouldMapAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate paymentDeadline = LocalDate.now().plusDays(30);
        
        BuyOutSurplusTransactionDTO transactionDTO = BuyOutSurplusTransactionDTO.builder()
                .id(123L)
                .performanceDataId(456L)
                .transactionCode("TX-123")
                .buyOutFee(new BigDecimal("100.50"))
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .fileDocumentUuid("doc-123")
                .creationDate(now)
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(new BigDecimal("200.00"))
                        .invoicedSurplusGained(new BigDecimal("500.00"))
                        .priBuyOutCost(new BigDecimal("300.00"))
                        .invoicedSurplusGained(new BigDecimal("0"))
                        .invoicedPreviousPaidFees(new BigDecimal("50.00"))
                        .invoicedPaymentDeadline(paymentDeadline)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .build())
                .build();
        
        PerformanceDataDetailsInfoDTO performanceDataDTO =
                PerformanceDataDetailsInfoDTO.builder()
                        .accountBusinessId("ACC-123")
                        .operatorName("Test Operator")
                        .reportVersion(1)
                        .submissionType(PerformanceDataSubmissionType.PRIMARY)
                        .priBuyOutCarbon(new BigDecimal("1000.00"))
                        .build();
        
        FileInfoDTO fileInfoDTO = FileInfoDTO.builder()
                .name("filename")
                .uuid("someUuid")
                .build();
        
        BuyOutSurplusTransactionDetailsDTO result = mapper.toBuyOutSurplusTransactionDetailsDTO(
                transactionDTO, performanceDataDTO, fileInfoDTO);
        
        assertNotNull(result);
        assertEquals(123L, result.getId());
        assertEquals("TX-123", result.getTransactionCode());
        assertEquals("ACC-123", result.getAccountBusinessId());
        assertEquals("Test Operator", result.getOperatorName());
        assertEquals("1", result.getReportVersion());
        assertEquals(PerformanceDataSubmissionType.PRIMARY, result.getSubmissionType());
        assertEquals("filename", result.getFileInfoDTO().getName());
        assertEquals("someUuid", result.getFileInfoDTO().getUuid());
        assertEquals(now, result.getCreationDate());
        assertEquals(paymentDeadline, result.getDueDate());
        assertEquals(BuyOutSurplusPaymentStatus.PAID, result.getPaymentStatus());
        assertEquals(BuyOutSurplusChargeType.FEE, result.getChargeType());
        assertEquals(new BigDecimal("1000.00"), result.getPriBuyOutCarbon());
        assertEquals(new BigDecimal("300.00"), result.getPriBuyOutCost());
        assertEquals(new BigDecimal("100.50"), result.getBuyOutFee());
        assertEquals(new BigDecimal("0"), result.getInvoicedSurplusGained());
        assertEquals(new BigDecimal("50.00"), result.getInvoicedPreviousPaidFees());
        assertEquals(new BigDecimal("200.00"), result.getInvoicedBuyOutFee());
    }

    @Test
    void toBuyOutSurplusTransactionSummaryDTO() {

        BuyOutSurplusTransaction entity = BuyOutSurplusTransaction.builder()
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .buyOutFee(BigDecimal.TEN)
                .transactionCode("CCA060021")
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(BigDecimal.TWO)
                        .build())
                .build();

        BuyOutSurplusTransactionSummaryDTO expected = BuyOutSurplusTransactionSummaryDTO.builder()
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .currentAmount(BigDecimal.TEN)
                .transactionCode("CCA060021")
                .initialAmount(BigDecimal.TWO)
                .build();

        BuyOutSurplusTransactionSummaryDTO actual = mapper.toBuyOutSurplusTransactionSummaryDTO(entity);

        assertEquals(expected, actual);
    }
}
