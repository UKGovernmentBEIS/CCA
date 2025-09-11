package uk.gov.cca.api.migration.sectorassociation;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDocumentDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.FileStatus;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {FileStatus.class, MigrationConstants.class})
public interface SectorAssociationMigrationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileName", expression = "java(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(sectorAssociationSchemeDocumentDTO.getFileName()))")
    @Mapping(target = "fileContent", expression = "java(sectorAssociationSchemeDocumentDTO.getFileName().getBytes())")
    @Mapping(target = "fileSize", expression = "java(sectorAssociationSchemeDocumentDTO.getFileName().length())")
    @Mapping(target = "fileType", expression = "java(org.springframework.util.StringUtils.getFilenameExtension(sectorAssociationSchemeDocumentDTO.getFileName()))")
    @Mapping(target = "status", expression = "java(FileStatus.PENDING_MIGRATION)")
    @Mapping(target = "createdBy", expression = "java(MigrationConstants.MIGRATION_PROCESS_USER)")
    SectorAssociationSchemeDocument toSectorAssociationSchemeDocument(SectorAssociationSchemeDocumentDTO sectorAssociationSchemeDocumentDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "targetSet.id", ignore = true)
    SectorAssociationScheme toSectorAssociationScheme(SectorAssociationSchemeDTO sectorAssociationSchemeDTO, SectorAssociation sectorAssociation);

    @AfterMapping
    default void setTargetSetToTargetCommitments(@MappingTarget SectorAssociationScheme sectorAssociationScheme) {
        if (sectorAssociationScheme.getTargetSet() != null) {
            sectorAssociationScheme.getTargetSet().getTargetCommitments()
                    .forEach(c -> c.setTargetSet(sectorAssociationScheme.getTargetSet()));
        }
    }
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "targetSet", source = "subsectorAssociationSchemeDTO.targetSet")
    @Mapping(target = "subsectorAssociation", source = "newSubsector")
    SubsectorAssociationScheme toSubsectorAssociationScheme(SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO, SubsectorAssociation newSubsector);
    
    @AfterMapping
    default void setTargetSet(@MappingTarget SubsectorAssociationScheme subsectorAssociationScheme) {
        subsectorAssociationScheme.getTargetSet().setId(null);
        subsectorAssociationScheme.getTargetSet().getTargetCommitments().forEach(c -> c.setTargetSet(subsectorAssociationScheme.getTargetSet()));
    }
}
