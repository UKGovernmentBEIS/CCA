package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementApplySaveTargetUnitDetails {

    @Size(max = 255)
    @NotBlank
    private String operatorName;

    @Valid
    @NotNull
    private AccountAddressDTO operatorAddress;

    @Valid
    @NotNull
    private UnderlyingAgreementTargetUnitResponsiblePerson responsiblePersonDetails;
}
