package uk.gov.cca.api.underlyingagreement.domain.dto;

import java.time.LocalDateTime;

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
    private int consolidationNumber;
    private String fileDocumentUuid;
    private LocalDateTime activationDate;
}
