package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The sector association DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationDTO {

    @NotNull(message = "{sectorAssociation.sectorAssociationContact.notNull}")
    @Valid
    private SectorAssociationContactDTO sectorAssociationContact;

    @NotNull(message = "{sectorAssociation.sectorAssociationDetails.notNull}")
    @Valid
    private SectorAssociationDetailsDTO sectorAssociationDetails;
}
