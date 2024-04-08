package uk.gov.cca.api.workflow.request.flow.common.service;


import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.enumeration.AccountStatus;
import uk.gov.cca.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Set;

@RequiredArgsConstructor
public abstract class RequestCreateAccountRelatedValidator implements RequestCreateByAccountValidator {

    private final RequestCreateValidatorService requestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId) {
        return requestCreateValidatorService
                .validate(accountId, this.getApplicableAccountStatuses(), this.getMutuallyExclusiveRequests());
    }

    protected abstract Set<AccountStatus> getApplicableAccountStatuses();

    protected abstract Set<RequestType> getMutuallyExclusiveRequests();
}
