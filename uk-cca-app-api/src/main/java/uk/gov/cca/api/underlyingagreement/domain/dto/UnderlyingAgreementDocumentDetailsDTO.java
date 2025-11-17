package uk.gov.cca.api.underlyingagreement.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementDocumentDetailsDTO {

	private LocalDate activationDate;
    private LocalDate terminatedDate;
    private FileInfoDTO fileDocument;
}
