package uk.gov.cca.api.authorization.ccaauth.sectoruser.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectorUserAuthoritiesDTO {

    private List<SectorUserAuthorityDTO> authorities;
    private boolean editable;
}
