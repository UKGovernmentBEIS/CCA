package uk.gov.cca.api.authorization.ccaauth.operator.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OperatorAuthorityDTO extends UserAuthorityDTO {
	
    private ContactType contactType;
}
