package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GreenfieldSiteEvidencePlaceholderAttachment extends PlaceholderAttachment {

    public GreenfieldSiteEvidencePlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.TARGET_PERIOD_DETAILS_GREENFIELD_EVIDENCE,
                "Greenfield site evidence placeholder.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

}
