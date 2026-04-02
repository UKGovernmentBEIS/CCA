package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewValidator;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class NoticeOfIntentSubmitRequestPeerReviewValidator {

    private final NoticeOfIntentSubmitValidator noticeOfIntentSubmitValidator;
    private final CcaPeerReviewValidator peerReviewValidator;

    public void validate(final RequestTask requestTask,
                         final PeerReviewRequestTaskActionPayload payload,
                         final AppUser appUser) {

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload taskPayload =
                (NonComplianceNoticeOfIntentSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate submit data
        validationResults.add(noticeOfIntentSubmitValidator.validate(taskPayload));

        // Validate peer reviewer
        validationResults.add(peerReviewValidator
                .validate(requestTask, payload, appUser, CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_NON_COMPLIANCE, ValidatorHelper.extractViolations(validationResults));
        }
    }

}
