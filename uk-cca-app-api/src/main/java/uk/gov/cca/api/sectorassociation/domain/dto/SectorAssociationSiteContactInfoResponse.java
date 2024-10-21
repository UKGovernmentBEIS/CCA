package uk.gov.cca.api.sectorassociation.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorAssociationSiteContactInfoResponse {

    private List<SectorAssociationSiteContactInfoDTO> siteContacts;

    private boolean editable;

    private Long totalItems;
}
