package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload extends RequestTaskActionPayload {

    private Determination determination;
}
