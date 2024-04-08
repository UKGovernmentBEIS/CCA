package uk.gov.cca.api.authorization.verifier.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.authorization.core.domain.AuthorityStatus;
import uk.gov.cca.api.authorization.core.domain.dto.RoleCode;
import uk.gov.netz.api.common.domain.RoleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifierAuthorityUpdateDTO {

    @NotBlank
    private String userId;

    @NotNull
    private AuthorityStatus authorityStatus;

    @RoleCode(roleType = RoleType.VERIFIER)
    private String roleCode;
}
