package uk.gov.cca.api.workflow.request.flow.payment;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public final class RequestTypeCardPaymentDescriptionMapper {

    static final Map<RequestType, String> cardPaymentDescriptions = new EnumMap<>(RequestType.class);

    public String getCardPaymentDescription(RequestType requestType) {
        return cardPaymentDescriptions.get(requestType);
    }
}
