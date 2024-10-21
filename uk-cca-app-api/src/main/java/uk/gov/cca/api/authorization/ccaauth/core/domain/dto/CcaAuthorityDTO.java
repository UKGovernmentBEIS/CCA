package uk.gov.cca.api.authorization.ccaauth.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityDTO;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CcaAuthorityDTO extends AuthorityDTO {

    private Long sectorAssociationId;
}
