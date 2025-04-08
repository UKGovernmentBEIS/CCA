package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class EligibleProcessDescriptionPlaceholderAttachment extends PlaceholderAttachment {

    public EligibleProcessDescriptionPlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.MANAGE_FACILITIES_ELIGIBLE_PROCESS_DESCRIPTION,
                "Eligible process description placeholder.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

}
