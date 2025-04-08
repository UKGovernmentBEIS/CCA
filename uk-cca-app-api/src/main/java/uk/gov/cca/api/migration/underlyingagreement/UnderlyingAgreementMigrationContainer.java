package uk.gov.cca.api.migration.underlyingagreement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachment;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementMigrationContainer {
    
    private static final int DEFAULT_CONSOLIDATION_NUMBER_VERSION = 1;
    
    private Long persistentAccountId;

    @Builder.Default
    private List<LegacyFileAttachment> fileAttachments = new ArrayList<>();
    
    private LocalDateTime activationDate;
    private UnderlyingAgreementContainer underlyingAgreementContainer;
    @Builder.Default
    private int consolidationNumber = DEFAULT_CONSOLIDATION_NUMBER_VERSION;
    
    @Builder.Default
    Map<String, LocalDateTime> facilitiesCreatedDate = new HashMap<>();
    
    // Underlying Agreement document
    private FileInfoDTO fileDocument;
}
