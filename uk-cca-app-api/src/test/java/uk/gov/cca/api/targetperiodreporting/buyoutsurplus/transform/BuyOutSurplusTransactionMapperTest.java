package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.AccountBuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionMapperTest {

    private final BuyOutSurplusTransactionMapper mapper = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);

    @Test
    void toBuyOutSurplusTransaction() {
        final BuyOutSurplusContainer container = BuyOutSurplusContainer.builder()
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedSurplusGained(BigDecimal.ZERO)
                .priBuyOutCost(BigDecimal.ONE)
                .invoicedPreviousPaidFees(BigDecimal.TWO)
                .build();
        final AccountBuyOutSurplusTransactionCreate dto = AccountBuyOutSurplusTransactionCreate.builder()
                .transactionCode("CCA060020")
                .accountId(11L)
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataId(1L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .buyOutSurplusContainer(container)
                .build();

        final BuyOutSurplusTransaction expected = BuyOutSurplusTransaction.builder()
                .transactionCode("CCA060020")
                .accountId(11L)
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .invoicedBuyOutFee(BigDecimal.TEN)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("fileDocumentUuid")
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .build();
        final BuyOutSurplusTransactionDetail expectedTransactionDetail = BuyOutSurplusTransactionDetail.builder()
                .transaction(expected)
                .performanceDataId(1L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .buyOutSurplusContainer(container)
                .build();

        // Invoke
        BuyOutSurplusTransaction result = mapper.toBuyOutSurplusTransaction(dto);

        // Verify
        assertThat(result.getTransactionCode()).isEqualTo(expected.getTransactionCode());
        assertThat(result.getAccountId()).isEqualTo(expected.getAccountId());
        assertThat(result.getChargeType()).isEqualTo(expected.getChargeType());
        assertThat(result.getInvoicedBuyOutFee()).isEqualTo(expected.getInvoicedBuyOutFee());
        assertThat(result.getInvoicedPaymentDeadline()).isEqualTo(expected.getInvoicedPaymentDeadline());
        assertThat(result.getFileDocumentUuid()).isEqualTo(expected.getFileDocumentUuid());
        assertThat(result.getPerformanceDataResourceType()).isEqualTo(expected.getPerformanceDataResourceType());
        assertThat(result.getTransactionDetailList()).hasSize(1);
        assertThat(result.getTransactionDetailList().getFirst().getPerformanceDataId()).isEqualTo(expectedTransactionDetail.getPerformanceDataId());
        assertThat(result.getTransactionDetailList().getFirst().getBuyOutFee()).isEqualTo(expectedTransactionDetail.getBuyOutFee());
        assertThat(result.getTransactionDetailList().getFirst().getPaymentStatus()).isEqualTo(expectedTransactionDetail.getPaymentStatus());
        assertThat(result.getTransactionDetailList().getFirst().getBuyOutSurplusContainer()).isEqualTo(expectedTransactionDetail.getBuyOutSurplusContainer());
    }

    @Test
    void toBuyOutSurplusTransactionDetailInfoDTO() {
        final BuyOutSurplusTransactionDetail entity = BuyOutSurplusTransactionDetail.builder()
                .id(1L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .build();

        final BuyOutSurplusTransactionDetailInfoDTO expected = BuyOutSurplusTransactionDetailInfoDTO.builder()
                .id(1L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .build();
        // Invoke
        BuyOutSurplusTransactionDetailInfoDTO result = mapper.toBuyOutSurplusTransactionDetailInfoDTO(entity);

        // Verify
        assertThat(result).isEqualTo(expected);
    }
}
