package uk.gov.cca.api.underlyingagreement.domain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementDetailsDTO {

    private Long id;
    private LocalDate activationDate;
    private FileInfoDTO fileDocument;
}
