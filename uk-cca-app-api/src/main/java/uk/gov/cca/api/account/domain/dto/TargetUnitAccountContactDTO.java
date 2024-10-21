package uk.gov.cca.api.account.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.common.validation.PhoneNumberIntegrity;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TargetUnitAccountContactDTO {

    @NotBlank
    @Size(max = 255)
    @Email
    private String email;

    @NotBlank
    @Size(max = 255)
    private String firstName;

    @NotBlank
    @Size(max = 255)
    private String lastName;

    @Size(max = 255)
    private String jobTitle;

    @Valid
    @NotNull
    private AccountAddressDTO address;

    @Valid
    @PhoneNumberIntegrity(message = "{target.unit.account.responsible.person.phoneNumber.dto}")
    private PhoneNumberDTO phoneNumber;
}
