package uk.gov.cca.api.workflow.request.flow.payment;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.cca.api.workflow.request.flow.payment.RequestTypeCardPaymentDescriptionMapper;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class RequestTypeCardPaymentDescriptionMapperTest {

    @Test
    void getCardPaymentDescription() {
        assertNull(RequestTypeCardPaymentDescriptionMapper.getCardPaymentDescription(mock(RequestType.class)));
    }
}