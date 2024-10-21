package uk.gov.cca.api.authorization.ccaauth.sectoruser.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SectorUserDeletionEvent {

    private String userId;
    private Long sectorAssociationId;
    private boolean existCcaAuthoritiesOnOtherSectorAssociations;
}
