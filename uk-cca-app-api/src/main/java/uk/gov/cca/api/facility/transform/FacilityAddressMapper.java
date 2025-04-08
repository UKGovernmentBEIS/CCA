package uk.gov.cca.api.facility.transform;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityAddressMapper {

    FacilityAddress toFacilityAddress(AccountAddressDTO accountAddressDTO);

    void setAddress(@MappingTarget FacilityAddress address, AccountAddressDTO accountAddressDTO);

}
