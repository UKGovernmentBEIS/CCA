package uk.gov.cca.api.authorization.ccaauth.sectoruser.domain;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.RoleCode;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SectorUserAuthorityUpdateDTO {

    @NotBlank
    private String userId;
    @RoleCode(roleType = SECTOR_USER)
    private String roleCode;
    @NotNull
    private AuthorityStatus authorityStatus;


}
