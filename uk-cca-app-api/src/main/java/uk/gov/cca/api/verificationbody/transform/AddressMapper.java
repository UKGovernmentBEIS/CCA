package uk.gov.cca.api.verificationbody.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.verificationbody.domain.Address;
import uk.gov.cca.api.verificationbody.domain.dto.AddressDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AddressMapper {

    Address toAddress(AddressDTO addressDto);
}
