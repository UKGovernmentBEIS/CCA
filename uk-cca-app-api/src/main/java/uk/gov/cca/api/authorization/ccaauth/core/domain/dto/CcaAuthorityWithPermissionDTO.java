package uk.gov.cca.api.authorization.ccaauth.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CcaAuthorityWithPermissionDTO {

    private Long id;

    private String code;

    private String status;

    private Long accountId;

    private String competentAuthority;

    private Long verificationBodyId;

    private Long sectorAssociationId;

    private String permissions;
}
