package uk.gov.cca.api.user.sectoruser.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.user.core.domain.dto.validation.Password;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SectorUserRegistrationWithCredentialsDTO extends SectorUserRegistrationDTO {

    @NotBlank(message = "{userAccount.password.notEmpty}")
    @Password(message = "{userAccount.password.typeMismatch}")
    private String password;
}
