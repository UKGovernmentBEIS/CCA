package uk.gov.cca.api.sectorassociation.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude
public class SectorAssociationSchemeDetailsUpdateDTO {

    @NotNull(message = "{sectorAssociationScheme.umbrellaAgreement.notNull}")
    private String umbrellaAgreementUuid;

    @NotNull
    private LocalDate umaDate;

    @NotNull
    @Size(max = 10000)
    private String sectorDefinition;
}
