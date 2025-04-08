package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ManufacturingProcessDescriptionPlaceholderAttachment extends PlaceholderAttachment {

    public ManufacturingProcessDescriptionPlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.MANAGE_FACILITIES_MANUFACTURING_PROCESS_DESCRIPTION,
                "Manufacturing process description placeholder.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

}
