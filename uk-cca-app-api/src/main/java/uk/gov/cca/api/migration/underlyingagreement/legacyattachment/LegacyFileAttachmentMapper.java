package uk.gov.cca.api.migration.underlyingagreement.legacyattachment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;

import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {StringUtils.class})
public interface LegacyFileAttachmentMapper {

    LegacyFileAttachment toLegacyFileAttachment(FileAttachment fileAttachment, String prefix);

    default List<LegacyFileAttachment> toLegacyFileAttachments(List<FileAttachment> fileAttachments, String prefix) {
        return fileAttachments.stream()
                .map(file -> toLegacyFileAttachment(file, prefix))
                .toList();
    }
}
