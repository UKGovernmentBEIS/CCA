package uk.gov.cca.api.web.config.swagger;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload;
import uk.gov.netz.api.swagger.SwaggerSchemasAbstractProvider;

@Component
public class HistorySchemasProvider extends SwaggerSchemasAbstractProvider {

    @Override
    public void afterPropertiesSet() throws Exception {
        // Buy Out Surplus
        addResolvedShemas(BuyOutSurplusTransactionAmountChangedHistoryPayload.class.getSimpleName(), BuyOutSurplusTransactionAmountChangedHistoryPayload.class);
        addResolvedShemas(BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload.class.getSimpleName(), BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload.class);
    }
}
