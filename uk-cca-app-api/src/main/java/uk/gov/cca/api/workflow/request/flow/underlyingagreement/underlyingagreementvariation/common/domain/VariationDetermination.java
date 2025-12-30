package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'ACCEPTED') == (#variationImpactsAgreement != null)}",
        message = "underlyingagreement.variation.review.variationDetermination.variationImpactsAgreement.typeMismatch")
@SpELExpression(expression = "{!((T(java.lang.Boolean).FALSE.equals(#variationImpactsAgreement)) && (#additionalInformation == null))}",
        message = "underlyingagreement.variation.review.variationDetermination.additionalInformation.typeMismatch")
public class VariationDetermination {

    private Boolean variationImpactsAgreement;

    @JsonUnwrapped
    @Valid
    @NotNull
    private Determination determination;
}
