package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementTargetUnitResponsiblePerson {

    @Size(max = 255)
    @NotBlank
    private String firstName;

    @Size(max = 255)
    @NotBlank
    private String lastName;

    @Size(max = 255)
    @Email
    @NotBlank
    private String email;

    @Valid
    @NotNull
    private AccountAddressDTO address;

}
