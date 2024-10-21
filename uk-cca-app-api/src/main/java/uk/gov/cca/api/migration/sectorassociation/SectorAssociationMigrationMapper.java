package uk.gov.cca.api.migration.sectorassociation;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.FileStatus;

@Mapper(componentModel = "spring", config = MapperConfig.class, uses={SectorAssociationSchemeDocumentMigrationMapper.class}, imports = {FileStatus.class, MigrationConstants.class})
public interface SectorAssociationMigrationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "targetSet.id", ignore = true)
    @Mapping(target = "subsectorAssociationSchemes", ignore = true)
    SectorAssociationScheme toSectorAssociationScheme(SectorAssociationSchemeDTO sectorAssociationSchemeDTO, SectorAssociation sectorAssociation);

    @AfterMapping
    default void setTargetSetToTargetCommitments(@MappingTarget SectorAssociationScheme sectorAssociationScheme) {
        if (sectorAssociationScheme.getTargetSet() != null) {
            sectorAssociationScheme.getTargetSet().getTargetCommitments()
                    .forEach(c -> c.setTargetSet(sectorAssociationScheme.getTargetSet()));
        }
    }
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sectorAssociationScheme.targetSet", ignore = true)
    @Mapping(target = "targetSet", source = "subsectorAssociationSchemeDTO.targetSet")
    SubsectorAssociationScheme toSubsectorAssociationScheme(SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO, SectorAssociationScheme sectorAssociationScheme);
    
    @AfterMapping
    default void setTargetSet(@MappingTarget SubsectorAssociationScheme subsectorAssociationScheme) {
        subsectorAssociationScheme.getTargetSet().setId(null);
        subsectorAssociationScheme.getTargetSet().getTargetCommitments().forEach(c -> c.setTargetSet(subsectorAssociationScheme.getTargetSet()));
    }
}
