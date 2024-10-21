package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.account.domain.enumeration.AccountStatus;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@Service
public class AdminTerminationCreateValidator extends RequestCreateAccountRelatedValidator {

	public AdminTerminationCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
    }

    @Override
    public Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(TargetUnitAccountStatus.LIVE);
    }

    @Override
    public Set<String> getMutuallyExclusiveRequests() {
        return Set.of(CcaRequestType.ADMIN_TERMINATION, CcaRequestType.UNDERLYING_AGREEMENT); 
    }

	@Override
	public String getRequestType() {
		return CcaRequestType.ADMIN_TERMINATION;
	}
}
