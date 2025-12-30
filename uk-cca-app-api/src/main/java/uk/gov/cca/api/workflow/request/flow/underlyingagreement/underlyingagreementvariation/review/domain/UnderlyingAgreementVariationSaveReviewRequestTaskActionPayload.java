package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationReviewGroup;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationSaveReviewRequestTaskActionPayload extends RequestTaskActionPayload {

    private UnderlyingAgreementVariationReviewSavePayload underlyingAgreement;

    private VariationDetermination determination;

    @Builder.Default
    private Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> reviewGroupDecisions = new EnumMap<>(UnderlyingAgreementVariationReviewGroup.class);

    @Builder.Default
    private Map<String, UnderlyingAgreementVariationFacilityReviewDecision> facilitiesReviewGroupDecisions = new HashMap<>();

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();
}
