package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Rule70EvidencePlaceholderAttachment extends PlaceholderAttachment {

    public Rule70EvidencePlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.MANAGE_FACILITIES_EVIDENCE,
                "70 30 evidence placeholder.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

}
