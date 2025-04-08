package uk.gov.cca.api.migration.underlyingagreement.attachments;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;

@UtilityClass
public class FileAttachmentUtil {
    
    public List<FileAttachment> startsWith(List<FileAttachment> files, String businessId, LegacyFileAttachmentType attachmentType) {
        final String prefix = String.join(" ", businessId, attachmentType.getIndex());
        return startsWith(files, prefix);
    }
    
    public List<FileAttachment> startsWith(List<FileAttachment> files, String prefix) {
        List<FileAttachment> results = new ArrayList<>();
        files.stream().forEach(file -> {
            if (file.getFileName().toLowerCase().startsWith(prefix.toLowerCase())) {
                results.add(file);
            }
        });
        return results;
    }
    
}
