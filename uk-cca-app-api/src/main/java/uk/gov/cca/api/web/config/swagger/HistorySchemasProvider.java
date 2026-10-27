package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;

@Component
public class HistorySchemasProvider extends SwaggerSchemasAbstractProvider {

    @Override
    public void afterPropertiesSet() throws Exception {
        // Buy Out Surplus
        addResolvedShemas(BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.class.getSimpleName(), BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.class);
        addResolvedShemas(BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.class.getSimpleName(), BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.class);
    }
}
