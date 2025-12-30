package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationSaveReviewDeterminationRequestTaskActionPayload extends RequestTaskActionPayload {

    private VariationDetermination determination;

    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();
}
