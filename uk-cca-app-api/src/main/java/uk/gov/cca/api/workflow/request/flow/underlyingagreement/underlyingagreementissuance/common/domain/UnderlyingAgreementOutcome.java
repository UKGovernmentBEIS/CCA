package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnderlyingAgreementOutcome {
	
    SUBMITTED,
    CANCELLED;
}
