package uk.gov.cca.api.workflow.request.flow.payment.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.workflow.request.flow.payment.domain.CardPaymentProcessResponseDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.cca.api.workflow.payment.domain.dto.PaymentGetResult;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CardPaymentMapper {

    CardPaymentProcessResponseDTO toCardPaymentProcessResponseDTO(PaymentGetResult paymentGetResult);
}
