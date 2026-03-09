package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnderlyingAgreementVariationOutcome {
	SUBMITTED,
    CANCELLED,
    COMPLETED,
}
