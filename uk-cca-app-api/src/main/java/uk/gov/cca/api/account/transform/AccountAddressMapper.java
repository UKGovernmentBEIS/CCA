package uk.gov.cca.api.account.transform;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountAddressMapper {

    AccountAddress toAccountAddress(AccountAddressDTO accountAddressDTO);

    void setAddress(@MappingTarget AccountAddress address, AccountAddressDTO accountAddressDTO);

    AccountAddressDTO toAccountAddressDTO(AccountAddress accountAddress);
}
