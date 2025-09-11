package uk.gov.cca.api.subsistencefees.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.common.domain.HistoryPayload;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SubsistenceFeesMoaReceivedAmountHistoryPayload extends HistoryPayload {

    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @NotNull
    private BigDecimal transactionAmount;

    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @NotNull
    private BigDecimal previousReceivedAmount;
}
