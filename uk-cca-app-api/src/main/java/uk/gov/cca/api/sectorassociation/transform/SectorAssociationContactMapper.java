package uk.gov.cca.api.sectorassociation.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, uses = LocationMapper.class)
public interface SectorAssociationContactMapper {

    @Mapping(source = "location", target = "address")
    SectorAssociationContactDTO sectorAssociationContactToDTO(SectorAssociationContact contact);

    @Mapping(source = "address", target = "location")
    SectorAssociationContact toSectorAssociationContact(SectorAssociationContactDTO contactDTO);
}
