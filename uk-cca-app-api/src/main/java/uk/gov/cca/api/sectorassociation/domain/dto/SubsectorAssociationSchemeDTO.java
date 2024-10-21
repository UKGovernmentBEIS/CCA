package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubsectorAssociationSchemeDTO {

    @NotNull(message = "{subsectorAssociationScheme.subsectorAssociation.notNull}")
    @Valid
    private SubsectorAssociationDTO subsectorAssociation;

    @NotNull(message = "{subsectorAssociationScheme.targetSet.notNull}")
    @Valid
    private TargetSetDTO targetSet;
}
