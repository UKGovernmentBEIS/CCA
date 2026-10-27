package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.common.domain.HistoryPayload;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.class, name = "AMOUNT_CHANGED"),
        @JsonSubTypes.Type(value = BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.class, name = "PAYMENT_STATUS_CHANGED"),
})
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BuyOutSurplusTransactionDetailHistoryPayload extends HistoryPayload {

    @NotNull
    private BuyOutSurplusTransactionDetailChangeType type;
}
