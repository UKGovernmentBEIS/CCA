package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{!((T(java.lang.Boolean).FALSE.equals(#variationImpactsAgreement)) && (#additionalInformation == null))}",
        message = "underlyingagreement.variation.regulatorledsubmit.variationDetermination.additionalInformation.typeMismatch")
public class VariationRegulatorLedDetermination {

    @NotNull
    private Boolean variationImpactsAgreement;

    @Size(max = 10000)
    private String additionalInformation;

    @Builder.Default
    private Set<UUID> files = new HashSet<>();
}
