package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.account.domain.*;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#isCompanyRegistrationNumber)  == (#companyRegistrationNumber != null)}", message = "target.unit.account.companyRegistrationNumber.notEmpty")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#isCompanyRegistrationNumber) == (#registrationNumberMissingReason != null)}", message = "target.unit.account.registrationNumberMissingReason.notEmpty")
public class TargetUnitAccountPayload {

    @NotBlank(message = "{target.unit.account.name.notEmpty}")
    @Size(max = 255, message = "{account.name.typeMismatch}")
    private String name;

    @NotNull(message = "{target.unit.account.emissionTradingScheme.notEmpty}")
    private CcaEmissionTradingScheme emissionTradingScheme;

    @NotNull(message = "{target.unit.account.competentAuthority.notEmpty}")
    private CompetentAuthorityEnum competentAuthority;

    @NotNull(message = "{target.unit.account.operator.type.notEmpty}")
    private TargetUnitAccountOperatorType operatorType;

    @NotNull(message = "{target.unit.account.is.company.registration.number.notEmpty}")
    private Boolean isCompanyRegistrationNumber;

    @Size(max = 255)
    private String companyRegistrationNumber;

    @Size(max = 255)
    private String registrationNumberMissingReason;

    @Size(max = 255)
    private String sicCode;

    private Long subsectorAssociationId;

    @Valid
    @NotNull(message = "{target.unit.account.address.notEmpty}")
    private AccountAddressDTO address;

    @Valid
    @NotNull(message = "{target.unit.account.responsible.person.notEmpty}")
    private TargetUnitAccountContactDTO responsiblePerson;

    @Valid
    @NotNull(message = "{target.unit.account.administrative.contact.details.notEmpty}")
    private TargetUnitAccountContactDTO administrativeContactDetails;
}
