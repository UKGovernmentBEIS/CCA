package uk.gov.cca.api.sectorassociation.transform;

import org.mapstruct.Mapper;

import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface LocationMapper {

    AddressDTO locationToAddressDTO(Location location);

    Location addressDTOToLocation(AddressDTO addressDTO);
}
