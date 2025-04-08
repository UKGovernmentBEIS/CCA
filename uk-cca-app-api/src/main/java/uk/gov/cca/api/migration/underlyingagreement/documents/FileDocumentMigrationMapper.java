package uk.gov.cca.api.migration.underlyingagreement.documents;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.documents.domain.FileDocument;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {UUID.class, FileStatus.class, MigrationConstants.class})
public interface FileDocumentMigrationMapper {

    @Mapping(target = "uuid", expression = "java(UUID.randomUUID().toString())")
    // temporarily save file metadata
    @Mapping(target = "fileName", expression = "java(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(fileName))")
    @Mapping(target = "fileContent", expression = "java(fileName.getBytes())")
    @Mapping(target = "fileSize", expression = "java(fileName.length())")
    @Mapping(target = "fileType", expression = "java(org.springframework.util.StringUtils.getFilenameExtension(fileName))")
    @Mapping(target = "status", expression = "java(FileStatus.PENDING_MIGRATION)")
    @Mapping(target = "createdBy", expression = "java(MigrationConstants.MIGRATION_PROCESS_USER)")
    FileDocument toFileDocument(String fileName);

    List<FileDocument> toFileDocuments(List<String> fileNames);
}