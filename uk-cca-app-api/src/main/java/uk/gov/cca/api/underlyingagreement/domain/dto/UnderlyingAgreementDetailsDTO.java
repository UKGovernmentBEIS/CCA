package uk.gov.cca.api.underlyingagreement.domain.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementDetailsDTO {

    private Long id;
    private Map<SchemeVersion, UnderlyingAgreementDocumentDetailsDTO> underlyingAgreementDocumentMap;
}
