package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubsectorAssociationSchemeInfoDTO {

    @NotNull
    private Long id;

    private SubsectorAssociationDTO subsectorAssociation;
}
