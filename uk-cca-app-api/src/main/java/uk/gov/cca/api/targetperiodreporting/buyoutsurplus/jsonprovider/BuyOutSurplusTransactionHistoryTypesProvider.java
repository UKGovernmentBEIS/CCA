package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.jsonprovider;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionReceivedAmountHistoryPayload;
import uk.gov.netz.api.common.config.jackson.JsonSubTypesProvider;

import java.util.List;

import static uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED;
import static uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailChangeType.AMOUNT_CHANGED;
import static uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailChangeType.PAYMENT_STATUS_CHANGED;

@Component
public class BuyOutSurplusTransactionHistoryTypesProvider implements JsonSubTypesProvider {

    @Override
    public List<NamedType> getTypes() {
        return List.of(
                // BuyOutSurplusTransactionDetailHistory
                new NamedType(BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.class, AMOUNT_CHANGED.name()),
                new NamedType(BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.class, PAYMENT_STATUS_CHANGED.name()),

                // BuyOutSurplusTransactionHistory
                new NamedType(BuyOutSurplusTransactionReceivedAmountHistoryPayload.class, RECEIVED_AMOUNT_CHANGED.name())
        );
    }
}
