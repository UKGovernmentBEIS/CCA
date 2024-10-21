package uk.gov.cca.api.user.sectoruser.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityUpdateDTO;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public final class SectorUserAuthorityUpdateWrapperDTO {

    @NotNull
    private List<SectorUserAuthorityUpdateDTO> sectorUserAuthorityUpdateDTOList = new ArrayList<>();
}
