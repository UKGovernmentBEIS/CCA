package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementSaveReviewRequestTaskActionPayload extends RequestTaskActionPayload {

	private UnderlyingAgreementReviewSavePayload underlyingAgreement;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
    
    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();
}
