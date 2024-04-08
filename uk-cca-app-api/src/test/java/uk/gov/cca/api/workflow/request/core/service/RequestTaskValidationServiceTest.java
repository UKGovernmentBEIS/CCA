package uk.gov.cca.api.workflow.request.core.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.service.RequestTaskValidationService;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentTrackRequestTaskPayload;

import java.math.BigDecimal;

class RequestTaskValidationServiceTest {

    private RequestTaskValidationService requestTaskValidationService;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        requestTaskValidationService = new RequestTaskValidationService(validator);
    }

    @Test
    void validateRequestTaskPayload_is_valid() {
        PaymentTrackRequestTaskPayload requestTaskPayload = PaymentTrackRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PAYMENT_TRACK_PAYLOAD)
            .amount(BigDecimal.ZERO)
            .paymentRefNum("refNbr")
            .build();

        requestTaskValidationService.validateRequestTaskPayload(requestTaskPayload);
    }
}