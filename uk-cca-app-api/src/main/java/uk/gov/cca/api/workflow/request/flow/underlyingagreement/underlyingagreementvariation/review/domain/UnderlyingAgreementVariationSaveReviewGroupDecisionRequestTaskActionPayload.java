package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationSaveReviewGroupDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private UnderlyingAgreementVariationReviewGroup group;

    private VariationDetermination determination;

    @NotNull
    @Valid
    private UnderlyingAgreementReviewDecision decision;

    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();
}
