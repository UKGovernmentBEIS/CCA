package uk.gov.cca.api.web.orchestrator.sectorassociation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;

/**
 * The sector association DTO.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationResponseDTO {

    @NotNull(message = "{sectorAssociation.sectorAssociationContact.notNull}")
    private SectorAssociationContactDTO sectorAssociationContact;

    @NotNull(message = "{sectorAssociation.sectorAssociationDetails.notNull}")
    private SectorAssociationDetailsResponseDTO sectorAssociationDetails;

    private boolean editable;
}
