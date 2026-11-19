package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BuyOutSurplusTransactionReceivedAmountHistoryPayloadTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_negative_amount_valid() {

        BuyOutSurplusTransactionReceivedAmountHistoryPayload payload = BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .adjustmentAmount(BigDecimal.valueOf(-500))
                .previousReceivedAmount(BigDecimal.valueOf(1000))
                .paymentDate(LocalDate.now())
                .build();

        final Set<ConstraintViolation<BuyOutSurplusTransactionReceivedAmountHistoryPayload>> violations = validator.validate(payload);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_negative_amount_without_payment_date_valid() {

        BuyOutSurplusTransactionReceivedAmountHistoryPayload payload = BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .adjustmentAmount(BigDecimal.valueOf(-500))
                .previousReceivedAmount(BigDecimal.valueOf(1000))
                .build();

        final Set<ConstraintViolation<BuyOutSurplusTransactionReceivedAmountHistoryPayload>> violations = validator.validate(payload);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_all_fields_valid() {

        BuyOutSurplusTransactionReceivedAmountHistoryPayload payload = BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .adjustmentAmount(BigDecimal.valueOf(500))
                .previousReceivedAmount(BigDecimal.valueOf(300))
                .paymentDate(LocalDate.now())
                .build();

        final Set<ConstraintViolation<BuyOutSurplusTransactionReceivedAmountHistoryPayload>> violations = validator.validate(payload);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_payment_day_is_missing_invalid() {

        BuyOutSurplusTransactionReceivedAmountHistoryPayload payload = BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .adjustmentAmount(BigDecimal.valueOf(500))
                .previousReceivedAmount(BigDecimal.valueOf(300))
                .build();

        final Set<ConstraintViolation<BuyOutSurplusTransactionReceivedAmountHistoryPayload>> violations = validator.validate(payload);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{buyOutSurplus.transaction.paymentDate}");
    }

    @Test
    void validate_null_adjustmentAmount_invalid() {

        BuyOutSurplusTransactionReceivedAmountHistoryPayload payload = BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .previousReceivedAmount(BigDecimal.valueOf(300))
                .build();

        final Set<ConstraintViolation<BuyOutSurplusTransactionReceivedAmountHistoryPayload>> violations = validator.validate(payload);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }


    @Test
    void validate_adjustment_amount_is_zero_invalid() {

        BuyOutSurplusTransactionReceivedAmountHistoryPayload payload = BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .adjustmentAmount(BigDecimal.valueOf(0))
                .previousReceivedAmount(BigDecimal.valueOf(300))
                .build();

        final Set<ConstraintViolation<BuyOutSurplusTransactionReceivedAmountHistoryPayload>> violations = validator.validate(payload);

        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactly("{buyOutSurplus.transaction.adjustmentAmount}");
    }
}
