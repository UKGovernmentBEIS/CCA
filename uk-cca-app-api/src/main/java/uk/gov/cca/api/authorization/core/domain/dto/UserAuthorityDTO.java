package uk.gov.cca.api.authorization.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.authorization.core.domain.AuthorityStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthorityDTO {
    private String userId;
    private String roleName;
    private String roleCode;
    private AuthorityStatus authorityStatus;
    private LocalDateTime authorityCreationDate;
}
