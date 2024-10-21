package uk.gov.cca.api.authorization.ccaauth.sectoruser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorUserAssignedSubResource {

	private Long sectorAssociationId;
    private String resourceSubType;
}
