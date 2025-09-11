package uk.gov.cca.api.migration.underlyingagreement.attachments;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.PlaceholderAttachment;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.FileStatus;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {UUID.class, FileStatus.class, MigrationConstants.class})
public interface FileAttachmentMigrationMapper {

    @Mapping(target = "uuid", expression = "java(UUID.randomUUID().toString())")
    // temporarily save file metadata
    @Mapping(target = "fileName", expression = "java(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(fileName))")
    @Mapping(target = "fileContent", expression = "java(fileName.getBytes())")
    @Mapping(target = "fileSize", expression = "java(fileName.length())")
    @Mapping(target = "fileType", expression = "java(org.springframework.util.StringUtils.getFilenameExtension(fileName))")
    @Mapping(target = "status", expression = "java(FileStatus.PENDING_MIGRATION)")
    @Mapping(target = "createdBy", expression = "java(MigrationConstants.MIGRATION_PROCESS_USER)")
    FileAttachment toFileAttachment(String fileName);
    
    @Mapping(target = "uuid", expression = "java(UUID.randomUUID().toString())")
    // temporarily save file metadata
    @Mapping(target = "fileName", expression = "java(org.apache.commons.text.StringEscapeUtils.unescapeHtml4(placeholderAttachment.getFileName()))")
    @Mapping(target = "fileContent", expression = "java(placeholderAttachment.getFileName().getBytes())")
    @Mapping(target = "fileSize", expression = "java(placeholderAttachment.getFileName().length())")
    @Mapping(target = "fileType", source = "fileType")
    @Mapping(target = "status", expression = "java(FileStatus.PENDING_MIGRATION)")
    @Mapping(target = "createdBy", expression = "java(MigrationConstants.MIGRATION_PROCESS_USER)")
    FileAttachment toFileAttachment(PlaceholderAttachment placeholderAttachment);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "fileContent", expression = "java(originalFileAttachment.getFileName().getBytes())")
    FileAttachment toFileAttachmentCopy(FileAttachment originalFileAttachment);
    
    List<FileAttachment> toFileAttachmentCopy(List<FileAttachment> originalFileAttachment);
    
    @AfterMapping
    default void updateFileType(@MappingTarget FileAttachment fileAttachment, String fileName) {
        Optional<FileType> fileType = Arrays.stream(FileType.values())
                .filter(type -> type.getSimpleType().equalsIgnoreCase(fileAttachment.getFileType()))
                .findFirst();
        if (fileType.isPresent()) {
            fileAttachment.setFileType(fileType.get().getMimeTypes().iterator().next());
        }
    }
}