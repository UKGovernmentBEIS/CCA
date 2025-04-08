package uk.gov.cca.api.sectorassociation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationInfoNameDTO {
    private Long id;
    private String acronym;
    private String name;
    private CompetentAuthorityEnum competentAuthority;
}
