package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionMapperTest {

    private final BuyOutSurplusTransactionMapper mapper = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);

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
