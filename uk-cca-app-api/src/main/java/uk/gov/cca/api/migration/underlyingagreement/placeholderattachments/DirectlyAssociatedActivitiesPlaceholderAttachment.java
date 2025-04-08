package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DirectlyAssociatedActivitiesPlaceholderAttachment extends PlaceholderAttachment {

    public DirectlyAssociatedActivitiesPlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.MANAGE_FACILITIES_DIRECTLY_ASSOCIATED_ACTIVITIES_DESCRIPTION,
                "Directly associated activities placeholder.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

}
