package uk.gov.cca.api.user.sectoruser.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SectorUserAuthorityDetailsDTO extends SectorUserDTO {

    @NotNull(message = "{sectorUser.contactType.notEmpty}")
    private ContactType contactType;

    private String organisationName;
}
