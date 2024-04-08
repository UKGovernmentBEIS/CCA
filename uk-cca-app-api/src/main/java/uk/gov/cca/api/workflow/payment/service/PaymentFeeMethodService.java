package uk.gov.cca.api.workflow.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.cca.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentFeeMethodService {

    private final PaymentFeeMethodRepository paymentFeeMethodRepository;

    public Optional<FeeMethodType> getFeeMethodType(CompetentAuthorityEnum competentAuthority, RequestType requestType) {
        return paymentFeeMethodRepository.findByCompetentAuthorityAndRequestType(competentAuthority, requestType)
                .map(PaymentFeeMethod::getType)
                .or(Optional::empty);
    }
}
