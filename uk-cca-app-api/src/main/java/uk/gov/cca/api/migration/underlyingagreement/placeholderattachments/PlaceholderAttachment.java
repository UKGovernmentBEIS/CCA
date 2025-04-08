package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
public abstract class PlaceholderAttachment {
    private String businessId;
    private LegacyFileAttachmentType type;
    private String fileName;
    private String fileType;
    
    public PlaceholderAttachment(String businessId, LegacyFileAttachmentType type, String fileName, String fileType) {
        this.businessId = businessId;
        this.type = type;
        this.fileName =  businessId + " " + type.getIndex() + " " + fileName;
        this.fileType =  fileType;
    }
}
