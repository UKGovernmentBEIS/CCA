package uk.gov.cca.api.sectorassociation.transform;

import java.util.Map;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeInfo;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemesDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SubsectorAssociationSchemeMapper {

    SubsectorAssociationSchemeInfo toSubsectorAssociationSchemeInfo(SubsectorAssociationScheme subsectorAssociationScheme);

    SubsectorAssociationSchemesDTO toSubsectorAssociationSchemesDTO(String name, Map<SchemeVersion, SubsectorAssociationSchemeDTO> subsectorAssociationSchemeMap);

    @Mapping(target = "editable", expression = "java(subsectorAssociationScheme.getSchemeVersion() != SchemeVersion.CCA_2 && hasEditPermission)")
    SubsectorAssociationSchemeDTO toSubsectorAssociationSchemeDTO(SubsectorAssociationScheme subsectorAssociationScheme, boolean hasEditPermission);

    SubsectorAssociationInfoDTO toSubsectorAssociationInfoDTO(SubsectorAssociation subsectorAssociation);
}
