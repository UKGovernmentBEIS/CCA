package uk.gov.cca.api.user.operator.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class CcaOperatorUserRegistrationWithCredentialsDTO extends CcaOperatorUserRegistrationDTO {
	@NotBlank(message = "{userAccount.password.notEmpty}")
	private String password;

}
