package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UnderlyingAgreementSubmitRequestTaskPayload extends UnderlyingAgreementRequestTaskPayload {
}
