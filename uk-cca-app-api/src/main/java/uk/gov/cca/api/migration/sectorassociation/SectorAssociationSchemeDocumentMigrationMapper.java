package uk.gov.cca.api.migration.sectorassociation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDocumentDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.FileStatus;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {FileStatus.class, MigrationConstants.class})
public interface SectorAssociationSchemeDocumentMigrationMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileName", expression = "java(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(sectorAssociationSchemeDocumentDTO.getFileName()))")
    @Mapping(target = "fileContent", expression = "java(sectorAssociationSchemeDocumentDTO.getFileName().getBytes())")
    @Mapping(target = "fileSize", expression = "java(sectorAssociationSchemeDocumentDTO.getFileName().length())")
    @Mapping(target = "fileType", expression = "java(org.springframework.util.StringUtils.getFilenameExtension(sectorAssociationSchemeDocumentDTO.getFileName()))")
    @Mapping(target = "status", expression = "java(FileStatus.PENDING_MIGRATION)")
    @Mapping(target = "createdBy", expression = "java(MigrationConstants.MIGRATION_PROCESS_USER)")
    SectorAssociationSchemeDocument toSectorAssociationSchemeDocument(SectorAssociationSchemeDocumentDTO sectorAssociationSchemeDocumentDTO);

}
