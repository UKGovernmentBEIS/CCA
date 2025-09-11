package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import java.util.List;

import static uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType.AMOUNT_CHANGED;
import static uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType.PAYMENT_STATUS_CHANGED;

@Component
public class BuyOutSurplusTransactionHistoryTypesProvider implements JsonSubTypesProvider {

    @Override
    public List<NamedType> getTypes() {
        return List.of(
                new NamedType(BuyOutSurplusTransactionAmountChangedHistoryPayload.class, AMOUNT_CHANGED.name()),
                new NamedType(BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload.class, PAYMENT_STATUS_CHANGED.name())
        );
    }
}
