package uk.gov.cca.api.workflow.payment.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.workflow.payment.domain.BankAccountDetails;
import uk.gov.cca.api.workflow.payment.domain.dto.BankAccountDetailsDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BankAccountDetailsMapper {

    BankAccountDetailsDTO toBankAccountDetailsDTO(BankAccountDetails bankAccountDetails);
}
