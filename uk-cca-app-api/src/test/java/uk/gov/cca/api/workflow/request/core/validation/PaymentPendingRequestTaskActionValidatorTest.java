package uk.gov.cca.api.workflow.request.core.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.validation.PaymentPendingRequestTaskActionValidator;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class PaymentPendingRequestTaskActionValidatorTest {

    @InjectMocks
    private PaymentPendingRequestTaskActionValidator validator;

    @Test
    void getTypes() {
        Set<RequestTaskActionType> requestTaskActionTypes = new HashSet<>();
        requestTaskActionTypes.addAll(RequestTaskActionType.getNotifyOperatorForDecisionTypesBlockedByPayment());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRequestPeerReviewTypesBlockedByPayment());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRfiRdeSubmissionTypes());

        assertEquals(requestTaskActionTypes, validator.getTypes());
    }

}