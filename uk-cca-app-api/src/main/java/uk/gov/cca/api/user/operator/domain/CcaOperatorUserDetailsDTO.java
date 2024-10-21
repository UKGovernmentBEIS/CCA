package uk.gov.cca.api.user.operator.domain;

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
public class CcaOperatorUserDetailsDTO extends CcaOperatorUserDTO {

    @NotNull(message = "{operator.contactType.notEmpty}")
    private ContactType contactType;

    private String organisationName;
}
