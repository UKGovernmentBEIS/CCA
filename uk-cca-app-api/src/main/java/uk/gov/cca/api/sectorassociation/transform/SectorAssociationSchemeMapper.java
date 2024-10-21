package uk.gov.cca.api.sectorassociation.transform;

import org.mapstruct.*;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SectorAssociationSchemeMapper {

    SectorAssociationSchemeDTO sectorAssociationSchemeToDTO(SectorAssociationScheme sectorAssociationScheme);

    SectorAssociationScheme toSectorAssociationScheme(SectorAssociationSchemeDTO sectorAssociationSchemeDTO);
}
