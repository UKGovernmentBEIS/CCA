package uk.gov.cca.api.workflow.request.flow.rfi.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RfiSubmitPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.flow.common.validation.WorkflowUsersValidator;

@Service
@RequiredArgsConstructor
public class SubmitRfiValidatorService {

    private final WorkflowUsersValidator workflowUsersValidator;

    public void validate(final RequestTask requestTask,
                         final RfiSubmitPayload rfiSubmitPayload,
                         final AppUser appUser) {

        final Long accountId = requestTask.getRequest().getAccountId();

        if (!workflowUsersValidator.areOperatorsValid(accountId, rfiSubmitPayload.getOperators(), appUser)
                || !workflowUsersValidator.isSignatoryValid(requestTask, rfiSubmitPayload.getSignatory())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}
