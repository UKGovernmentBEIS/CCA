package uk.gov.cca.api.underlyingagreement.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementDocumentDTO {

	private Long id;
	private int consolidationNumber;
    private String fileDocumentUuid;
    private LocalDateTime activationDate;
    private SchemeVersion schemeVersion;
}
