package uk.gov.cca.api.authorization.ccaauth.core.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CcaAuthorityInfoDTO {
	
    private Long id;
    private String userId;
    private AuthorityStatus authorityStatus;
    private LocalDateTime creationDate;
    private Long accountId;
    private Long sectorAssociationId;
    private String code;
    private Long verificationBodyId;
	
}
