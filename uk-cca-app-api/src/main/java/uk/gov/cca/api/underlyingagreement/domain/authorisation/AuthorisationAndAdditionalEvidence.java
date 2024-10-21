package uk.gov.cca.api.underlyingagreement.domain.authorisation;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementSection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorisationAndAdditionalEvidence implements UnderlyingAgreementSection {

    @NotEmpty
    @Builder.Default
    private Set<UUID> authorisationAttachmentIds = new HashSet<>();

    @Builder.Default
    private Set<UUID> additionalEvidenceAttachmentIds = new HashSet<>();
}
