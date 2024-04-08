package uk.gov.cca.api.workflow.request.core.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;
import uk.gov.cca.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

import java.util.Set;

@Service
public class VerificationBodyExistenceRequestTaskActionValidator extends
    RequestTaskActionConflictBasedAbstractValidator {

    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.NO_VB_FOUND;
    }

    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of();
    }

    @Override
    public Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        return ObjectUtils.isEmpty(requestTask.getRequest().getVerificationBodyId())
            ? RequestTaskActionValidationResult.invalidResult(this.getErrorMessage())
            : RequestTaskActionValidationResult.validResult();
    }
}
