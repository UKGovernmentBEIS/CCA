package uk.gov.cca.api.migration.underlyingagreement.legacyattachment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegacyFileAttachment {

    private FileAttachment fileAttachment;
    private String prefix;
}
