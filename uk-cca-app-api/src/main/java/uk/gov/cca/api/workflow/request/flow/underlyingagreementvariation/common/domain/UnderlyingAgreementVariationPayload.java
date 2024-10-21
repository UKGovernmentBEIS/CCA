package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementVariationPayload {

    @NotNull
    @Valid
    private UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails;

    @NotNull
    @Valid
    private UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails;

    @NotNull
    @Valid
    @JsonUnwrapped
    private UnderlyingAgreement underlyingAgreement;
}
