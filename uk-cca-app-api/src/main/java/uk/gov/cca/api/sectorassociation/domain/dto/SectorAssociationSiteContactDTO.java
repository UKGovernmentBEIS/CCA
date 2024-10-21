package uk.gov.cca.api.sectorassociation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorAssociationSiteContactDTO {

    private Long sectorAssociationId;

    private String userId;
}
