package uk.gov.cca.api.underlyingagreement.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementDTO {

	private Long id;
    private UnderlyingAgreementContainer underlyingAgreementContainer;
    private Long accountId;
    private List<UnderlyingAgreementDocumentDTO> underlyingAgreementDocuments;
}
