package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AnnotatedSitePlanPlaceholderAttachment extends PlaceholderAttachment {

    public AnnotatedSitePlanPlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.MANAGE_FACILITIES_ANNOTATED_SITE_PLANS,
                "Annotated site plan placeholder.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
}
