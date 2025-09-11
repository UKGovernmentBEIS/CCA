package uk.gov.cca.api.sectorassociation.transform;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemesDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", uses = {SubsectorAssociationSchemeMapper.class}, config = MapperConfig.class)
public interface SectorAssociationSchemeMapper {
    
    SectorAssociationSchemeDTO toSectorAssociationSchemeDTO(SectorAssociationScheme sectorAssociationScheme);

    @Mapping(target = "subsectorAssociations", source = "subsectors")
	SectorAssociationSchemesDTO toSectorAssociationSchemesDTO(
			Map<SchemeVersion, SectorAssociationSchemeDTO> sectorAssociationSchemeMap,
			List<SubsectorAssociation> subsectors);
}
