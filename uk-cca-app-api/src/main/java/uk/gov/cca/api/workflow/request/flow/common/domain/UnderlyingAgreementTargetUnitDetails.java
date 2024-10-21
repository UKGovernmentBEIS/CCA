package uk.gov.cca.api.workflow.request.flow.common.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#isCompanyRegistrationNumber)  == (#companyRegistrationNumber != null)}", message = "target.unit.account.companyRegistrationNumber.notEmpty")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#isCompanyRegistrationNumber) == (#registrationNumberMissingReason != null)}", message = "target.unit.account.registrationNumberMissingReason.notEmpty")
@SpELExpression(expression = "{((#subsectorAssociationId != null) == (#subsectorAssociationName != null)) || " +
        "((#subsectorAssociationId == null) == (#subsectorAssociationName == null))}", message = "target.unit.account.subsectorAssociationName.and.subsectorAssociationId.inconsistency")
public class UnderlyingAgreementTargetUnitDetails {

    @Size(max = 255)
    @NotBlank
    private String operatorName;

    @Valid
    @NotNull
    private AccountAddressDTO operatorAddress;

    @Valid
    @NotNull
    private UnderlyingAgreementTargetUnitResponsiblePerson responsiblePersonDetails;

    @NotNull
    private TargetUnitAccountOperatorType operatorType;

    @NotNull
    private Boolean isCompanyRegistrationNumber;

    @Size(max = 255)
    private String companyRegistrationNumber;

    @Size(max = 255)
    private String registrationNumberMissingReason;

    //Timeline Event specific field
    @Size(max = 255)
    private String subsectorAssociationName;

    private Long subsectorAssociationId;
}
