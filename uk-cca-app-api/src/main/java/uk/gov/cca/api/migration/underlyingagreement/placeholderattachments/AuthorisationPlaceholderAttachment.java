package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AuthorisationPlaceholderAttachment extends PlaceholderAttachment {

    public AuthorisationPlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.AUTHORISATION_AND_ADDITIONAL_EVIDENCE_AUTHORISATION,
                "Authorisation placeholder.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

}
