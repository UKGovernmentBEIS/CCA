package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementReviewGroup;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

	@NotNull
    private UnderlyingAgreementReviewGroup group;

    @NotNull
    @Valid
    private UnderlyingAgreementReviewDecision decision;

    private Determination determination;
    
    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();
}
