package uk.gov.cca.api.workflow.request.flow.payment.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.workflow.payment.domain.dto.PaymentGetResult;
import uk.gov.cca.api.workflow.payment.domain.dto.PaymentStateInfo;
import uk.gov.cca.api.workflow.request.flow.payment.domain.CardPaymentProcessResponseDTO;
import uk.gov.cca.api.workflow.request.flow.payment.domain.CardPaymentStateDTO;
import uk.gov.cca.api.workflow.request.flow.payment.transform.CardPaymentMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CardPaymentMapperTest {

    private final CardPaymentMapper cardPaymentMapper = Mappers.getMapper(CardPaymentMapper.class);

    @Test
    void toCardPaymentStateResponseDTO() {
        String paymentId = "n4brhul26f2hn1lt992ejj10ht";
        String nextUrl = "nextUrl";
        PaymentStateInfo paymentStateInfo = PaymentStateInfo.builder()
            .status("failed")
            .code("P0020")
            .message("Payment expired")
            .build();
        PaymentGetResult paymentGetResult = PaymentGetResult.builder()
            .paymentId(paymentId)
            .state(paymentStateInfo)
            .nextUrl(nextUrl)
            .build();

        //invoke
        CardPaymentProcessResponseDTO cardPaymentProcessResponseDTO =
            cardPaymentMapper.toCardPaymentProcessResponseDTO(paymentGetResult);

        //assert
        assertNotNull(cardPaymentProcessResponseDTO);
        assertEquals(paymentId, cardPaymentProcessResponseDTO.getPaymentId());
        assertEquals(nextUrl, cardPaymentProcessResponseDTO.getNextUrl());

        CardPaymentStateDTO cardPaymentStateDTO = cardPaymentProcessResponseDTO.getState();
        assertNotNull(cardPaymentStateDTO);
        assertEquals(paymentStateInfo.getStatus(), cardPaymentStateDTO.getStatus());
        assertEquals(paymentStateInfo.getCode(), cardPaymentStateDTO.getCode());
        assertEquals(paymentStateInfo.getMessage(), cardPaymentStateDTO.getMessage());
    }
}