package uk.gov.cca.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorAuthorityInfoDTO {

    private String firstName;
    private String lastName;
    private String roleName;
    private String roleCode;
    private String contactType;
    private AuthorityStatus status;
    private LocalDateTime authorityCreationDate;
    private String userId;
}
