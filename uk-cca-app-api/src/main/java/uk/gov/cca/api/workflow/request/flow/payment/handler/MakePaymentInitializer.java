package uk.gov.cca.api.workflow.request.flow.payment.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.cca.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.cca.api.workflow.payment.service.BankAccountDetailsService;
import uk.gov.cca.api.workflow.payment.service.PaymentMethodService;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MakePaymentInitializer implements InitializeRequestTaskHandler {

    private final PaymentMethodService paymentMethodService;
    private final BankAccountDetailsService bankAccountDetailsService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        CompetentAuthorityEnum competentAuthority = request.getCompetentAuthority();
        Set<PaymentMethodType> paymentMethodTypes = paymentMethodService.getPaymentMethodTypesByCa(competentAuthority);

        PaymentMakeRequestTaskPayload paymentMakeRequestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PAYMENT_MAKE_PAYLOAD)
            .amount(request.getPayload().getPaymentAmount())
            .paymentRefNum(request.getId())
            .creationDate(LocalDate.now())
            .paymentMethodTypes(paymentMethodTypes)
            .build();
        
        if (paymentMethodTypes.contains(PaymentMethodType.BANK_TRANSFER)) {
            paymentMakeRequestTaskPayload.setBankAccountDetails(bankAccountDetailsService.getBankAccountDetailsByCa(competentAuthority));
        }

        return paymentMakeRequestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return RequestTaskType.getMakePaymentTypes();
    }
}
