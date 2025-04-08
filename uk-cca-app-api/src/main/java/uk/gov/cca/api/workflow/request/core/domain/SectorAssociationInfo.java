package uk.gov.cca.api.workflow.request.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationInfo {
    private Long id;
    private String acronym;
    private String name;
    private CompetentAuthorityEnum competentAuthority;
}
