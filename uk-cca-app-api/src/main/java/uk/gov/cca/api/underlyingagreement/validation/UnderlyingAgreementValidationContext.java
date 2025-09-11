package uk.gov.cca.api.underlyingagreement.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementValidationContext {
    private SchemeVersion schemeVersion;
    private LocalDateTime requestCreationDate;
    
    public UnderlyingAgreementValidationContext(SchemeVersion schemeVersion) {
        this.schemeVersion = schemeVersion;
    }
}
