package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.account.domain.enumeration.AccountStatus;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@Service
public class UnderlyingAgreementVariationCreateValidator extends RequestCreateAccountRelatedValidator {

	public UnderlyingAgreementVariationCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
    }

    @Override
    public Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(TargetUnitAccountStatus.LIVE);
    }

    @Override
    public Set<String> getMutuallyExclusiveRequests() {
        return Set.of(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION); 
    }

	@Override
	public String getRequestType() {
		return CcaRequestType.UNDERLYING_AGREEMENT_VARIATION;
	}
}
