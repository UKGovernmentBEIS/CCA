package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.account.domain.enumeration.AccountStatus;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

@Service
public class NonComplianceCreateValidator extends RequestCreateAccountRelatedValidator {

    public NonComplianceCreateValidator(RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
    }

    @Override
    public Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(TargetUnitAccountStatus.LIVE, TargetUnitAccountStatus.TERMINATED);
    }

    @Override
    public Set<String> getMutuallyExclusiveRequests() {
        return Set.of();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.NON_COMPLIANCE;
    }
}
