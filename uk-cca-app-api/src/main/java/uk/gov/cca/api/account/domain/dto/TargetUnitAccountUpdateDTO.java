package uk.gov.cca.api.account.domain.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "!(#companyRegistrationNumber == null && #registrationNumberMissingReason == null)",
        message = "target.unit.account.companyRegistrationNumber.and.registrationNumberMissingReason.inconsistency")
public class TargetUnitAccountUpdateDTO {

    @Size(max = 255)
    @NotBlank
    private String operatorName;

    @NotNull
    private TargetUnitAccountOperatorType operatorType;

    @Size(max = 255)
    private String companyRegistrationNumber;

    @Size(max = 255)
    private String registrationNumberMissingReason;

    private Long subsectorAssociationId;

    @Valid
    @NotNull
    private AccountAddressDTO operatorAddress;

    @Valid
    @NotNull
    private TargetUnitAccountContactDTO responsiblePerson;

}
