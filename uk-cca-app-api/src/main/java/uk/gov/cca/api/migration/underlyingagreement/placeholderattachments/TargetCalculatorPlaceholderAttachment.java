package uk.gov.cca.api.migration.underlyingagreement.placeholderattachments;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TargetCalculatorPlaceholderAttachment extends PlaceholderAttachment {

    public TargetCalculatorPlaceholderAttachment(String businessId) {
        super(businessId, 
                LegacyFileAttachmentType.TARGET_PERIOD_DETAILS_TARGET_CALCULATOR_FILE,
                "TP6 target calculator placeholder.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

}
