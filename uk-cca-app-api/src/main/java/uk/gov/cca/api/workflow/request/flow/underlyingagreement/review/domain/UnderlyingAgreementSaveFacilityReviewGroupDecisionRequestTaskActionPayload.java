package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

    private Determination determination;

    @NotNull
    private String group;

    @NotNull
    @Valid
    private UnderlyingAgreementFacilityReviewDecision decision;

    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();
}
