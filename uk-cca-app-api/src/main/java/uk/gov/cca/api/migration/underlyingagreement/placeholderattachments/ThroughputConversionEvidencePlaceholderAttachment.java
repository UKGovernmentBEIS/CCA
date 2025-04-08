package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ThroughputConversionEvidencePlaceholderAttachment extends PlaceholderAttachment {

    public ThroughputConversionEvidencePlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.TARGET_PERIOD_DETAILS_CONVERSION_EVIDENCE,
                "Throughput conversion evidence placeholder.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

}
