package uk.gov.cca.api.account.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.common.validation.PhoneNumberIntegrity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTargetUnitAccountResponsiblePersonDTO {

    @Size(max = 255)
    private String jobTitle;

    @Valid
    @PhoneNumberIntegrity(message = "{target.unit.account.responsible.person.phoneNumber.dto}")
    private PhoneNumberDTO phoneNumber;
}
