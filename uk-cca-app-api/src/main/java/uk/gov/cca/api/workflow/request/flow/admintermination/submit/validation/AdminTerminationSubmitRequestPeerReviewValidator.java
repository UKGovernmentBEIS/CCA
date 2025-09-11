package uk.gov.cca.api.workflow.request.flow.admintermination.submit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTerminationSubmitRequestPeerReviewValidator {

    private final AdminTerminationSubmitValidator adminTerminationSubmitValidator;
    private final CcaPeerReviewValidator peerReviewValidator;

    public void validate(final RequestTask requestTask,
                         final PeerReviewRequestTaskActionPayload payload,
                         final AppUser appUser) {

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        final AdminTerminationSubmitRequestTaskPayload taskPayload =
                (AdminTerminationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate submit data
        validationResults.add(adminTerminationSubmitValidator.validate(taskPayload));

        // Validate peer reviewer
        validationResults.add(peerReviewValidator
                .validate(requestTask, payload, appUser, CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_PEER_REVIEW));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_ADMIN_TERMINATION, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
