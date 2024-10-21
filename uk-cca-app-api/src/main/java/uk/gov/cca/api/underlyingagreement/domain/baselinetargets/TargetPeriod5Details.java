package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementSection;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#details != null)}", message = "underlyingagreement.tp5Details.details")
public class TargetPeriod5Details implements UnderlyingAgreementSection {

    @NotNull
    private Boolean exist;

    @Valid
    private TargetPeriod6Details details;
}
