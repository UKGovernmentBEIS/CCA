package uk.gov.cca.api.user.sectoruser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SectorUsersAuthoritiesInfoDTO {

    @Builder.Default
    private List<SectorUserAuthorityInfoDTO> authorities = new ArrayList<>();
    private boolean editable;
}
