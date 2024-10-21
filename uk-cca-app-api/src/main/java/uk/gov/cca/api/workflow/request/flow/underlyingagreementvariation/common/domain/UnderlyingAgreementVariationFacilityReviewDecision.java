package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#type eq 'ACCEPTED' && #facilityStatus eq 'NEW') == (#changeStartDate != null)}",
        message = "underlyingagreement.variation.review.changeStartDate")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#changeStartDate) == (#startDate != null)}",
        message = "underlyingagreement.review.startDate")
public class UnderlyingAgreementVariationFacilityReviewDecision extends UnderlyingAgreementReviewDecision {

    private Boolean changeStartDate;

    private LocalDate startDate;

    @NotNull
    private FacilityStatus facilityStatus;
}
