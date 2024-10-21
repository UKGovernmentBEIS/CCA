package uk.gov.cca.api.sectorassociation.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SubsectorAssociationSchemeMapper {

    SubsectorAssociationSchemeDTO subsectorAssociationSchemeToDTO(SubsectorAssociationScheme subsectorAssociationScheme);

    SubsectorAssociationScheme toSubsectorAssociationScheme(SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO);
}
