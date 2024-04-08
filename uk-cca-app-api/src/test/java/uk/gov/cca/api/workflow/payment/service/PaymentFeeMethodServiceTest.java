package uk.gov.cca.api.workflow.payment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.payment.service.PaymentFeeMethodService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.cca.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.cca.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentFeeMethodServiceTest {

    @InjectMocks
    private PaymentFeeMethodService paymentFeeService;

    @Mock
    private PaymentFeeMethodRepository paymentFeeMethodRepository;

    @Test
    void getMethodFeeType() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = mock(RequestType.class);
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(FeeMethodType.STANDARD)
            .build();

        when(paymentFeeMethodRepository.findByCompetentAuthorityAndRequestType(competentAuthority, requestType))
            .thenReturn(Optional.of(paymentFeeMethod));

        Optional<FeeMethodType> feeMethodType =
            paymentFeeService.getFeeMethodType(competentAuthority, requestType);

        assertEquals(Optional.of(FeeMethodType.STANDARD), feeMethodType);

    }

    @Test
    void getMethodFeeType_not_found() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        RequestType requestType = mock(RequestType.class);

        when(paymentFeeMethodRepository.findByCompetentAuthorityAndRequestType(competentAuthority, requestType))
            .thenReturn(Optional.empty());

        Optional<FeeMethodType> feeMethodType =
                paymentFeeService.getFeeMethodType(competentAuthority, requestType);

        assertTrue(feeMethodType.isEmpty());
    }
}