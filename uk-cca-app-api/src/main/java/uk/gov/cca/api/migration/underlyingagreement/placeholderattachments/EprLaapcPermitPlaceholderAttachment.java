package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EprLaapcPermitPlaceholderAttachment extends PlaceholderAttachment {

    public EprLaapcPermitPlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.MANAGE_FACILITIES_PERMIT_FILE,
                "EPR LAAPC permit placeholder.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

}
